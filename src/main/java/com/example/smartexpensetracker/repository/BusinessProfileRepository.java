package com.example.smartexpensetracker.repository;

import com.example.smartexpensetracker.model.BusinessProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BusinessProfileRepository extends JpaRepository<BusinessProfile, Long> {

    List<BusinessProfile> findByUserId(Long userId);

}