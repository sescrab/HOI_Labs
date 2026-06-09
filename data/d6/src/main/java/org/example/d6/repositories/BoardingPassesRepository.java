package org.example.d6.repositories;

import jakarta.persistence.LockModeType;
import org.example.d6.entities.BoardingPasses;
import org.example.d6.entities.BoardingPassesId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BoardingPassesRepository extends JpaRepository<BoardingPasses, BoardingPassesId> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM BoardingPasses b WHERE b.id.flightId = :flightId AND b.seatNo = :seatNo")
    Optional<BoardingPasses> findAndLockByFlightIdAndSeatNo(@Param("flightId") Integer flightId,
                                                            @Param("seatNo") String seatNo);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM BoardingPasses b WHERE b.id.flightId = :flightId")
    List<BoardingPasses> findAllAndLockByFlightId(@Param("flightId") Integer flightId);
}