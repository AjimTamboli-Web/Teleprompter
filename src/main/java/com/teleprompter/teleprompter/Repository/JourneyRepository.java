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
     * Searches for published journeys with available capacity using a half-open date interval.
     * Includes a primary key tie-breaker in ORDER BY to guarantee stable pagination results.
     */
	@Query("""    
			SELECT j FROM Journey j 
			WHERE j.sourceCity.id = :sourceCityId  
			AND j.destinationCity.id = :destinationCityId 
			AND j.departureTime >= :fromTime 
			AND j.departureTime <  :toTime 
			AND j.status = :status 
			AND j.availableWeightCapacity > 0    
			ORDER BY j.departureTime ASC, j.id ASC    // [FIX]: Added j.id ASC to ensure stable pagination.
			""")  // Java 17 also offers the option of text blocks ("""), which are cleaner and easier to read.          
	Page<Journey> searchJourney(@Param("sourceCityId") Long sourceCityId, 
								@Param("destinationCityId") Long destinationCityId, 
								@Param("fromTime") LocalDateTime fromTime,
								@Param("toTime") LocalDateTime toTime,
								@Param("status") JourneyStatus status,
								Pageable pageable);
	
	
}
