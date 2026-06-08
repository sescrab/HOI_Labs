package org.example.d6.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Boarding {
    @JsonProperty("boarding_no")
    private final Integer boardingNo;
    @JsonProperty("seat_no")
    private final String seatNo;
    @JsonProperty("boarding_time")
    private final String boardingTime;
}
