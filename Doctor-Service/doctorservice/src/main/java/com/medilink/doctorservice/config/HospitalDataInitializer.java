package com.medilink.doctorservice.config;

import com.medilink.doctorservice.entity.Hospital;
import com.medilink.doctorservice.entity.HospitalType;
import com.medilink.doctorservice.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HospitalDataInitializer implements CommandLineRunner {

    private final HospitalRepository hospitalRepository;

    @Override
    public void run(String... args) throws Exception {
        if (hospitalRepository.count() == 0) {
            List<Hospital> hospitals = Arrays.asList(
                // Colombo District Hospitals
                Hospital.builder()
                        .name("National Hospital of Sri Lanka")
                        .address("Regent Street, Colombo 08")
                        .city("Colombo")
                        .province("Western")
                        .telephone("0112691111")
                        .email("info@nhsl.health.gov.lk")
                        .type(HospitalType.HOSPITAL)
                        .isActive(true)
                        .createdAt(LocalDateTime.now())
                        .build(),
                        
                Hospital.builder()
                        .name("Lady Ridgeway Hospital for Children")
                        .address("Regent Street, Colombo 08")
                        .city("Colombo")
                        .province("Western")
                        .telephone("0112691111")
                        .type(HospitalType.HOSPITAL)
                        .isActive(true)
                        .createdAt(LocalDateTime.now())
                        .build(),
                        
                Hospital.builder()
                        .name("Nawaloka Hospitals")
                        .address("23, Deshamanya Road, Colombo 05")
                        .city("Colombo")
                        .province("Western")
                        .telephone("0112544444")
                        .email("info@nawaloka.com")
                        .type(HospitalType.HOSPITAL)
                        .isActive(true)
                        .createdAt(LocalDateTime.now())
                        .build(),
                        
                Hospital.builder()
                        .name("Asiri Surgical Hospital")
                        .address("181, Kirula Road, Colombo 05")
                        .city("Colombo")
                        .province("Western")
                        .telephone("0115330000")
                        .email("info@asiri.lk")
                        .type(HospitalType.HOSPITAL)
                        .isActive(true)
                        .createdAt(LocalDateTime.now())
                        .build(),
                        
                // Kandy District Hospitals
                Hospital.builder()
                        .name("Kandy General Hospital")
                        .address("Kandy Road, Kandy")
                        .city("Kandy")
                        .province("Central")
                        .telephone("0812222361")
                        .type(HospitalType.HOSPITAL)
                        .isActive(true)
                        .createdAt(LocalDateTime.now())
                        .build(),
                        
                // Galle District Hospitals
                Hospital.builder()
                        .name("Karapitiya Teaching Hospital")
                        .address("Karapitiya, Galle")
                        .city("Galle")
                        .province("Southern")
                        .telephone("0912234567")
                        .type(HospitalType.HOSPITAL)
                        .isActive(true)
                        .createdAt(LocalDateTime.now())
                        .build(),
                        
                // Jaffna District Hospitals
                Hospital.builder()
                        .name("Jaffna Teaching Hospital")
                        .address("Jaffna")
                        .city("Jaffna")
                        .province("Northern")
                        .telephone("0212222261")
                        .type(HospitalType.HOSPITAL)
                        .isActive(true)
                        .createdAt(LocalDateTime.now())
                        .build(),
                        
                // Anuradhapura District Hospitals
                Hospital.builder()
                        .name("Anuradhapura Teaching Hospital")
                        .address("Anuradhapura")
                        .city("Anuradhapura")
                        .province("North Central")
                        .telephone("0252222261")
                        .type(HospitalType.HOSPITAL)
                        .isActive(true)
                        .createdAt(LocalDateTime.now())
                        .build(),
                        
                // Private Clinics in Colombo
                Hospital.builder()
                        .name("Durdans Hospital")
                        .address("15, Alfred House, Colombo 03")
                        .city("Colombo")
                        .province("Western")
                        .telephone("0112304300")
                        .email("info@durdans.com")
                        .type(HospitalType.CLINIC)
                        .isActive(true)
                        .createdAt(LocalDateTime.now())
                        .build(),
                        
                Hospital.builder()
                        .name("Lanka Hospitals")
                        .address("545, Elvitigala Mawatha, Colombo 05")
                        .city("Colombo")
                        .province("Western")
                        .telephone("0117660000")
                        .email("info@lankahospitals.com")
                        .type(HospitalType.CLINIC)
                        .isActive(true)
                        .createdAt(LocalDateTime.now())
                        .build(),
                        
                // Diagnostic Centers
                Hospital.builder()
                        .name("Channel Bay")
                        .address("32, Stratford Avenue, Colombo 06")
                        .city("Colombo")
                        .province("Western")
                        .telephone("0112505500")
                        .type(HospitalType.DIAGNOSTIC_CENTER)
                        .isActive(true)
                        .createdAt(LocalDateTime.now())
                        .build()
            );

            hospitalRepository.saveAll(hospitals);
            System.out.println("Sri Lankan hospitals initialized successfully!");
        }
    }
}
