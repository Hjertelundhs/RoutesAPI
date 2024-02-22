package com.hjertelundh.routes.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import com.hjertelundh.routes.exception.ExternalApiException;
import com.hjertelundh.routes.exception.ParsingException;
import com.hjertelundh.routes.provider.dto.JourneyPatternPointOnLineDto;
import com.hjertelundh.routes.provider.dto.LineDto;
import com.hjertelundh.routes.provider.dto.StopPointDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class SLProvider {

    private static final int TIME_OUT = 30_000;

    private final String journeyPatternPointOnLineUrl;
    private final String lineUrl;
    private final String stopPointUrl;
    private final ObjectMapper objectMapper;

    public SLProvider(
            @Value("${routes.journey-pattern-point-on-line-url}") String journeyPatternPointOnLineUrl,
            @Value("${routes.line-url}") String lineUrl,
            @Value("${routes.stop-point-url}") String stopPointUrl,
            ObjectMapper objectMapper) {
        this.journeyPatternPointOnLineUrl = journeyPatternPointOnLineUrl;
        this.lineUrl = lineUrl;
        this.stopPointUrl = stopPointUrl;
        this.objectMapper = objectMapper;
    }

    @SneakyThrows
    public List<JourneyPatternPointOnLineDto> getJourneyPaths() {
        var response = sendGetRequest(journeyPatternPointOnLineUrl);
        return objectMapper.readValue(
                objectMapper.writeValueAsString(response), new TypeReference<>() {});
    }

    @SneakyThrows
    public List<LineDto> getLinePaths() {
        var response = sendGetRequest(lineUrl);

        var lineDtos =
                objectMapper.readValue(
                        objectMapper.writeValueAsString(response), new TypeReference<List<LineDto>>() {});

        log.info("Filtering for busses only....");
        List<LineDto> busLines = new ArrayList<>();
        for (LineDto lineDto : lineDtos) {
            if (lineDto.getDefaultTransportModeCode() == LineDto.DefaultTransportModeCode.BUS) {
                busLines.add(lineDto);
            }
        }
        log.info("Found {} bus LineDtos", busLines.size());
        return busLines;
    }

    @SneakyThrows
    public Map<Integer, StopPointDto> getStopPoints() {
        var response = sendGetRequest(stopPointUrl);

        var stopPointDtos =
                objectMapper.readValue(
                        objectMapper.writeValueAsString(response), new TypeReference<List<StopPointDto>>() {});

        log.info("Filtering for bus terminals only....");
        var stopPointMap = new HashMap<Integer, StopPointDto>();
        for (StopPointDto stopPoint : stopPointDtos) {
            if (stopPoint.getStopAreaTypeCode() == StopPointDto.StopAreaTypeCode.BUSTERM) {
                stopPointMap.put(stopPoint.getStopPointNumber(), stopPoint);
            }
        }
        log.info("Found {} bus terminal StopPoints", stopPointMap.size());
        return stopPointMap;
    }

    @SuppressWarnings("unchecked")
    private Object sendGetRequest(String path) {
        var endpointName = path.substring(path.lastIndexOf("=") + 1);
        try {
            var getRequest =
                    HttpRequest.newBuilder()
                            .uri(new URI(path))
                            .header("Content-Type", "application/json")
                            .header("Accept-Encoding", "gzip, deflate")
                            .GET()
                            .timeout(Duration.ofMillis(TIME_OUT))
                            .build();
            log.info("Sending request for " + endpointName);
            var response =
                    HttpClient.newHttpClient().send(getRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                log.error("Unexpected http status code: {}", response.statusCode());
                throw new ExternalApiException();
            }

            log.info("Returning response for " + endpointName);
            var payload = stringPayloadToMap(response.body());
            var responseData = (Map<String, Object>) payload.get("ResponseData");
            return responseData.get("Result");
        } catch (URISyntaxException | IOException | InterruptedException e) {
            log.error("Failed parsing payload due to: {}", e.getMessage());
            throw new ParsingException();
        }
    }

    private Map<String, Object> stringPayloadToMap(String payload) throws JsonProcessingException {
        return objectMapper.readValue(payload, new TypeReference<HashMap<String, Object>>() {});
    }
}