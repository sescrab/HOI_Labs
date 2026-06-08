package org.example.d6.repositories;

import org.example.d6.entities.Routes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface RoutesRepository extends JpaRepository<Routes, String> {
    @Query(value = "SELECT * FROM bookings.routes WHERE route_no = :routeNo AND validity @> CAST(:dateTime AS timestamptz)", nativeQuery = true)
    Optional<Routes> findByRouteNoAndValidityContains(@Param("routeNo") String routeNo, @Param("dateTime") OffsetDateTime dateTime);


    @Query(value = "SELECT * FROM bookings.routes WHERE departure_airport = :airportCode AND validity @> bookings.now()", nativeQuery = true)
    List<Routes> findByDepartureAirport_AirportCode(@Param("airportCode") String airportCode);

    @Query(value = "SELECT * FROM bookings.routes WHERE arrival_airport = :airportCode AND validity @> bookings.now()", nativeQuery = true)
    List<Routes> findByArrivalAirport_AirportCode(@Param("airportCode") String airportCode);
}