package org.example.d6.repositories;

import org.example.d6.entities.Tickets;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketsRepository extends JpaRepository<Tickets, String> {
}