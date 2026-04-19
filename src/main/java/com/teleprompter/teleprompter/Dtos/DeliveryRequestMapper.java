package com.teleprompter.teleprompter.dtos;

import org.springframework.stereotype.Component;

import com.teleprompter.teleprompter.entity.City;
import com.teleprompter.teleprompter.entity.DeliveryRequest;
import com.teleprompter.teleprompter.entity.Journey;
import com.teleprompter.teleprompter.entity.Parcel;

@Component
public class DeliveryRequestMapper {

	
	public DeliveryRequestResponse toResponse(DeliveryRequest deliveryRequest) {
		
		if(deliveryRequest == null) {
			return null;
		}
		
		DeliveryRequestResponse response = new DeliveryRequestResponse();
		
		response.setId(deliveryRequest.getId());
		response.setPickupAddress(deliveryRequest.getPickupAddress());
		response.setDropAddress(deliveryRequest.getDropAddress());
		response.setStatus(deliveryRequest.getStatus());
		
		
		response.setAcceptedAt(deliveryRequest.getAcceptedAt());
		response.setPickedUpAt(deliveryRequest.getPickedUpAt());
		response.setDeliveredAt(deliveryRequest.getDeliveredAt());
		response.setClosedAt(deliveryRequest.getClosedAt());
		response.setCreatedAt(deliveryRequest.getCreatedAt());
		response.setUpdatedAt(deliveryRequest.getUpdatedAt());
		
		
		response.setParcel(mapToParcelSummary(deliveryRequest.getParcel()));
		response.setJourney(mapToJourneySummary(deliveryRequest.getJourney()));
		
		return response;
		
		
	}
	
	
	private ParcelSummaryForRequestResponse mapToParcelSummary(Parcel parcel) {
		
		if(parcel == null) {
			return null;
		}
		
		ParcelSummaryForRequestResponse summary = new ParcelSummaryForRequestResponse();
		
		summary.setId(parcel.getId());
		summary.setDescription(parcel.getDescription());
		summary.setWeight(parcel.getWeight());
		summary.setFragile(parcel.isFragile());
		summary.setPhotoUrl(parcel.getPhotoUrl());
		summary.setCategory(parcel.getCategory());
		
		return summary;
		
	}
	
	private JourneySummaryResponseForDelivery mapToJourneySummary(Journey journey) {
		
		if(journey == null) {
			return null;
		}
		
		JourneySummaryResponseForDelivery summary = new JourneySummaryResponseForDelivery();
		
		summary.setId(journey.getId());
		summary.setDepartureTime(journey.getDepartureTime());
		summary.setTransportMode(journey.getTransportMode());
		
		summary.setSourceCity(mapToCitySummary(journey.getSourceCity()));
		summary.setDestinationCity(mapToCitySummary(journey.getDestinationCity()));
		
		
		return summary;
		
		
	}
	
	
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
