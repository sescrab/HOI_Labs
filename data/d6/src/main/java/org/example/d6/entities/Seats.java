package org.example.d6.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seats", schema = "bookings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Seats {

    @EmbeddedId
    private SeatsId id;

    @Enumerated(EnumType.STRING)
    @Column(name = "fare_conditions", nullable = false, length = 20)
    private Segments.FareCondition fareConditions;
}