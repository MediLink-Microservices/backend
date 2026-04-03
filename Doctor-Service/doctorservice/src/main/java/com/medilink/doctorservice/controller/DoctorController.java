package com.medilink.doctorservice.controller;

import com.medilink.doctorservice.dto.DoctorRequestDTO;
import com.medilink.doctorservice.dto.DoctorResponseDTO;
import com.medilink.doctorservice.entity.DoctorStatus;
import com.medilink.doctorservice.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService service;

    @PostMapping
    public ResponseEntity<DoctorResponseDTO> create(@Valid @RequestBody DoctorRequestDTO doctorDTO) {
        DoctorResponseDTO createdDoctor = service.createDoctor(doctorDTO);
        return new ResponseEntity<>(createdDoctor, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<DoctorResponseDTO>> getAll() {
        List<DoctorResponseDTO> doctors = service.getAllDoctors();
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponseDTO> getById(@PathVariable String id) {
        DoctorResponseDTO doctor = service.getDoctorById(id);
        return ResponseEntity.ok(doctor);
    }

    @GetMapping("/search")
    public ResponseEntity<List<DoctorResponseDTO>> getBySpecialty(@RequestParam String specialty) {
        List<DoctorResponseDTO> doctors = service.getDoctorsBySpecialty(specialty);
        return ResponseEntity.ok(doctors);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorResponseDTO> update(@PathVariable String id, 
                                                   @Valid @RequestBody DoctorRequestDTO doctorDTO) {
        DoctorResponseDTO updatedDoctor = service.updateDoctor(id, doctorDTO);
        return ResponseEntity.ok(updatedDoctor);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<DoctorResponseDTO> updateStatus(@PathVariable String id, 
                                                          @RequestParam DoctorStatus status) {
        DoctorResponseDTO updatedDoctor = service.updateDoctorStatus(id, status);
        return ResponseEntity.ok(updatedDoctor);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<DoctorResponseDTO> approveDoctor(@PathVariable String id) {
        DoctorResponseDTO updatedDoctor = service.updateDoctorStatus(id, DoctorStatus.APPROVED);
        return ResponseEntity.ok(updatedDoctor);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<DoctorResponseDTO> rejectDoctor(@PathVariable String id) {
        DoctorResponseDTO updatedDoctor = service.updateDoctorStatus(id, DoctorStatus.REJECTED);
        return ResponseEntity.ok(updatedDoctor);
    }

    @GetMapping("/hospital/{hospitalId}")
    public ResponseEntity<List<DoctorResponseDTO>> getDoctorsByHospital(@PathVariable String hospitalId) {
        List<DoctorResponseDTO> doctors = service.getDoctorsByHospital(hospitalId);
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<DoctorResponseDTO>> getDoctorsByCity(@PathVariable String city) {
        List<DoctorResponseDTO> doctors = service.getDoctorsByCity(city);
        return ResponseEntity.ok(doctors);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable String id) {
        service.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }
}