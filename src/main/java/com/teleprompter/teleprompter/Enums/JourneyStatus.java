package com.teleprompter.teleprompter.enums;

import java.util.Set;


public enum JourneyStatus {
	
			PUBLISHED,  IN_PROGRESS, COMPLETED, CANCELLED;
		
//	Complete life-cycle (FIXED: switch now correctly runs on 'this')
	public boolean canTransitionTo(JourneyStatus target) {
		return switch (this) {    
		case PUBLISHED -> Set.of(IN_PROGRESS, CANCELLED).contains(target);
		case IN_PROGRESS -> Set.of(COMPLETED).contains(target);
		case COMPLETED, CANCELLED -> false;
		};
	}
			
}
