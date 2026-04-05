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
