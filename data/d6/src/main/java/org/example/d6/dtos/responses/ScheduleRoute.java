package org.example.d6.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ScheduleRoute {
    @JsonProperty("days_of_week")
    private final List<Integer> daysOfWeek;
    private final String time;
    @JsonProperty("flight_no")
    private final String flightNo;
    private final String origin;
    private final String destination;
}
