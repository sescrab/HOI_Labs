package org.example.d6.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class Flight {
    @JsonProperty("flight_id")
    private final Integer flightId;
    @JsonProperty("scheduled_departure_time")
    private final String scheduledDepartureTime;
    @JsonProperty("scheduled_arrival_time")
    private final String scheduledArrivalTime;
    private final Double price;
}
