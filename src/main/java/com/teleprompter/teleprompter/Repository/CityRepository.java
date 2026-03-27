package com.teleprompter.teleprompter.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teleprompter.teleprompter.Entity.City;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

}
