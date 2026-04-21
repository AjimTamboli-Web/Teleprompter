package com.teleprompter.teleprompter.service;

import com.teleprompter.teleprompter.dtos.CreateParcelRequest;
import com.teleprompter.teleprompter.dtos.ParcelResponse;

public interface ParcelService {

	ParcelResponse createParcel(CreateParcelRequest request);
	
}
