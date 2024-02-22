package com.hjertelundh.routes.provider.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class JourneyPatternPointOnLineDto {

    // Unique identification number for line
    @JsonProperty(value = "LineNumber")
    private int lineNumber;

    // Line direction
    @JsonProperty(value = "DirectionCode")
    private int directionCode;

    // Unique identification number for stop point. This is also the StopPointNumber for StopPointDto
    @JsonProperty(value = "JourneyPatternPointNumber")
    private int journeyPatternPointNumber;

    // Last Modified
    @JsonProperty(value = "LastModifiedUtcDateTime")
    private String lastModifiedUtcDateTime;

    // Valid from date
    @JsonProperty(value = "ExistsFromDate")
    private String existsFromDate;
}