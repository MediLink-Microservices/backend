package com.medilink.doctorservice.repository;

import com.medilink.doctorservice.entity.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HospitalRepository extends MongoRepository<Hospital, String> {
    
    List<Hospital> findByCity(String city);
    
    List<Hospital> findByProvince(String province);
    
    List<Hospital> findByType(HospitalType type);
    
    List<Hospital> findByIsActiveTrue();
    
    List<Hospital> findByCityAndIsActiveTrue(String city);
    
    List<Hospital> findByProvinceAndIsActiveTrue(String province);
}
