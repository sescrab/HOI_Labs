package org.example.d6.repositories;

import org.example.d6.entities.PricingRules;
import org.example.d6.entities.Routes;
import org.example.d6.entities.Segments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PricingRulesRepository extends JpaRepository<PricingRules, String> {
    Optional<PricingRules> findByIdRouteNoAndIdFareConditions(String routeNo, String fareConditions);
}