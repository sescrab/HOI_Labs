package org.example.d6.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "segments", schema = "bookings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Segments {
    public enum FareCondition {
        Economy, Comfort, Business
    }
    @EmbeddedId
    private SegmentsId id;

    @ManyToOne
    @MapsId("ticketNo")
    @JoinColumn(name = "ticket_no", nullable = false)
    private Tickets ticket;

    @ManyToOne
    @MapsId("flightId")
    @JoinColumn(name = "flight_id", nullable = false)
    private Flights flight;

    @Enumerated(EnumType.STRING)
    @Column(name = "fare_conditions", nullable = false)
    private FareCondition fareConditions;

    @Column(name = "price", nullable = false, columnDefinition = "numeric(10,2)")
    private Double price;
}