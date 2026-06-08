package org.example.d6.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class Booking {
    @JsonProperty("book_ref")
    private final String bookRef;
    Ticket ticket;
}
