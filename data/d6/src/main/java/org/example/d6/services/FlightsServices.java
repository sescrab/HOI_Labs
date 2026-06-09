package org.example.d6.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.d6.dtos.requests.BookingRequest;
import org.example.d6.dtos.requests.CheckinRequest;
import org.example.d6.dtos.responses.*;
import org.example.d6.entities.*;
import org.example.d6.repositories.*;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.sql.Array;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FlightsServices {

    private final AirportsDataRepository airportsDataRepository;
    private final RoutesRepository routesRepository;
    private final FlightsRepository flightsRepository;
    private final SegmentsRepository segmentsRepository;
    private final TicketsRepository ticketsRepository;
    private final BookingsRepository bookingsRepository;
    private final BoardingPassesRepository boardingPassesRepository;
    private final PricingRulesRepository pricingRulesRepository;
    private final SeatsRepository seatsRepository;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<City> collectCities(){
        List<AirportsData> airportsData = airportsDataRepository.findAll();

        ArrayList<City> cities = new ArrayList<>();
        for(AirportsData airport : airportsData){
            cities.add(new City(airport.getCity().get("ru")));
        }
        return cities;
    }

    public List<Airport> collectAirports(){
        return  collectAirports(null);
    }
    public List<Airport> collectAirports(String city){
        List<AirportsData> airportsData = airportsDataRepository.findAll();

        ArrayList<Airport> airports = new ArrayList<>();
        for(AirportsData airport : airportsData){
            if(city == null || city.equals(airport.getCity().get("ru"))) {
                airports.add(new Airport(airport.getAirportCode(), airport.getAirportName().get("ru"), airport.getCity().get("ru")));
            }
        }
        return airports;
    }

    public List<ScheduleRoute> collectInboundRoutes(String airportCode) {
        List<Routes> inboundRoutes = routesRepository.findByArrivalAirport_AirportCode(airportCode);
        ArrayList<ScheduleRoute> routes = new ArrayList<>();
        for(Routes inboundRoute : inboundRoutes) {
            routes.add(new ScheduleRoute(Arrays.asList(inboundRoute.getDaysOfWeek()),
                    inboundRoute.getScheduledTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                    inboundRoute.getRouteNo(),
                    inboundRoute.getDepartureAirport().getAirportCode(),
                    inboundRoute.getArrivalAirport().getAirportCode()));
        }
        return routes;
    }

    public List<ScheduleRoute> collectOutboundRoutes(String airportCode) {
        List<Routes> outboundRoutes = routesRepository.findByDepartureAirport_AirportCode(airportCode);
        ArrayList<ScheduleRoute> routes = new ArrayList<>();
        for(Routes outboundRoute : outboundRoutes) {
            routes.add(new ScheduleRoute(Arrays.asList(outboundRoute.getDaysOfWeek()),
                    outboundRoute.getScheduledTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                    outboundRoute.getRouteNo(),
                    outboundRoute.getDepartureAirport().getAirportCode(),
                    outboundRoute.getArrivalAirport().getAirportCode()));
        }
        return routes;
    }

    public List<RouteSearched> searchRoutes(String from, String to, LocalDate departureDate,
                                            String bookingClass, Integer maxConnections) throws SQLException {
        int connections = 0;
        if(maxConnections != null){
            if(maxConnections > 3 || maxConnections < 0){
                throw new IllegalArgumentException("Incorrect maxConnections amount (0, 1, 2, 3 possible)");
            }
            connections = maxConnections;
        }

        String sql = """
        WITH RECURSIVE flight_search AS (
            SELECT 
                f.flight_id,
                f.scheduled_departure,
                f.scheduled_arrival,
                r.arrival_airport,
                0 AS connections,
                ARRAY[f.flight_id] AS path,
                p.max_price AS total_price
            FROM flights f
            JOIN routes r ON f.route_no = r.route_no AND r.validity @> f.scheduled_departure
            JOIN airports_data a ON r.departure_airport = a.airport_code
            JOIN pricing_rules p ON r.route_no = p.route_no AND p.fare_conditions = :bookingClass
            WHERE (a.airport_code = :from OR a.city->>'ru' = :from)
              AND f.scheduled_departure >= :departureDate
              AND f.scheduled_departure < :departureDate + interval '24 hours'
              AND f.status = 'Scheduled'
              
            UNION ALL
            
            SELECT 
                f.flight_id,
                f.scheduled_departure,
                f.scheduled_arrival,
                r.arrival_airport,
                fs.connections + 1,
                fs.path || f.flight_id,
                fs.total_price + p.max_price
            FROM flight_search fs
            JOIN flights f ON f.scheduled_departure > fs.scheduled_arrival
                                      AND f.scheduled_departure < fs.scheduled_arrival + interval '24 hours'
            JOIN routes r ON f.route_no = r.route_no AND r.validity @> f.scheduled_departure
            JOIN pricing_rules p ON r.route_no = p.route_no AND p.fare_conditions = :bookingClass
            WHERE r.departure_airport = fs.arrival_airport
              AND fs.connections < :maxConnections
              AND NOT f.flight_id = ANY(fs.path)
              AND f.status = 'Scheduled'
        )
        SELECT fs.path, fs.total_price 
        FROM flight_search fs
        JOIN airports_data arr ON fs.arrival_airport = arr.airport_code
        WHERE (arr.airport_code = :to OR arr.city->>'ru' = :to)
        ORDER BY fs.total_price ASC, fs.connections ASC
        LIMIT 50;
    """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("from", from)
                .addValue("to", to)
                .addValue("departureDate", departureDate.atStartOfDay().atOffset(ZoneOffset.UTC))
                .addValue("bookingClass", bookingClass)
                .addValue("maxConnections", connections);

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, params);
        List<RouteSearched> results = new ArrayList<>();

        for (Map<String, Object> row : rows) {
            List<Integer> flightIds = Arrays.asList((Integer[]) ((Array) row.get("path")).getArray());
            BigDecimal totalPrice = (BigDecimal) row.get("total_price");
            List<Flight> flights = collectFlights(flightIds, bookingClass);
            results.add(new RouteSearched(flights.size(), totalPrice.doubleValue(), flights));
        }

        return results;
    }

    private List<Flight> collectFlights(List<Integer> flightIds, String bookingClass) {
        List<Flight> flightsList = new ArrayList<>();
        if (flightIds.isEmpty()) return flightsList;

        Map<Integer, Flights> flightMap = new HashMap<>();
        for (Flights flight : flightsRepository.findAllById(flightIds)) {
            flightMap.put(flight.getFlightId(), flight);
        }

        for (Integer id : flightIds) {
            Flights flight = flightMap.get(id);
            if (flight == null) continue;
            Double price = getPriceForFlightAndClass(flight.getFlightId(), bookingClass);
            Flight responseFlight = new Flight(
                    flight.getFlightId(),
                    flight.getScheduledDeparture().toString(),
                    flight.getScheduledArrival().toString(),
                    price
            );
            flightsList.add(responseFlight);
        }
        return flightsList;
    }


    @Transactional()
    public Booking createBooking(BookingRequest request) {
        List<Flights> flights = new ArrayList<>();
        double totalPrice = 0.0;

        for (Integer flightId : request.getFlightIds()) {
            Flights flight = flightsRepository.findAndLockById(flightId)
                    .orElseThrow(() -> new EntityNotFoundException("Flight not found: " + flightId));

            if (flight.getStatus() != Flights.FlightStatus.Scheduled) {
                throw new IllegalStateException("Flight " + flightId + " is not available for booking");
            }

            PricingRules priceRule = pricingRulesRepository
                    .findAndLockByRouteNoAndFareConditions(flight.getRouteNo(), request.getBookingClass())
                    .orElseThrow(() -> new EntityNotFoundException("Price rule not found for route " + flight.getRouteNo()));
            totalPrice += priceRule.getMaxPrice();
            flights.add(flight);
        }


        String bookRef = generateBookRef();
        Bookings booking = new Bookings();
        booking.setBookRef(bookRef);
        booking.setBookDate(OffsetDateTime.now());
        booking.setTotalAmount(totalPrice);
        bookingsRepository.save(booking);

        String ticketNo = generateTicketNo();
        Tickets ticket = new Tickets();
        ticket.setTicketNo(ticketNo);
        ticket.setBooking(booking);
        ticket.setPassengerId(request.getPassengerId());
        ticket.setPassengerName(request.getPassengerName());
        ticket.setOutbound(true);
        ticketsRepository.save(ticket);

        Ticket responseTicket = new Ticket(ticket.getTicketNo(), new ArrayList<>());
        for (Flights flight : flights) {
            SegmentsId segId = new SegmentsId(ticketNo, flight.getFlightId());
            Segments segment = new Segments();
            segment.setId(segId);
            segment.setTicket(ticket);
            segment.setFlight(flight);
            segment.setFareConditions(Segments.FareCondition.valueOf(request.getBookingClass()));
            segment.setPrice(getPriceForFlightAndClass(flight.getFlightId(), request.getBookingClass()));
            segmentsRepository.save(segment);
            responseTicket.getSegments().add(new Segment(flight.getFlightId(), request.getBookingClass()));
        }

        return new Booking(bookRef, responseTicket);
    }

    @Transactional()
    public Boarding checkIn(CheckinRequest request) {
        Flights lockedFlight = flightsRepository.findAndLockById(request.getFlightId()).orElseThrow(
                () -> new EntityNotFoundException("Flight not found" + request.getFlightId()));

        Optional<BoardingPasses> possiblePass = boardingPassesRepository.findById(
                new BoardingPassesId(request.getTicketNo(), request.getFlightId()));
        if(possiblePass.isPresent()){
            BoardingPasses pass = possiblePass.get();
            return new Boarding(pass.getBoardingNo(), pass  .getSeatNo(),
                    pass.getBoardingTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        }


        SegmentsId segId = new SegmentsId(request.getTicketNo(), request.getFlightId());
        Segments segment = segmentsRepository.findById(segId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found for the flight"));

        Optional<BoardingPasses> existing = boardingPassesRepository
                .findAndLockByFlightIdAndSeatNo(request.getFlightId(), request.getSeatNo());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Seat " + request.getSeatNo() + " is already taken");
        }

        Routes route = routesRepository.findByRouteNoAndValidityContains
                (lockedFlight.getRouteNo(), lockedFlight.getScheduledDeparture()).orElseThrow();
        Seats seat = seatsRepository.findById(new SeatsId(route.getAirplaneCode(), request.getSeatNo())).orElseThrow(
                () -> new EntityNotFoundException("Seat does not exist"));
        if(!seat.getFareConditions().equals(segment.getFareConditions())){
            throw new IllegalArgumentException("Selected seat with incorrect fare condition");
        }

        List<BoardingPasses> existingPasses = boardingPassesRepository.findAllAndLockByFlightId(request.getFlightId());
        int maxno = 1;
        for(BoardingPasses pass : existingPasses){
            if(pass.getBoardingNo() >= maxno){
                maxno = pass.getBoardingNo() + 1;
            }
        }

        BoardingPassesId bpId = new BoardingPassesId(request.getTicketNo(), request.getFlightId());
        BoardingPasses boardingPass = new BoardingPasses();
        boardingPass.setId(bpId);
        boardingPass.setSeatNo(request.getSeatNo());
        boardingPass.setBoardingNo(maxno);
        boardingPass.setBoardingTime(OffsetDateTime.now());

        boardingPassesRepository.save(boardingPass);

        return new Boarding(maxno, request.getSeatNo(),
                boardingPass.getBoardingTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
    }


    private Double getPriceForFlightAndClass(Integer flightId, String bookingClass) {
        Flights flight = flightsRepository.findByFlightId(flightId).orElseThrow();
        Double price = pricingRulesRepository.findByIdRouteNoAndIdFareConditions(flight.getRouteNo(), bookingClass)
                .orElseThrow().getMaxPrice();
        return price;
    }

    private String generateBookRef() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    private String generateTicketNo() {
        return UUID.randomUUID().toString().substring(0, 13);
    }
}