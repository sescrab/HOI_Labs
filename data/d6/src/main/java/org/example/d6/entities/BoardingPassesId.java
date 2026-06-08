package org.example.d6.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardingPassesId implements Serializable {

    @Column(name = "ticket_no")
    private String ticketNo;

    @Column(name = "flight_id")
    private Integer flightId;
}