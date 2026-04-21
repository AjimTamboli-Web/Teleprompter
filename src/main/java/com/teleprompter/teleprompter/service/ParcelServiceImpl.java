package com.teleprompter.teleprompter.service;

import org.springframework.stereotype.Service;

import com.teleprompter.teleprompter.dtos.CreateParcelRequest;
import com.teleprompter.teleprompter.dtos.ParcelMapper;
import com.teleprompter.teleprompter.dtos.ParcelResponse;
import com.teleprompter.teleprompter.entity.Parcel;
import com.teleprompter.teleprompter.entity.User;
import com.teleprompter.teleprompter.repository.ParcelRepository;
import com.teleprompter.teleprompter.repository.UserRepository;

import jakarta.transaction.Transactional;


@Service
public class ParcelServiceImpl implements ParcelService{

	
	 // Final dependencies for immutability and guaranteed initialization
	private final ParcelRepository parcelRepo;
	private final UserRepository userRepo;
	private final ParcelMapper parcelMap;
	
	 /**
     * Constructor-based dependency injection.
     * Always preferred over @Autowired field injection for testing and safety.
     */
	public ParcelServiceImpl(ParcelRepository parcelRepo, UserRepository userRepo, ParcelMapper parcelMap) {
		this.parcelMap = parcelMap;
		this.parcelRepo = parcelRepo;
		this.userRepo = userRepo;
	}
	
	
	@Override
	@Transactional   // Ensures the entire creation process runs inside a database transaction boundary
	public ParcelResponse createParcel(CreateParcelRequest request) {
		
		 // 1. Database Verification: Check if the sender exists in our system
		User sender = userRepo.findById(request.getSenderId())
						.orElseThrow(() -> new RuntimeException("Sender not found with Id"));
		
		 // 2. Mapping: Create a detached Parcel entity from the validated DTO fields
		Parcel parcel = new Parcel();
		
		parcel.setCategory(request.getCategory());
		parcel.setDescription(request.getDescription());
		parcel.setFragile(request.getFragile());
		parcel.setPhotoUrl(request.getPhotoUrl());
		parcel.setValue(request.getValue());
		parcel.setRestrictedItemsDeclared(request.getRestrictedItemsDeclared());
		parcel.setWeight(request.getWeight());
		
		 // Business Rule Exception: Business states a new parcel starts as 'AWAITING_MATCH'
        // parcel.setStatus(ParcelStatus.AWAITING_MATCH); // (Add this once your ParcelStatus enum is active)
		
		 // 3. Attach Relationship: Set the verified User entity as the sender
		parcel.setSender(sender);
		
		 // 4. Persistence: Save the complete entity to PostgreSQL
		Parcel saveParcel = parcelRepo.save(parcel);
		
		// 5. Outbound Transformation: Map the managed entity back to clean ParcelResponse
		return parcelMap.toResponse(saveParcel);
	}

}
