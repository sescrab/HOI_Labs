package org.example.d6.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.example.d6.dtos.requests.BookingRequest;
import org.example.d6.dtos.requests.CheckinRequest;
import org.example.d6.dtos.responses.*;
import org.example.d6.services.FlightsServices;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Book;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class FlightsController {

    private final FlightsServices flightsServices;


    @GetMapping("/cities")
    @ResponseStatus(HttpStatus.OK)
    public List<City> getCities() {
        return flightsServices.collectCities();
    }

    @GetMapping("/airports")
    @ResponseStatus(HttpStatus.OK)
    public List<Airport> getAirports() {
        return flightsServices.collectAirports();
    }

    @GetMapping("/airports/{city}")
    @ResponseStatus(HttpStatus.OK)
    public List<Airport> getAirportsByCity(@PathVariable("city") String city) {
        return flightsServices.collectAirports(city);
    }

    @GetMapping("/airports/{airport_code}/schedule/inbound")
    @ResponseStatus(HttpStatus.OK)
    public List<ScheduleRoute> getAirportScheduleInbound(
            @PathVariable("airport_code") String airportCode) {
        return flightsServices.collectInboundRoutes(airportCode);
    }

    @GetMapping("/airports/{airport_code}/schedule/outbound")
    @ResponseStatus(HttpStatus.OK)
    public List<ScheduleRoute> getAirportScheduleOutbound(
            @PathVariable("airport_code") String airportCode) {
        return flightsServices.collectOutboundRoutes(airportCode);
    }

    @GetMapping("/routes/search")
    @ResponseStatus(HttpStatus.OK)
    public List<RouteSearched> getRoutesSearch(
            @RequestParam("from") @NotBlank String from,
            @RequestParam("to") @NotBlank String to,
            @RequestParam("departure_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NonNull LocalDate departureDate,
            @RequestParam("booking_class") @NotBlank String bookingClass,
            @RequestParam(value = "max_connections", defaultValue = "1") Integer maxConnections) throws SQLException {
        return flightsServices.searchRoutes(from, to, departureDate, bookingClass, maxConnections);
    }

    @PostMapping("/booking")
    @ResponseStatus(HttpStatus.CREATED)
    public Booking postBooking(
            @Valid @RequestBody BookingRequest request) {
        return flightsServices.createBooking(request);
    }

    @PutMapping("/check-in")
    @ResponseStatus(HttpStatus.CREATED)
    public Boarding putCheckin(
            @Valid @RequestBody CheckinRequest request) {
        return flightsServices.checkIn(request);
    }

}
