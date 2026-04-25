package com.teleprompter.teleprompter.dtos;


import org.springframework.stereotype.Component;

import com.teleprompter.teleprompter.entity.Parcel;
import com.teleprompter.teleprompter.entity.User;

@Component
public class ParcelMapper {

//	Converts a database Parcel entity into a clean outbound ParcelResponse DTO.
	
	public ParcelResponse toResponse(Parcel parcel) {
		if(parcel == null) {
			return null;
		}
		
		ParcelResponse response = new ParcelResponse();
		
		 // Mapping basic flat fields safely with corrected primitive boolean methods ()
		
		response.setId(parcel.getId());
		response.setDescription(parcel.getDescription());
		response.setFragile(parcel.isFragile());
		response.setRestrictedItemsDeclared(parcel.isRestrictedItemsDeclared());
		
		response.setCreatedAt(parcel.getCreatedAt());
		response.setUpdatedAt(parcel.getUpdatedAt());
		
		response.setWeight(parcel.getWeight());
		response.setValue(parcel.getValue());
		response.setPhotoUrl(parcel.getPhotoUrl());
		response.setCategory(parcel.getCategory());
		
//		Mapping nested relationship using private helper
		response.setSender(mapToSenderSummary(parcel.getSender()));
		
		return response;
	}

       public Parcel toEntity(CreateParcelRequest request) {
		
		if(request == null) {
			return null;
		}
		
		// Fail-fast null guard with illegalArgumentException so at the time of unboxing we not face danger NPE. 
		 // Technical Guard 1: Checking fragile to prevent silent unboxing NPE and Safe and intentional failure
		if(request.getFragile() == null) {
			throw new IllegalArgumentException("Technical Failure: 'fragile' field must not be null during entity mapping.");
		}

		// Technical Guard 2: Checking restrictedItemsDeclared to prevent unboxing NPE and Safe and intentional failure
		if(request.getRestrictedItemsDeclared() == null) {
			throw new IllegalArgumentException("Technical Failure: 'restrictedItemsDeclared' field must not be null during entity mapping.");
		}
		
		Parcel parcel = new Parcel();
		
		parcel.setCategory(request.getCategory());
		parcel.setDescription(request.getDescription());
		parcel.setPhotoUrl(request.getPhotoUrl());
		parcel.setValue(request.getValue());
		parcel.setWeight(request.getWeight());
		
		
		//1. uses Defense-in-Depth instead of "trust the boundary" to save the unboxing risk Boolean to boolean
//		parcel.setFragile(request.getFragile() != null && request.getFragile());
//		parcel.setRestrictedItemsDeclared(request.getRestrictedItemsDeclared() != null && request.getRestrictedItemsDeclared());
	
		//2. Fail fast guard using in-build tools
//		parcel.setFragile(Objects.requireNonNull(request.getFragile(), "Fragile field must not be null"));
//		parcel.setRestrictedItemsDeclared(Objects.requireNonNull(request.getRestrictedItemsDeclared(), "restrictedItems must not be null"));
		
		// 3. Explicitly unboxing the validated non-null wrapper objects into primitives safely 
		parcel.setFragile(request.getFragile());
		parcel.setRestrictedItemsDeclared(request.getRestrictedItemsDeclared());
		
		
		return parcel;
	}
	
	
	/**
     * Private helper to cleanly isolate the User -> SenderSummaryResponse mapping.
     * Fixed: Changed to 'private' and capitalized 'Sender' (CamelCase).
     */
	private SenderSummaryResponse mapToSenderSummary(User user) {
		if(user == null) {
			return null;
		}
		
		SenderSummaryResponse summary = new SenderSummaryResponse();
		
		summary.setId(user.getId());
		summary.setName(user.getName());
		
		return summary;
	}
	
}
