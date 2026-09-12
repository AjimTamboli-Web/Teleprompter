package com.teleprompter.teleprompter.enums;

import com.teleprompter.teleprompter.exception.BusinessRuleViolationException;

public enum JourneyStatus {
	
			PUBLISHED,  IN_PROGRESS, COMPLETED, CANCELLED;
		
	// allowed transition logic
			public JourneyStatus transitionTo(JourneyStatus nextStatus) {
				if(this == PUBLISHED && nextStatus == CANCELLED) {
					return CANCELLED;
				}
				
				throw new BusinessRuleViolationException("Cannot transition journey from " + this + " to " + nextStatus);
			}
			
}
