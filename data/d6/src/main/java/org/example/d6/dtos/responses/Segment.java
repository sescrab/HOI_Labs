package org.example.d6.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Segment {
    @JsonProperty("flight_id")
    private final Integer flightId;
    @JsonProperty("fare_conditions")
    private final String farConditions;
}
