package org.example.d6.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.util.List;

@Getter
public class CheckinRequest {
    @JsonProperty("ticket_no")
    @NotBlank
    private String ticketNo;
    @JsonProperty("flight_id")
    private Integer flightId;
    @JsonProperty("seat_no")
    @NotBlank
    private String seatNo;
}