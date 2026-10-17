package com.teleprompter.teleprompter.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teleprompter.teleprompter.dtos.CreateParcelRequest;
import com.teleprompter.teleprompter.dtos.ParcelMapper;
import com.teleprompter.teleprompter.dtos.ParcelResponse;
import com.teleprompter.teleprompter.dtos.UpdateParcelRequest;
import com.teleprompter.teleprompter.entity.City;
import com.teleprompter.teleprompter.entity.DeliveryRequest;
import com.teleprompter.teleprompter.entity.Parcel;
import com.teleprompter.teleprompter.entity.User;
import com.teleprompter.teleprompter.enums.DeliveryRequestStatus;
import com.teleprompter.teleprompter.enums.ParcelStatus;
import com.teleprompter.teleprompter.exception.BusinessRuleViolationException;
import com.teleprompter.teleprompter.exception.ResourceNotFoundException;
import com.teleprompter.teleprompter.repository.CityRepository;
import com.teleprompter.teleprompter.repository.DeliveryRequestRepository;
import com.teleprompter.teleprompter.repository.ParcelRepository;
import com.teleprompter.teleprompter.repository.UserRepository;

@Service
public class ParcelServiceImpl {

	// Final dependencies for immutability and guaranteed initialization
	private final ParcelRepository parcelRepo;
	private final UserRepository userRepo;
	private final ParcelMapper parcelMap;
	private final CityRepository cityRepo;
	private final DeliveryRequestRepository deliveryRequestRepository;

	/**
	 * Constructor-based dependency injection. Always preferred over @Autowired
	 * field injection for testing and safety.
	 */
	public ParcelServiceImpl(ParcelRepository parcelRepo, UserRepository userRepo, ParcelMapper parcelMap,
			CityRepository cityRepo, DeliveryRequestRepository deliveryRequestRepository) {
		this.parcelMap = parcelMap;
		this.parcelRepo = parcelRepo;
		this.userRepo = userRepo;
		this.cityRepo = cityRepo;
		this.deliveryRequestRepository = deliveryRequestRepository;
	}

	@Transactional // Ensures the entire creation process runs inside a database transaction
					// boundary
	public ParcelResponse createParcel(CreateParcelRequest request) {

//		Same city check (Fail Fast)
		if (Objects.equals(request.getSourceCityId(), request.getDestinationCityId())) {
			throw new BusinessRuleViolationException("Source and destination cities must be different.");
		}

		// 1. Database Verification: Core business check before logic execution
		User sender = userRepo.findById(request.getSenderId())
				.orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getSenderId()));

		City sourceCity = cityRepo.findById(request.getSourceCityId())
				.orElseThrow(() -> new ResourceNotFoundException("City", "id", request.getSourceCityId()));

		City destinationCity = cityRepo.findById(request.getDestinationCityId())
				.orElseThrow(() -> new ResourceNotFoundException("City", "id", request.getDestinationCityId()));

		// 2. Mapping: FIXED! Delegating incoming DTO -> Entity conversion to the Mapper
		Parcel parcel = parcelMap.toEntity(request);

		parcel.setStatus(ParcelStatus.AWAITING_MATCH);

		// 3. Attach Relationship: Service handles the database-linked assignment
		parcel.setSender(sender);

		parcel.setSourceCity(sourceCity);
		parcel.setDestinationCity(destinationCity);

		// 4. Persistence: Write the managed entity to PostgreSQL
		Parcel savedParcel = parcelRepo.saveAndFlush(parcel);

		// 5. Outbound Transformation: Return shaped data to the controller boundary
		return parcelMap.toResponse(savedParcel);

	}

	@Transactional(readOnly = true) // Tells Hibernate to skip heavy dirty-checking workflows
	public ParcelResponse getParcelById(Long id) {

		if (id == null) { // if id is null then instead of NPE it passed with IllegealArgumentException
							// immediately fail-fast
			throw new IllegalArgumentException("Technical Failure: Search ID must not be null.");
		}

		// Using direct orElseThrow functional chaining with detailed context message
		Parcel parcel = parcelRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Parcel", "id", id));

		// Outbound Transformation: Return safe shaped data to the boundary
		return parcelMap.toResponse(parcel);

	}

	/**
	 * Fetches all parcels using strict server-side pagination (Pageable). Skips
	 * heavy Hibernate dirty-checking comparison workflows via readOnly flag.
	 */
	@Transactional(readOnly = true)
	public Page<ParcelResponse> getAllParcels(Pageable pageable) {

		if (pageable == null) {
			throw new IllegalArgumentException("Technical Failure: Pageable configuration must not be null.");
		}

		// 1. Data Access: Fetching the chunk of entities + automatic count query via
		// Spring Data JPA
		Page<Parcel> parcelPage = parcelRepo.findAll(pageable);

		// 2. Transformation: Safe transformation preserving all pagination metadata
		// We use method reference 'parcelMapr::toResponse' for clean functional style
		return parcelPage.map(parcelMap::toResponse);

	}

	@Transactional
	public ParcelResponse updateParcel(Long id, UpdateParcelRequest request) {

		Parcel parcel = parcelRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Parcel", "id", id));

//		Status check (Only AWAITING_MATCH can be edited)
		if (parcel.getStatus() != ParcelStatus.AWAITING_MATCH) {
			throw new BusinessRuleViolationException("Only parcels in AWAITING_MATCH status can be modified.");
		}

//		Partial update logic (Null-check-per-Field Pattern)
		if (request.getWeight() != null) {
			parcel.setWeight(request.getWeight());
		}
		if (request.getValue() != null) {
			parcel.setValue(request.getValue());
		}
		if (request.getPhotoUrl() != null) {
			parcel.setPhotoUrl(request.getPhotoUrl());
		}
		if (request.getDescription() != null) {
			parcel.setDescription(request.getDescription());
		}
		if (request.getCategory() != null) {
			parcel.setCategory(request.getCategory());
		}
		if (request.getRestrictedItemsDeclared() != null) {
			parcel.setRestrictedItemsDeclared(request.getRestrictedItemsDeclared());
		}
		if (request.getFragile() != null) {
			parcel.setFragile(request.getFragile());
		}

//		 Dirty checking automatically saves changes, no repo.save() needed!
		return parcelMap.toResponse(parcel);
	}

	@Transactional
	public ParcelResponse deleteParcel(Long id) { // (Replacing Hard Delete with POST Cancel)

		Parcel parcel = parcelRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Parcel", "id", id));

		// TODO: Owner Check

		if (parcel.getStatus() != ParcelStatus.AWAITING_MATCH) {
			throw new BusinessRuleViolationException("Only parcels in AWAITING_MATCH status can be cancelled.");
		}

		parcel.setStatus(ParcelStatus.CANCELLED);

		// Cascade cancel all PENDING requests for this parcel
		List<DeliveryRequest> request = deliveryRequestRepository.findByParcelId(id);
		for (DeliveryRequest req : request) {
			if (req.getStatus() == DeliveryRequestStatus.PENDING) {
				req.setStatus(DeliveryRequestStatus.CANCELLED);
				req.setClosedAt(LocalDateTime.now());
			}
		}

		parcelRepo.flush(); // fix: Explicit flush BEFORE mapping ensures updatedAt is current.
		
		return parcelMap.toResponse(parcel);
	}

}
