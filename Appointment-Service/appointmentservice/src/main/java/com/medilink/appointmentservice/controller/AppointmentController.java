package com.medilink.appointmentservice.controller;

import com.medilink.appointmentservice.dto.CreateAppointmentRequest;
import com.medilink.appointmentservice.model.Appointment;
import com.medilink.appointmentservice.model.AppointmentStatus;
import com.medilink.appointmentservice.service.AppointmentService;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/appointments")
@CrossOrigin(origins = "*")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping("/book")
    public ResponseEntity<Appointment> bookAppointment(@Valid @RequestBody CreateAppointmentRequest request) {
        Appointment appointment = appointmentService.createAppointment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(appointment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Appointment> getAppointment(@PathVariable String id) {
        return appointmentService.getAppointmentById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<AppointmentStatus> getAppointmentStatus(@PathVariable String id) {
        return appointmentService.getAppointmentById(id)
                .map(Appointment::getStatus)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Appointment>> getPatientAppointments(@PathVariable String patientId) {
        return ResponseEntity.ok(appointmentService.getPatientAppointments(patientId));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Appointment>> getDoctorAppointments(@PathVariable String doctorId) {
        return ResponseEntity.ok(appointmentService.getDoctorAppointments(doctorId));
    }

    @GetMapping("/doctor/{doctorId}/availability")
    public ResponseEntity<List<Appointment>> getDoctorAppointmentsWithinRange(
            @PathVariable String doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(appointmentService.getDoctorAppointmentsWithinRange(doctorId, start, end));
    }

    @PutMapping("/{id}/modify")
    public ResponseEntity<Appointment> modifyAppointment(
            @PathVariable String id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newDateTime) {
        return appointmentService.modifyAppointment(id, newDateTime)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<Appointment> cancelAppointment(@PathVariable String id, @RequestParam String reason) {
        return appointmentService.cancelAppointment(id, reason)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Appointment> updateStatus(
            @PathVariable String id,
            @RequestParam AppointmentStatus status) {
        return appointmentService.updateAppointmentStatus(id, status)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Appointment>> getPendingAppointments() {
        return ResponseEntity.ok(appointmentService.getPendingAppointments());
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Appointment Service is running");
    }

    @DeleteMapping("/{id}/permanent-delete")
    public ResponseEntity<String> permanentlyDeleteAppointment(@PathVariable String id) {
        boolean deleted = appointmentService.permanentlyDeleteAppointment(id);
        if (deleted) {
            return ResponseEntity.ok("Appointment permanently deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Appointment not found");
        }
    }
}
