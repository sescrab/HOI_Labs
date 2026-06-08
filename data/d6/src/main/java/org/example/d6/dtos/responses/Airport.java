package org.example.d6.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Airport {
    @JsonProperty("airport_code")
    private final String airportCode;
    @JsonProperty("airport_name")
    private final String airportName;
    private final String city;
}
