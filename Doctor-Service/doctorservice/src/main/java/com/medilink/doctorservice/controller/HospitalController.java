package com.medilink.doctorservice.controller;

import com.medilink.doctorservice.dto.HospitalRequestDTO;
import com.medilink.doctorservice.dto.HospitalResponseDTO;
import com.medilink.doctorservice.entity.HospitalType;
import com.medilink.doctorservice.service.HospitalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hospitals")
@RequiredArgsConstructor
public class HospitalController {

    private final HospitalService hospitalService;

    @PostMapping
    public ResponseEntity<HospitalResponseDTO> createHospital(@Valid @RequestBody HospitalRequestDTO hospitalRequestDTO) {
        HospitalResponseDTO hospital = hospitalService.createHospital(hospitalRequestDTO);
        return new ResponseEntity<>(hospital, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HospitalResponseDTO> getHospitalById(@PathVariable String id) {
        HospitalResponseDTO hospital = hospitalService.getHospitalById(id);
        return ResponseEntity.ok(hospital);
    }

    @GetMapping
    public ResponseEntity<List<HospitalResponseDTO>> getAllHospitals() {
        List<HospitalResponseDTO> hospitals = hospitalService.getAllHospitals();
        return ResponseEntity.ok(hospitals);
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<HospitalResponseDTO>> getHospitalsByCity(@PathVariable String city) {
        List<HospitalResponseDTO> hospitals = hospitalService.getHospitalsByCity(city);
        return ResponseEntity.ok(hospitals);
    }

    @GetMapping("/province/{province}")
    public ResponseEntity<List<HospitalResponseDTO>> getHospitalsByProvince(@PathVariable String province) {
        List<HospitalResponseDTO> hospitals = hospitalService.getHospitalsByProvince(province);
        return ResponseEntity.ok(hospitals);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<HospitalResponseDTO>> getHospitalsByType(@PathVariable HospitalType type) {
        List<HospitalResponseDTO> hospitals = hospitalService.getHospitalsByType(type);
        return ResponseEntity.ok(hospitals);
    }

    @GetMapping("/active")
    public ResponseEntity<List<HospitalResponseDTO>> getActiveHospitals() {
        List<HospitalResponseDTO> hospitals = hospitalService.getActiveHospitals();
        return ResponseEntity.ok(hospitals);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HospitalResponseDTO> updateHospital(
            @PathVariable String id, 
            @Valid @RequestBody HospitalRequestDTO hospitalRequestDTO) {
        HospitalResponseDTO hospital = hospitalService.updateHospital(id, hospitalRequestDTO);
        return ResponseEntity.ok(hospital);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHospital(@PathVariable String id) {
        hospitalService.deleteHospital(id);
        return ResponseEntity.noContent().build();
    }
}
