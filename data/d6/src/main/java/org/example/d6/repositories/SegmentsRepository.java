package org.example.d6.repositories;

import org.example.d6.entities.Segments;
import org.example.d6.entities.SegmentsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.Optional;

public interface SegmentsRepository extends JpaRepository<Segments, SegmentsId> {
    @Query("SELECT MIN(s.price) FROM Segments s WHERE s.flight.flightId = :flightId AND s.fareConditions = :fareClass")
    Optional<Double> findMinPriceByFlightAndClass(@Param("flightId") Integer flightId, @Param("fareClass") String fareClass);
}