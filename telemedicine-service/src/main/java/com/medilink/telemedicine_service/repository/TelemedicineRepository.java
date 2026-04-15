package com.medilink.telemedicine_service.repository;

import com.medilink.telemedicine_service.model.Telemedicine;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TelemedicineRepository extends MongoRepository<Telemedicine, String> {
    List<Telemedicine> findByDoctorId(String doctorId);
    List<Telemedicine> findByPatientId(String patientId);
    List<Telemedicine> findByDoctorIdAndStatus(String doctorId, String status);
}
