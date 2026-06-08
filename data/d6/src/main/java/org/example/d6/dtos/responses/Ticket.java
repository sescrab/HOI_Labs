package org.example.d6.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class Ticket {
    @JsonProperty("ticket_no")
    private final String ticketNo;
    List<Segment> segments;
}
