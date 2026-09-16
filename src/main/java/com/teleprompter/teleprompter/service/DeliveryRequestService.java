package com.teleprompter.teleprompter.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teleprompter.teleprompter.dtos.CreateDeliveryRequest;
import com.teleprompter.teleprompter.dtos.DeliveryRequestResponse;
import com.teleprompter.teleprompter.entity.Journey;
import com.teleprompter.teleprompter.entity.Parcel;
import com.teleprompter.teleprompter.enums.JourneyStatus;
import com.teleprompter.teleprompter.enums.ParcelStatus;
import com.teleprompter.teleprompter.exception.BusinessRuleViolationException;
import com.teleprompter.teleprompter.exception.ResourceNotFoundException;
import com.teleprompter.teleprompter.repository.DeliveryRequestRepository;
import com.teleprompter.teleprompter.repository.JourneyRepository;
import com.teleprompter.teleprompter.repository.ParcelRepository;

@Service
public class DeliveryRequestService {

	private final DeliveryRequestRepository deliveryRequestRepo;
	private final ParcelRepository parcelRepo;
	private final JourneyRepository journeyRepo;

	public DeliveryRequestService(DeliveryRequestRepository deliveryRequestRepo, ParcelRepository parcelRepo,
			JourneyRepository journeyRepo) {
		this.deliveryRequestRepo = deliveryRequestRepo;
		this.parcelRepo = parcelRepo;
		this.journeyRepo = journeyRepo;
	}

	@Transactional
	public DeliveryRequestResponse createDeliveryRequest(CreateDeliveryRequest request) {

		// Rule 1: Parcel Validation
		Parcel parcel = parcelRepo.findById(request.getParcelId())
				.orElseThrow(() -> new ResourceNotFoundException("Parcel", "id", request.getParcelId()));
		if (parcel.getStatus() != ParcelStatus.AWAITING_MATCH) {
			throw new BusinessRuleViolationException("Parcel must be in AWAITING_MATCH status.");
		}

		// Rule 2: Journey Validation
		Journey journey = journeyRepo.findById(request.getJourneyId())
				.orElseThrow(() -> new ResourceNotFoundException("Journey", "id", request.getJourneyId()));
		if (journey.getStatus() != JourneyStatus.PUBLISHED) {
			throw new BusinessRuleViolationException("Journey must be in PUBLISHES status.");
		}

		// Rule 3:Past Validation
		if(journey.getDepartureTime().isBefore(LocalDateTime.now())) {
			throw new BusinessRuleViolationException("Cannot request a journey that has already departed.");
		}
		
		
		
		return null;
	}

}
