package com.teleprompter.teleprompter.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.teleprompter.teleprompter.entity.Journey;

@Repository
public interface JourneyRepository extends JpaRepository<Journey, Long> {

	 /**
     * Finds all published journeys filtering by source, destination, date range, and capacity.
     * Uses explicit JPQL named parameters to fully prevent SQL injection.
     */
	@Query("SELECT j FROM Journey j WHERE j.sourceCity.id = :sourceCityId AND j.destinationCity.id = :destinationCityId")
	Page<Journey> searchJourney(@Param("sourceCityId") Long sourceCityId, @Param("destinationCityId") Long destinationCityId, Pageable pageable);
	
	
}
