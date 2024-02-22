package com.hjertelundh.routes.dto;
import java.util.Set;

public record BusLineDto(int busLine, Set<BusStopDto> busStops) {}