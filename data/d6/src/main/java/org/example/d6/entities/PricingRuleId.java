package org.example.d6.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PricingRuleId implements Serializable {

    @Column(name = "route_no", nullable = false)
    private String routeNo;

    @Column(name = "fare_conditions", nullable = false)
    private String fareConditions;
}