package org.example.d6.repositories;

import org.example.d6.entities.Seats;
import org.example.d6.entities.SeatsId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SeatsRepository extends JpaRepository<Seats, SeatsId> {
    Optional<Seats> findById(SeatsId id);
}