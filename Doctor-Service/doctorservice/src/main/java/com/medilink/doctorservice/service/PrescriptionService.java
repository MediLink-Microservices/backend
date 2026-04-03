package com.medilink.doctorservice.service;

import com.medilink.doctorservice.dto.PrescriptionDTO;
import com.medilink.doctorservice.dto.PrescriptionRequestDTO;
import com.medilink.doctorservice.entity.Prescription;
import com.medilink.doctorservice.exception.ResourceNotFoundException;
import com.medilink.doctorservice.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;

    public PrescriptionDTO createPrescription(PrescriptionRequestDTO prescriptionRequestDTO) {
        Prescription prescription = Prescription.builder()
                .doctorId(prescriptionRequestDTO.getDoctorId())
                .patientId(prescriptionRequestDTO.getPatientId())
                .medicines(prescriptionRequestDTO.getMedicines())
                .notes(prescriptionRequestDTO.getNotes())
                .createdAt(LocalDateTime.now())
                .build();

        Prescription savedPrescription = prescriptionRepository.save(prescription);
        return convertToDTO(savedPrescription);
    }

    public PrescriptionDTO getPrescriptionById(String prescriptionId) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found with id: " + prescriptionId));
        return convertToDTO(prescription);
    }

    public List<PrescriptionDTO> getPrescriptionsByPatientId(String patientId) {
        return prescriptionRepository.findByPatientId(patientId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PrescriptionDTO> getPrescriptionsByDoctorId(String doctorId) {
        return prescriptionRepository.findByDoctorId(doctorId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PrescriptionDTO> getAllPrescriptions() {
        return prescriptionRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void deletePrescription(String prescriptionId) {
        if (!prescriptionRepository.existsById(prescriptionId)) {
            throw new ResourceNotFoundException("Prescription not found with id: " + prescriptionId);
        }
        prescriptionRepository.deleteById(prescriptionId);
    }

    private PrescriptionDTO convertToDTO(Prescription prescription) {
        return PrescriptionDTO.builder()
                .prescriptionId(prescription.getPrescriptionId())
                .doctorId(prescription.getDoctorId())
                .patientId(prescription.getPatientId())
                .medicines(prescription.getMedicines())
                .notes(prescription.getNotes())
                .createdAt(prescription.getCreatedAt())
                .build();
    }
}
