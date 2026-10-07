package com.teleprompter.teleprompter.enums;

import java.util.Set;


public enum JourneyStatus {
	
			PUBLISHED,  IN_PROGRESS, COMPLETED, CANCELLED;
		
//	Complete life-cycle
	public boolean canTransitionTo(JourneyStatus target) {
		return switch (target) {
		case PUBLISHED -> Set.of(IN_PROGRESS, CANCELLED).contains(target);
		case IN_PROGRESS -> Set.of(COMPLETED).contains(target);
		case COMPLETED, CANCELLED -> false;
		};
	}
			
}
