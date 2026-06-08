package org.example.d6.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class BookingRequest {
    @JsonProperty("passenger_id")
    @NotBlank
    private String passengerId;
    @JsonProperty("passenger_name")
    @NotBlank
    private String passengerName;
    @JsonProperty("booking_class")
    @NotBlank
    private String bookingClass;
    @JsonProperty("flight_ids")
    private List<Integer> flightIds;
}