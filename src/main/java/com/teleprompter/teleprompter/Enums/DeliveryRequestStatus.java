package com.teleprompter.teleprompter.enums;

import java.util.Set;

public enum DeliveryRequestStatus {
    
	PENDING, ACCEPTED, REJECTED, PICKED_UP, IN_TRANSIT,
	DELIVERED, CANCELLED;
	
	
	Set<DeliveryRequestStatus> allowedNext(){     // java 17 switch that returns the allowed set works well. 
		return switch(this) {
		case PENDING -> Set.of(ACCEPTED, REJECTED, CANCELLED);
		case ACCEPTED -> Set.of(PICKED_UP, CANCELLED);
		case PICKED_UP -> Set.of(IN_TRANSIT);
		case IN_TRANSIT -> Set.of(DELIVERED);
		case REJECTED,DELIVERED,CANCELLED -> Set.of();  // final states
		};
	}
	
	public boolean canTransitionTo(DeliveryRequestStatus target) {
		return allowedNext().contains(target);
	}
	
	
}
