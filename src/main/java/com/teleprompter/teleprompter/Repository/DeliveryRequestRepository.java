package com.teleprompter.teleprompter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teleprompter.teleprompter.entity.DeliveryRequest;

@Repository
public interface DeliveryRequestRepository extends JpaRepository<DeliveryRequest, Long> {

}
