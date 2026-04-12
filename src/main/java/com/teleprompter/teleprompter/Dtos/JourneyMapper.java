package com.teleprompter.teleprompter.dtos;

import org.springframework.stereotype.Component;

import com.teleprompter.teleprompter.entity.City;
import com.teleprompter.teleprompter.entity.Journey;
import com.teleprompter.teleprompter.entity.User;

@Component
public class JourneyMapper {

	/**
     * Converts a database Journey entity into a clean outbound JourneyResponse DTO.
     */	
	public JourneyResponse toResponse(Journey journey) {
		
		if(journey == null) {
			return null;
		}
		
		JourneyResponse response = new JourneyResponse();
		
//		Mapping basic flat fields safely using setters
		response.setId(journey.getId());
		response.setDepartureTime(journey.getDepartureTime());
		response.setEstimatedArrivalTime(journey.getEstimatedArrivalTime());
		response.setMaxWeightCapacity(journey.getMaxWeightCapacity());
		response.setAvailableWeightCapacity(journey.getAvailableWeightCapacity());
		response.setTransportMode(journey.getTransportMode());
		response.setStatus(journey.getStatus());
		
//		Mapping nested relationship fields using clean private helpers
		response.setTraveler(mapToTravelerSummary(journey.getTraveler()));
		response.setSourceCity(mapToCitySummary(journey.getSourceCity()));
		response.setDestinationCity(mapToCitySummary(journey.getDestinationCity()));
		
		
		return response;
		
	}
	
	 /**
     * Private helper to cleanly isolate the User -> TravelerSummaryResponse mapping.
     */	
	private TravelerSummaryResponse mapToTravelerSummary(User user) {
		
		if(user == null) {
			return null;
		}
		TravelerSummaryResponse summary = new TravelerSummaryResponse();
		summary.setId(user.getId());
		summary.setName(user.getName());
		
		return summary;
		
	}
	
	/**
     * Private helper to cleanly isolate the City -> CitySummaryResponse mapping.
     * Includes 'state' for better route clarity in frontend listings.
     */	
	private CitySummaryResponse mapToCitySummary(City city) {
		
		if(city == null) {
			return null;
		}
		
		CitySummaryResponse summary = new CitySummaryResponse();
		
		summary.setId(city.getId());
		summary.setCityName(city.getCityName());
		summary.setState(city.getState());
		
		return summary;
	}
	
}
