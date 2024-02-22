package com.hjertelundh.routes.provider.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LineDto {

    // Unique identification number for line
    @JsonProperty(value = "LineNumber")
    private int lineNumber;

    // Line designation
    @JsonProperty(value = "LineDesignation")
    private String lineDesignation;

    // Grouping of lines for presentation
    @JsonProperty(value = "DefaultTransportMode")
    private String defaultTransportMode;

    // Traffic law
    @JsonProperty(value = "DefaultTransportModeCode")
    private DefaultTransportModeCode defaultTransportModeCode;

    // Last Modified
    @JsonProperty(value = "LastModifiedUtcDateTime")
    private String lastModifiedUtcDateTime;

    // Valid from date
    @JsonProperty(value = "ExistsFromDate")
    private String existsFromDate;

    public enum DefaultTransportModeCode {
        BUS,
        METRO,
        TRAM,
        TRAIN,
        SHIP,
        FERRY
    }
}