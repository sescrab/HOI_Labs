package org.example.d6.entities;

import jakarta.persistence.*;
import lombok.*;
import org.example.d6.services.JsonbConverter;
import org.postgresql.geometric.PGpoint;

import java.util.Map;

@Entity
@Table(name = "airports_data", schema = "bookings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AirportsData {

    @Id
    @Column(name = "airport_code", nullable = false, columnDefinition = "bpchar(3)")
    private String airportCode;

    @Convert(converter = JsonbConverter.class)
    @Column(name = "airport_name", nullable = false, columnDefinition = "jsonb")
    private Map<String, String> airportName;

    @Convert(converter = JsonbConverter.class)
    @Column(name = "city", nullable = false, columnDefinition = "jsonb")
    private Map<String, String> city;

    @Convert(converter = JsonbConverter.class)
    @Column(name = "country", nullable = false, columnDefinition = "jsonb")
    private Map<String, String> country;

    // геттеры, сеттеры, конструкторы
}