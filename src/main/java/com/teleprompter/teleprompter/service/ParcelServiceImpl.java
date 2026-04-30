package com.teleprompter.teleprompter.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teleprompter.teleprompter.dtos.CreateParcelRequest;
import com.teleprompter.teleprompter.dtos.ParcelMapper;
import com.teleprompter.teleprompter.dtos.ParcelResponse;
import com.teleprompter.teleprompter.dtos.UpdateParcelRequest;
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
	
	@Transactional(readOnly = true) // Tells Hibernate to skip heavy dirty-checking workflows
	public ParcelResponse getParcelById(Long id) {
		
		if(id == null) {  // if id is null then instead of NPE it passed with IllegealArgumentException immediately fail-fast
			throw new IllegalArgumentException("\"Technical Failure: Search ID must not be null.\"");
		}
		
		// FIXED: Using direct orElseThrow functional chaining with detailed context message
		Parcel parcel = parcelRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("Parcel not found with ID: " + id));
		
		
		// 2. Outbound Transformation: Return safe shaped data to the boundary
		return  parcelMap.toResponse(parcel);
		
	}
	
	
	 /**
      * Fetches all parcels using strict server-side pagination (Pageable).
      * Skips heavy Hibernate dirty-checking comparison workflows via readOnly flag.
     */
	@Transactional(readOnly = true)
	public Page<ParcelResponse> getAllParcels(Pageable pageable){		
		
		if(pageable == null) {
			throw new IllegalArgumentException("Technical Failure: Pageable configuration must not be null.");
		}
		
		
		// 1. Data Access: Fetching the chunk of entities + automatic count query via Spring Data JPA
		Page<Parcel> parcelPage = parcelRepo.findAll(pageable);
		
		// 2. Transformation: Safe transformation preserving all pagination metadata
        // We use method reference 'parcelMapper::toResponse' for clean functional style
	    return 	parcelPage.map(parcelMap::toResponse);
		
	}
	
	@Transactional          
	public ParcelResponse updateParcel(Long id, UpdateParcelRequest request) {
		
		Parcel parcel = parcelRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("Parcel not found.."));
		
//		Partial update logic (Null-check-per-Field Pattern)
		if(request.getWeight()  != null) {
			parcel.setWeight(request.getWeight());
		}
		if(request.getValue() != null) {
			parcel.setValue(request.getValue());
		}
		if(request.getPhotoUrl() != null) {
			parcel.setPhotoUrl(request.getPhotoUrl());
		}
		if(request.getDescription() != null) {
			parcel.setDescription(request.getDescription());
		}
		if(request.getCategory() != null) {
			parcel.setCategory(request.getCategory());
		}
		if(request.getRestrictedItemsDeclared() != null) {
			parcel.setRestrictedItemsDeclared(request.getRestrictedItemsDeclared());
		}
		if(request.getFragile() != null) {
			parcel.setFragile(request.getFragile());
		}
		
//		save updated entity into database
		Parcel updateParcel = parcelRepo.save(parcel);
		
//		Using pure mapper convert into response and return
		return parcelMap.toResponse(updateParcel);  
	}
	
	
	@Transactional
	public void deleteParcel(Long id) {  // using fetch first approach to avoid EmptyResultDataAccessException
		
		Parcel  parcel = parcelRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("Parcel not found, Id: " + id));
		
		
		parcelRepo.delete(parcel);
		
	}

}
