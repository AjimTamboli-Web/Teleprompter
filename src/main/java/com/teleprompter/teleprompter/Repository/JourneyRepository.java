package com.teleprompter.teleprompter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teleprompter.teleprompter.entity.Journey;

@Repository
public interface JourneyRepository extends JpaRepository<Journey, Long> {

}
