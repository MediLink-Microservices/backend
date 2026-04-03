package com.medilink.doctorservice.service;

import com.medilink.doctorservice.dto.DoctorRequestDTO;
import com.medilink.doctorservice.dto.DoctorResponseDTO;
import com.medilink.doctorservice.entity.Doctor;
import com.medilink.doctorservice.entity.DoctorStatus;
import com.medilink.doctorservice.exception.ResourceNotFoundException;
import com.medilink.doctorservice.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorResponseDTO createDoctor(DoctorRequestDTO doctorRequestDTO) {
        Doctor doctor = Doctor.builder()
                .name(doctorRequestDTO.getName())
                .email(doctorRequestDTO.getEmail())
                .phone(doctorRequestDTO.getPhone())
                .licenseNumber(doctorRequestDTO.getLicenseNumber())
                .yearsOfExperience(doctorRequestDTO.getYearsOfExperience())
                .specialty(doctorRequestDTO.getSpecialty())
                .workLocations(doctorRequestDTO.getWorkLocations())
                .fee(doctorRequestDTO.getFee())
                .availableForTelemedicine(doctorRequestDTO.getAvailableForTelemedicine() != null ? 
                    doctorRequestDTO.getAvailableForTelemedicine() : false)
                .status(DoctorStatus.PENDING)
                .build();

        Doctor savedDoctor = doctorRepository.save(doctor);
        return convertToResponseDTO(savedDoctor);
    }

    public DoctorResponseDTO getDoctorById(String doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));
        return convertToResponseDTO(doctor);
    }

    public List<DoctorResponseDTO> getAllDoctors() {
        return doctorRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<DoctorResponseDTO> getDoctorsBySpecialty(String specialty) {
        return doctorRepository.findBySpecialty(specialty).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public DoctorResponseDTO updateDoctor(String doctorId, DoctorRequestDTO doctorRequestDTO) {
        Doctor existingDoctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));

        existingDoctor.setName(doctorRequestDTO.getName());
        existingDoctor.setEmail(doctorRequestDTO.getEmail());
        existingDoctor.setPhone(doctorRequestDTO.getPhone());
        existingDoctor.setLicenseNumber(doctorRequestDTO.getLicenseNumber());
        existingDoctor.setYearsOfExperience(doctorRequestDTO.getYearsOfExperience());
        existingDoctor.setSpecialty(doctorRequestDTO.getSpecialty());
        existingDoctor.setWorkLocations(doctorRequestDTO.getWorkLocations());
        existingDoctor.setFee(doctorRequestDTO.getFee());
        existingDoctor.setAvailableForTelemedicine(doctorRequestDTO.getAvailableForTelemedicine());

        Doctor updatedDoctor = doctorRepository.save(existingDoctor);
        return convertToResponseDTO(updatedDoctor);
    }

    public DoctorResponseDTO updateDoctorStatus(String doctorId, DoctorStatus status) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));

        doctor.setStatus(status);
        Doctor updatedDoctor = doctorRepository.save(doctor);
        return convertToResponseDTO(updatedDoctor);
    }

    public void deleteDoctor(String doctorId) {
        if (!doctorRepository.existsById(doctorId)) {
            throw new ResourceNotFoundException("Doctor not found with id: " + doctorId);
        }
        doctorRepository.deleteById(doctorId);
    }

    private DoctorResponseDTO convertToResponseDTO(Doctor doctor) {
        return DoctorResponseDTO.builder()
                .doctorId(doctor.getDoctorId())
                .name(doctor.getName())
                .email(doctor.getEmail())
                .phone(doctor.getPhone())
                .licenseNumber(doctor.getLicenseNumber())
                .yearsOfExperience(doctor.getYearsOfExperience())
                .specialty(doctor.getSpecialty())
                .workLocations(doctor.getWorkLocations())
                .fee(doctor.getFee())
                .availableForTelemedicine(doctor.getAvailableForTelemedicine())
                .status(doctor.getStatus())
                .build();
    }
}
