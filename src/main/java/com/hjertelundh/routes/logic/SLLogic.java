package com.hjertelundh.routes.logic;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.hjertelundh.routes.dto.BusLineDto;
import com.hjertelundh.routes.dto.BusStopDto;
import com.hjertelundh.routes.exception.AggregationException;
import com.hjertelundh.routes.exception.ExternalApiException;
import com.hjertelundh.routes.exception.ParsingException;
import com.hjertelundh.routes.provider.SLProvider;
import com.hjertelundh.routes.provider.dto.JourneyPatternPointOnLineDto;
import com.hjertelundh.routes.provider.dto.LineDto;
import com.hjertelundh.routes.provider.dto.StopPointDto;


@Slf4j
@Component
@AllArgsConstructor
public class SLLogic {

    private final SLProvider slProvider;

    @SneakyThrows
    public List<BusLineDto> aggregateLogic() {
        // fetch the data from SL API asynchronously
        var journeyPathFuture = callExternalApiAsync(slProvider::getJourneyPaths);
        var linePathsFuture = callExternalApiAsync(slProvider::getLinePaths);
        var stopPointsFuture = callExternalApiAsync(slProvider::getStopPoints);

        // wait until all responses are done
        var combinedFuture =
                CompletableFuture.allOf(journeyPathFuture, linePathsFuture, stopPointsFuture);

        var busLineDetailFutures =
                combinedFuture.thenApply(
                        data -> {
                            try {
                                var allJourneyPatterns = journeyPathFuture.get();
                                var busLines = linePathsFuture.get();
                                var stopPointMap = stopPointsFuture.get();
                                // aggregate everything
                                return getBusLineStopDetails(allJourneyPatterns, busLines, stopPointMap);
                            } catch (ExternalApiException | ParsingException e) {
                                throw e;
                            } catch (Exception e) {
                                log.error("Failed to aggregate data due to: {}", e.getMessage());
                                throw new AggregationException();
                            }
                        });

        // wait until the aggregated data is done
        combinedFuture.join();

        // sort it by the top 10 routes with the most stops
        var topTenLargestRoutes = getTopTenLargestRoutes(busLineDetailFutures.get());

            for (BusLineDto busLineDto : topTenLargestRoutes) {
              log.info(
                  String.format(
                      "Line %d has %d number of stops: %s",
                      busLineDto.busLine(),
                      busLineDto.busStops().size(),
                      busLineDto.busStops()));
            }
        return topTenLargestRoutes;
    }

    private static <T> CompletableFuture<T> callExternalApiAsync(Callable<T> callable) {
        return CompletableFuture.supplyAsync(
                () -> {
                    try {
                        return callable.call();
                    } catch (Exception e) {
                        throw new ExternalApiException(e);
                    }
                });
    }

    private static Map<Integer, Set<BusStopDto>> getBusLineStopDetails(
            List<JourneyPatternPointOnLineDto> allJourneyPatterns,
            List<LineDto> busLines,
            Map<Integer, StopPointDto> stopPointMap) {

        // add the bus line numbers in a set to speed up the lookup for below
        var busLineNumbers = new HashSet<Integer>();
        for (LineDto buseLine : busLines) {
            busLineNumbers.add(buseLine.getLineNumber());
        }

        // keep track of the amount of unique bus stop for each bus line
        // key = bus line, value = set of the unique bus stops (BusStopDetailDto)
        var busLineJourneys = new HashMap<Integer, Set<BusStopDto>>();
        for (JourneyPatternPointOnLineDto journeyPatternPointOnLineDto : allJourneyPatterns) {
            var lineNumber = journeyPatternPointOnLineDto.getLineNumber();
            // verify that this line number indeed belongs to a bus route
            if (busLineNumbers.contains(lineNumber)) {
                var stopId = journeyPatternPointOnLineDto.getJourneyPatternPointNumber();
                var stopPointDto = stopPointMap.get(stopId);
                if (stopPointDto == null) {
                    continue;
                }
                var busStopDto = new BusStopDto(stopId, stopPointDto.getStopPointName());
                if (busLineJourneys.containsKey(lineNumber)) {
                    // add the BusStopDetailDto for this line number to the existing list
                    busLineJourneys.get(lineNumber).add(busStopDto);
                } else {
                    // create a new list of BusStopDetailDtos for this line number
                    var busStopDtos = new HashSet<BusStopDto>();
                    busStopDtos.add(busStopDto);
                    busLineJourneys.put(lineNumber, busStopDtos);
                }
            }
        }
        return busLineJourneys;
    }

    private List<BusLineDto> getTopTenLargestRoutes(
            Map<Integer, Set<BusStopDto>> busLineJourneys) {
        log.info("Finding the top 10 largest routes...");
        return busLineJourneys.entrySet().stream()
                .sorted(Comparator.comparingInt(entry -> -entry.getValue().size()))
                .limit(10)
                .map(entry -> new BusLineDto(entry.getKey(), entry.getValue()))
                .toList();
    }
}