package com.teleprompter.teleprompter.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teleprompter.teleprompter.Entity.Parcel;

@Repository
public interface ParcelRepository extends JpaRepository<Parcel, Long> {

}
