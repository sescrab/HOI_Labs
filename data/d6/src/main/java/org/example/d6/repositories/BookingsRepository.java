package org.example.d6.repositories;

import org.example.d6.entities.Bookings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingsRepository extends JpaRepository<Bookings, String> {
}
