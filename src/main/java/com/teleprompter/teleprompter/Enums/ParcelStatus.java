package com.teleprompter.teleprompter.enums;

public enum ParcelStatus {

	AWAITING_MATCH,   // parcel created, no accepted DeliveryRequest yet (this is the default on creation)
	MATCHED,        // a DeliveryRequest for this parcel has been accepted by a traveler
	IN_TRANSIT,    // traveler has physically picked it up
	DELIVERED,    // successfully delivered
	CANCELLED   // sender cancelled before delivery (only valid from AWAITING_MATCH or MATCHED — 
				//        you can't cancel something already DELIVERED)
	
}
