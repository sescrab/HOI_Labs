package org.example.d6.repositories;

import org.example.d6.entities.Flights;
import org.example.d6.entities.PricingRules;
import org.example.d6.entities.Segments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FlightsRepository extends JpaRepository<Flights, Integer> {
    Optional<Flights> findByFlightId(Integer flightId);
}