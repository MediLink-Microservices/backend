package com.medilink.doctorservice.controller;

import com.medilink.doctorservice.dto.PrescriptionDTO;
import com.medilink.doctorservice.dto.PrescriptionRequestDTO;
import com.medilink.doctorservice.service.PrescriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService service;

    @PostMapping
    public ResponseEntity<PrescriptionDTO> create(@Valid @RequestBody PrescriptionRequestDTO prescriptionDTO) {
        PrescriptionDTO createdPrescription = service.createPrescription(prescriptionDTO);
        return new ResponseEntity<>(createdPrescription, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrescriptionDTO> getById(@PathVariable String id) {
        PrescriptionDTO prescription = service.getPrescriptionById(id);
        return ResponseEntity.ok(prescription);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<PrescriptionDTO>> getByPatientId(@PathVariable String patientId) {
        List<PrescriptionDTO> prescriptions = service.getPrescriptionsByPatientId(patientId);
        return ResponseEntity.ok(prescriptions);
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<PrescriptionDTO>> getByDoctorId(@PathVariable String doctorId) {
        List<PrescriptionDTO> prescriptions = service.getPrescriptionsByDoctorId(doctorId);
        return ResponseEntity.ok(prescriptions);
    }

    @GetMapping
    public ResponseEntity<List<PrescriptionDTO>> getAll() {
        List<PrescriptionDTO> prescriptions = service.getAllPrescriptions();
        return ResponseEntity.ok(prescriptions);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.deletePrescription(id);
        return ResponseEntity.noContent().build();
    }
}