package com.teleprompter.teleprompter.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teleprompter.teleprompter.dtos.CreateParcelRequest;
import com.teleprompter.teleprompter.dtos.ParcelMapper;
import com.teleprompter.teleprompter.dtos.ParcelResponse;
import com.teleprompter.teleprompter.entity.Parcel;
import com.teleprompter.teleprompter.entity.User;
import com.teleprompter.teleprompter.repository.ParcelRepository;
import com.teleprompter.teleprompter.repository.UserRepository;




@Service
public class ParcelServiceImpl{

	
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
	
	
	
	@Transactional   // Ensures the entire creation process runs inside a database transaction boundary
	public ParcelResponse createParcel(CreateParcelRequest request) {
	
		 // 1. Database Verification: Core business check before logic execution
		User sender = userRepo.findById(request.getSenderId())
				    .orElseThrow(() -> new RuntimeException("Sender not found with ID: " + request.getSenderId()));
		
		
		 // 2. Mapping: FIXED! Delegating incoming DTO -> Entity conversion to the Mapper	
	      Parcel parcel =	parcelMap.toEntity(request);
		
	      
	      // 3. Attach Relationship: Service handles the database-linked assignment 
	      parcel.setSender(sender);
	      
	      // 4. Persistence: Write the managed entity to PostgreSQL
	      Parcel savedParcel = parcelRepo.save(parcel);
	     
	      // 5. Outbound Transformation: Return shaped data to the controller boundary
	      return parcelMap.toResponse(savedParcel);
	      
	      
	}

}
