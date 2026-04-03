package com.medilink.doctorservice.service;

import com.medilink.doctorservice.dto.HospitalRequestDTO;
import com.medilink.doctorservice.dto.HospitalResponseDTO;
import com.medilink.doctorservice.entity.Hospital;
import com.medilink.doctorservice.entity.HospitalType;
import com.medilink.doctorservice.exception.ResourceNotFoundException;
import com.medilink.doctorservice.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HospitalService {

    private final HospitalRepository hospitalRepository;

    public HospitalResponseDTO createHospital(HospitalRequestDTO hospitalRequestDTO) {
        Hospital hospital = Hospital.builder()
                .name(hospitalRequestDTO.getName())
                .address(hospitalRequestDTO.getAddress())
                .city(hospitalRequestDTO.getCity())
                .province(hospitalRequestDTO.getProvince())
                .telephone(hospitalRequestDTO.getTelephone())
                .email(hospitalRequestDTO.getEmail())
                .type(hospitalRequestDTO.getType())
                .isActive(hospitalRequestDTO.getIsActive() != null ? 
                    hospitalRequestDTO.getIsActive() : true)
                .createdAt(LocalDateTime.now())
                .build();

        Hospital savedHospital = hospitalRepository.save(hospital);
        return convertToResponseDTO(savedHospital);
    }

    public HospitalResponseDTO getHospitalById(String hospitalId) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with id: " + hospitalId));
        return convertToResponseDTO(hospital);
    }

    public List<HospitalResponseDTO> getAllHospitals() {
        return hospitalRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<HospitalResponseDTO> getHospitalsByCity(String city) {
        return hospitalRepository.findByCityAndIsActiveTrue(city).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<HospitalResponseDTO> getHospitalsByProvince(String province) {
        return hospitalRepository.findByProvinceAndIsActiveTrue(province).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<HospitalResponseDTO> getHospitalsByType(HospitalType type) {
        return hospitalRepository.findByType(type).stream()
                .filter(hospital -> hospital.getIsActive())
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<HospitalResponseDTO> getActiveHospitals() {
        return hospitalRepository.findByIsActiveTrue().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public HospitalResponseDTO updateHospital(String hospitalId, HospitalRequestDTO hospitalRequestDTO) {
        Hospital existingHospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with id: " + hospitalId));

        existingHospital.setName(hospitalRequestDTO.getName());
        existingHospital.setAddress(hospitalRequestDTO.getAddress());
        existingHospital.setCity(hospitalRequestDTO.getCity());
        existingHospital.setProvince(hospitalRequestDTO.getProvince());
        existingHospital.setTelephone(hospitalRequestDTO.getTelephone());
        existingHospital.setEmail(hospitalRequestDTO.getEmail());
        existingHospital.setType(hospitalRequestDTO.getType());
        existingHospital.setIsActive(hospitalRequestDTO.getIsActive());

        Hospital updatedHospital = hospitalRepository.save(existingHospital);
        return convertToResponseDTO(updatedHospital);
    }

    public void deleteHospital(String hospitalId) {
        if (!hospitalRepository.existsById(hospitalId)) {
            throw new ResourceNotFoundException("Hospital not found with id: " + hospitalId);
        }
        hospitalRepository.deleteById(hospitalId);
    }

    private HospitalResponseDTO convertToResponseDTO(Hospital hospital) {
        return HospitalResponseDTO.builder()
                .hospitalId(hospital.getHospitalId())
                .name(hospital.getName())
                .address(hospital.getAddress())
                .city(hospital.getCity())
                .province(hospital.getProvince())
                .telephone(hospital.getTelephone())
                .email(hospital.getEmail())
                .type(hospital.getType())
                .isActive(hospital.getIsActive())
                .createdAt(hospital.getCreatedAt())
                .build();
    }
}
