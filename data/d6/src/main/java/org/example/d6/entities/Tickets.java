package org.example.d6.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tickets", schema = "bookings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tickets {

    @Id
    @Column(name = "ticket_no", nullable = false)
    private String ticketNo;

    @ManyToOne
    @JoinColumn(name = "book_ref", nullable = false)
    private Bookings booking;

    @Column(name = "passenger_id", nullable = false)
    private String passengerId;

    @Column(name = "passenger_name", nullable = false)
    private String passengerName;

    @Column(name = "outbound", nullable = false)
    private boolean outbound;
}