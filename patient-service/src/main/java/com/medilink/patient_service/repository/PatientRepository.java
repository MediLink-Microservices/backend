package com.medilink.patient_service.repository;

import com.medilink.patient_service.model.PatientProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * PatientRepository provides standard MongoDB CRUD methods for the patients collection.
 */
@Repository
public interface PatientRepository extends MongoRepository<PatientProfile, String> {
    Optional<PatientProfile> findByNIC(String NIC);
    Optional<PatientProfile> findByAuthUserId(String authUserId);
}
