package com.teleprompter.teleprompter.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teleprompter.teleprompter.Entity.Journey;

@Repository
public interface JourneyRepository extends JpaRepository<Journey, Long> {

}
