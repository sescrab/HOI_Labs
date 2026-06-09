package org.example.d6.repositories;

import jakarta.persistence.LockModeType;
import org.example.d6.entities.PricingRuleId;
import org.example.d6.entities.PricingRules;
import org.example.d6.entities.Routes;
import org.example.d6.entities.Segments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PricingRulesRepository extends JpaRepository<PricingRules, PricingRuleId> {
    Optional<PricingRules> findByIdRouteNoAndIdFareConditions(String routeNo, String fareConditions);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT pr FROM PricingRules pr WHERE pr.id.routeNo = :routeNo AND pr.id.fareConditions = :fareConditions")
    Optional<PricingRules> findAndLockByRouteNoAndFareConditions(@Param("routeNo") String routeNo,
                                                                 @Param("fareConditions") String fareConditions);
}