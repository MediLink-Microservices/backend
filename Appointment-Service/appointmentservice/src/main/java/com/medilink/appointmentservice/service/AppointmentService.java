package com.medilink.appointmentservice.service;

import com.medilink.appointmentservice.dto.CreateAppointmentRequest;
import com.medilink.appointmentservice.model.Appointment;
import com.medilink.appointmentservice.model.AppointmentStatus;
import com.medilink.appointmentservice.repository.AppointmentRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class AppointmentService {

    private static final int DEFAULT_DURATION_MINUTES = 30;

    private final AppointmentRepository appointmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public Appointment createAppointment(CreateAppointmentRequest request) {
        LocalDateTime startTime = request.getAppointmentDateTime();
        LocalDateTime endTime = startTime.plusMinutes(DEFAULT_DURATION_MINUTES);

        Appointment appointment = new Appointment();
        appointment.setPatientId(request.getPatientId());
        appointment.setDoctorId(request.getDoctorId());
        appointment.setDoctorName(request.getDoctorName());
        appointment.setDoctorSpecialty(request.getDoctorSpecialty());
        appointment.setDoctorHospital(request.getDoctorHospital());
        appointment.setConsultationFee(request.getConsultationFee());
        appointment.setConsultationType(request.getConsultationType());
        appointment.setAppointmentDateTime(startTime);
        appointment.setNotes(request.getNotes());
        appointment.setAppointmentNumber(request.getAppointmentNumber());
        appointment.setStatus(AppointmentStatus.PENDING_PAYMENT);
        appointment.setDurationMinutes(DEFAULT_DURATION_MINUTES);
        appointment.setCreatedAt(LocalDateTime.now());
        appointment.setUpdatedAt(LocalDateTime.now());

        return appointmentRepository.save(appointment);
    }

    public Optional<Appointment> getAppointmentById(String id) {
        return appointmentRepository.findById(id);
    }

    public List<Appointment> getPatientAppointments(String patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    public List<Appointment> getDoctorAppointments(String doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }

    public List<Appointment> getDoctorAppointmentsWithinRange(String doctorId, LocalDateTime start, LocalDateTime end) {
        return appointmentRepository.findByDoctorIdAndAppointmentDateTimeBetween(doctorId, start, end);
    }

    public Optional<Appointment> updateAppointmentStatus(String id, AppointmentStatus status) {
        return appointmentRepository.findById(id).map(existing -> {
            existing.setStatus(status);
            existing.setUpdatedAt(LocalDateTime.now());
            return appointmentRepository.save(existing);
        });
    }

    public Optional<Appointment> modifyAppointment(String id, LocalDateTime newDateTime) {
        return appointmentRepository.findById(id).map(existing -> {
            if (existing.getStatus() == AppointmentStatus.CANCELLED
                    || existing.getStatus() == AppointmentStatus.COMPLETED) {
                throw new IllegalStateException("Completed or cancelled appointments cannot be modified.");
            }

            existing.setAppointmentDateTime(newDateTime);
            existing.setUpdatedAt(LocalDateTime.now());
            return appointmentRepository.save(existing);
        });
    }

    public Optional<Appointment> cancelAppointment(String id, String reason) {
        return appointmentRepository.findById(id).map(existing -> {
            existing.setStatus(AppointmentStatus.CANCELLED);
            existing.setReasonForCancellation(reason);
            existing.setUpdatedAt(LocalDateTime.now());
            return appointmentRepository.save(existing);
        });
    }

    public List<Appointment> getPendingAppointments() {
        return appointmentRepository.findByStatus(AppointmentStatus.PENDING_PAYMENT);
    }

    public boolean permanentlyDeleteAppointment(String id) {
        if (appointmentRepository.existsById(id)) {
            appointmentRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
