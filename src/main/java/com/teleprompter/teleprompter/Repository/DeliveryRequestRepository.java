package com.teleprompter.teleprompter.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teleprompter.teleprompter.entity.DeliveryRequest;

@Repository
public interface DeliveryRequestRepository extends JpaRepository<DeliveryRequest, Long> {

	
	boolean existsByParcelIdAndJourneyId(Long parcelId, Long journeyId); 
	
//	Fetching the parcel, the journey, and both cities associated with the journey together to avoid the N+1 problem.
	@EntityGraph(attributePaths = {"parcel","journey","journey.sourceCity","journey.destinationCity"})
	Page<DeliveryRequest> findByJourneyId(Long journeyId, Pageable pageable);
}
