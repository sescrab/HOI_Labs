package org.example.d6.repositories;

import org.example.d6.entities.AirportsData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AirportsDataRepository extends JpaRepository<AirportsData, String> {

    List<AirportsData> findAll();
}