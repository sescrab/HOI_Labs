package org.example.d6.entities;

import jakarta.persistence.*;
import lombok.*;
import org.example.d6.services.IntegerArrayConverter;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;

@Entity
@Table(name = "routes", schema = "bookings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Routes {

    @Id
    @Column(name = "route_no", nullable = false)
    private String routeNo;

    @ManyToOne
    @JoinColumn(name = "departure_airport", nullable = false)
    private AirportsData departureAirport;

    @ManyToOne
    @JoinColumn(name = "arrival_airport", nullable = false)
    private AirportsData arrivalAirport;

    @Convert(converter = IntegerArrayConverter.class)
    @Column(name = "days_of_week", nullable = false, columnDefinition = "integer[]")
    private Integer[] daysOfWeek;

    @Column(name = "scheduled_time", nullable = false)
    private LocalTime scheduledTime;
}