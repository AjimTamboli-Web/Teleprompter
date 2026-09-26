package com.teleprompter.teleprompter.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.teleprompter.teleprompter.dtos.CreateParcelRequest;
import com.teleprompter.teleprompter.dtos.ParcelResponse;
import com.teleprompter.teleprompter.dtos.UpdateParcelRequest;
import com.teleprompter.teleprompter.service.ParcelServiceImpl;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/api/parcels")
public class ParcelController {

	
	private final ParcelServiceImpl parcelService;
	
	// A constant cap has been set to protect the server from heavy load
		private static final int MAX_PAGE_SIZE = 50;
	
	public ParcelController(ParcelServiceImpl service) {
		this.parcelService = service;
	}
	
	@PostMapping
	public ResponseEntity<ParcelResponse> createParcel(@Valid @RequestBody CreateParcelRequest request){
		
	     ParcelResponse responseBody =	parcelService.createParcel(request);
		
	     return ResponseEntity.status(HttpStatus.CREATED).body(responseBody); 
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ParcelResponse> getParcelById(@PathVariable Long id){
		
		ParcelResponse responseBody	= parcelService.getParcelById(id);
		
		return ResponseEntity.status(HttpStatus.OK).body(responseBody);
	}
	
	
	@GetMapping
	public ResponseEntity<Page<ParcelResponse>> getAllParcels(@RequestParam @Min(value = 0) int page,
															  @RequestParam @Min(value = 1) @Max(value = MAX_PAGE_SIZE) int size){
		
		Pageable pageable = PageRequest.of(page, size);
		
		Page<ParcelResponse> responseBody = parcelService.getAllParcels(pageable);
		
		return ResponseEntity.ok(responseBody);
		
	}
	
	@PatchMapping("/{id}")
	public ResponseEntity<ParcelResponse> updateParcel(@PathVariable Long id,
													  @Valid @RequestBody UpdateParcelRequest request){
		
		ParcelResponse responseBody = parcelService.updateParcel(id, request);
		
		return ResponseEntity.status(HttpStatus.OK).body(responseBody);
		
	}
	
}
