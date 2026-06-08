package org.example.d6.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pricing_rules")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PricingRules {
    @EmbeddedId
    private PricingRuleId id;

    @Column(name = "min_price", nullable = false, columnDefinition = "numeric(10,2)")
    private Double minPrice;
    @Column(name = "avg_price", nullable = false, columnDefinition = "numeric(10,2)")
    private Double avgPrice;
    @Column(name = "max_price", nullable = false, columnDefinition = "numeric(10,2)")
    private Double maxPrice;

    @Column(name = "total_segments", nullable = false)
    private Integer totalSegments;
}