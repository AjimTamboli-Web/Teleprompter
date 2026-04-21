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

	private final ParcelRepository parcelRepo;
	private final UserRepository userRepo;
	private final ParcelMapper parcelMap;
	
	public ParcelServiceImpl(ParcelRepository parcelRepo, UserRepository userRepo, ParcelMapper parcelMap) {
		this.parcelMap = parcelMap;
		this.parcelRepo = parcelRepo;
		this.userRepo = userRepo;
	}
	
	
	@Override
	@Transactional
	public ParcelResponse createParcel(CreateParcelRequest request) {
		
		User sender = userRepo.findById(request.getSenderId())
						.orElseThrow(() -> new RuntimeException("Sender not found with Id"));
		
		Parcel parcel = new Parcel();
		
		parcel.setCategory(request.getCategory());
		parcel.setDescription(request.getDescription());
		parcel.setFragile(request.getFragile());
		parcel.setPhotoUrl(request.getPhotoUrl());
		parcel.setValue(request.getValue());
		parcel.setRestrictedItemsDeclared(request.getRestrictedItemsDeclared());
		parcel.setWeight(request.getWeight());
		
		parcel.setSender(sender);
		
		Parcel saveParcel = parcelRepo.save(parcel);
		
		return parcelMap.toResponse(saveParcel);
	}

}
