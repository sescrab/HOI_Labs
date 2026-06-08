package org.example.d6.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RouteSearched {
    @JsonProperty("flights_amount")
    private final Integer flightsAmount;
    @JsonProperty("total_price")
    private final Double totalPrice;
    private final List<Flight> flights;
}
