package com.hjertelundh.routes.provider.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StopPointDto {

    // Unique identification number for a stop point. This is also the JourneyPatternPointNumber for
    // JourneyPatternPointOnLineDto
    @JsonProperty(value = "StopPointNumber")
    private int stopPointNumber;

    // Name of the stop point
    @JsonProperty(value = "StopPointName")
    private String stopPointName;

    // Usage of the stop
    @JsonProperty(value = "StopAreaTypeCode")
    private StopAreaTypeCode stopAreaTypeCode;

    // Number for StopArea
    @JsonProperty(value = "StopAreaNumber")
    private int stopAreaNumber;

    // Coordinate in WGS84 format
    @JsonProperty(value = "LocationNorthingCoordinate")
    private double locationNorthingCoordinate;

    // Coordinate in WGS84 format
    @JsonProperty(value = "LocationEastingCoordinate")
    private double locationEastingCoordinate;

    // Taxi zone. A, B, or C
    @JsonProperty(value = "ZoneShortName")
    private String zoneShortName;

    // Last modified
    @JsonProperty(value = "LastModifiedUtcDateTime")
    private String lastModifiedUtcDateTime;

    // Effective from date
    @JsonProperty(value = "ExistsFromDate")
    private String existsFromDate;

    // Documentation for this is WRONG: BUSSTERM is actually BUSTERM
    public enum StopAreaTypeCode {
        BUSTERM,
        TRAMSTN,
        METROSTN,
        RAILWSTN,
        SHIPBER,
        FERRYBER
    }
}