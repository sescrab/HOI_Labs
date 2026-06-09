package org.example.d6.repositories;

import jakarta.persistence.LockModeType;
import org.example.d6.entities.Segments;
import org.example.d6.entities.SegmentsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface SegmentsRepository extends JpaRepository<Segments, SegmentsId> {
    @Query("SELECT COUNT(s) FROM Segments s WHERE s.flight.flightId = :flightId")
    int countByFlightId(@Param("flightId") Integer flightId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Segments s WHERE s.flight.flightId = :flightId")
    List<Segments> findAllAndLockByFlightId(@Param("flightId") Integer flightId);
}