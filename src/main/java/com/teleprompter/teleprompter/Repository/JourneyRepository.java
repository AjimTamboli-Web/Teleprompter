package com.teleprompter.teleprompter.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.teleprompter.teleprompter.entity.Journey;
import com.teleprompter.teleprompter.enums.JourneyStatus;

@Repository
public interface JourneyRepository extends JpaRepository<Journey, Long> {

	 /**
     * Finds all published journeys filtering by source, destination, date range, and capacity.
     * Uses explicit JPQL named parameters to fully prevent SQL injection.
     */
	@Query("SELECT j FROM Journey j " + 
			"WHERE j.sourceCity.id = :sourceCityId " +
			"AND j.destinationCity.id = :destinationCityId " + 
			"AND j.departureTime >= :fromTime " + 
			"AND j.departureTime <  :toTime " +
			"AND j.status = :status " +
			"AND j.availableWeightCapacity > 0 " +      // Direct comparison of BigDecimal with 0 is valid in JPQL.
			"ORDER BY j.departureTime ASC")            //  Earliest trip first
	Page<Journey> searchJourney(@Param("sourceCityId") Long sourceCityId, 
								@Param("destinationCityId") Long destinationCityId, 
								@Param("fromTime") LocalDateTime fromTime,
								@Param("toTime") LocalDateTime toTime,
								@Param("status") JourneyStatus status,
								Pageable pageable);
	
	
}
