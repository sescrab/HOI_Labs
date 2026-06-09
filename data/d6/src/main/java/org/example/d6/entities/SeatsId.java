package org.example.d6.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatsId implements Serializable {

    @Column(name = "airplane_code", nullable = false, length = 3)
    private String airplaneCode;

    @Column(name = "seat_no", nullable = false)
    private String seatNo;
}