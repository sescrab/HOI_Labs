package org.example.d6.repositories;

import org.example.d6.entities.BoardingPasses;
import org.example.d6.entities.BoardingPassesId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface BoardingPassesRepository extends JpaRepository<BoardingPasses, BoardingPassesId> {
    @Query("SELECT MAX(b.boardingNo) FROM BoardingPasses b WHERE b.id.flightId = :flightId")
    Optional<Integer> findMaxBoardingNoByFlightId(@Param("flightId") Integer flightId);
}