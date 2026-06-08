package org.example.d6.entities;

import jakarta.persistence.*;
import lombok.*;
import org.example.d6.entities.BoardingPassesId;
import org.example.d6.entities.Segments;

import java.time.OffsetDateTime;

@Entity
@Table(name = "boarding_passes", schema = "bookings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardingPasses {

    @EmbeddedId
    private BoardingPassesId id;

    @Column(name = "seat_no", nullable = false)
    private String seatNo;

    @Column(name = "boarding_no")
    private Integer boardingNo;

    @Column(name = "boarding_time")
    private OffsetDateTime boardingTime;
}