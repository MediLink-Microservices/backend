package com.medilink.doctorservice.controller;

import com.medilink.doctorservice.dto.ScheduleDTO;
import com.medilink.doctorservice.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ScheduleController {

    private final ScheduleService service;

    @PostMapping
    public ResponseEntity<ScheduleDTO> create(@Valid @RequestBody ScheduleDTO scheduleDTO) {
        ScheduleDTO createdSchedule = service.createSchedule(scheduleDTO);
        return new ResponseEntity<>(createdSchedule, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduleDTO> getById(@PathVariable String id) {
        ScheduleDTO schedule = service.getScheduleById(id);
        return ResponseEntity.ok(schedule);
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<ScheduleDTO>> getByDoctorId(@PathVariable String doctorId) {
        List<ScheduleDTO> schedules = service.getSchedulesByDoctorId(doctorId);
        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/hospital/{hospitalId}")
    public ResponseEntity<List<ScheduleDTO>> getByHospitalId(@PathVariable String hospitalId) {
        List<ScheduleDTO> schedules = service.getSchedulesByHospitalId(hospitalId);
        return ResponseEntity.ok(schedules);
    }

    @GetMapping
    public ResponseEntity<List<ScheduleDTO>> getAll() {
        List<ScheduleDTO> schedules = service.getAllSchedules();
        return ResponseEntity.ok(schedules);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ScheduleDTO> update(@PathVariable String id, 
                                           @Valid @RequestBody ScheduleDTO scheduleDTO) {
        ScheduleDTO updatedSchedule = service.updateSchedule(id, scheduleDTO);
        return ResponseEntity.ok(updatedSchedule);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }
}