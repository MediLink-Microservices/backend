package com.medilink.doctorservice.service;

import com.medilink.doctorservice.dto.PatientDTO;
import com.medilink.doctorservice.dto.PrescriptionDTO;
import com.medilink.doctorservice.dto.PrescriptionRequestDTO;
import com.medilink.doctorservice.entity.Prescription;
import com.medilink.doctorservice.exception.ResourceNotFoundException;
import com.medilink.doctorservice.repository.PrescriptionRepository;
import com.medilink.doctorservice.client.PatientServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PatientServiceClient patientServiceClient;

    public PrescriptionDTO createPrescription(PrescriptionRequestDTO prescriptionRequestDTO) {
        // Validate patient exists by calling patient service
        PatientDTO patient = patientServiceClient.getPatientById(prescriptionRequestDTO.getPatientId());
        
        Prescription prescription = Prescription.builder()
                .doctorId(prescriptionRequestDTO.getDoctorId())
                .patientId(prescriptionRequestDTO.getPatientId())
                .diagnosis(prescriptionRequestDTO.getDiagnosis())
                .medicines(prescriptionRequestDTO.getMedicines())
                .dosageInstructions(prescriptionRequestDTO.getDosageInstructions())
                .duration(prescriptionRequestDTO.getDuration())
                .notes(prescriptionRequestDTO.getNotes())
                .prescribedDate(LocalDateTime.now())
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

    public List<PrescriptionDTO> getPrescriptionsByPatient(String patientId) {
        // Validate patient exists
        PatientDTO patient = patientServiceClient.getPatientById(patientId);
        
        return prescriptionRepository.findByPatientId(patientId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PrescriptionDTO> getPrescriptionsByDoctor(String doctorId) {
        return prescriptionRepository.findByDoctorId(doctorId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private PrescriptionDTO convertToDTO(Prescription prescription) {
        return PrescriptionDTO.builder()
                .prescriptionId(prescription.getPrescriptionId())
                .doctorId(prescription.getDoctorId())
                .patientId(prescription.getPatientId())
                .diagnosis(prescription.getDiagnosis())
                .medicines(prescription.getMedicines())
                .dosageInstructions(prescription.getDosageInstructions())
                .duration(prescription.getDuration())
                .notes(prescription.getNotes())
                .prescribedDate(prescription.getPrescribedDate())
                .createdAt(prescription.getCreatedAt())
                .build();
    }
}
