package com.medilink.appointmentservice.service;

import com.medilink.appointmentservice.client.DoctorServiceClient;
import com.medilink.appointmentservice.client.PatientServiceClient;
import com.medilink.appointmentservice.client.dto.DoctorDetails;
import com.medilink.appointmentservice.client.dto.PatientDetails;
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
    private final PatientServiceClient patientServiceClient;
    private final DoctorServiceClient doctorServiceClient;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PatientServiceClient patientServiceClient,
            DoctorServiceClient doctorServiceClient) {
        this.appointmentRepository = appointmentRepository;
        this.patientServiceClient = patientServiceClient;
        this.doctorServiceClient = doctorServiceClient;
    }

    public Appointment createAppointment(CreateAppointmentRequest request) {
        PatientDetails patient = patientServiceClient.getPatientById(request.getPatientId());
        DoctorDetails doctor = doctorServiceClient.getDoctorById(request.getDoctorId());

        if (patient == null || patient.getId() == null || patient.getId().isBlank()) {
            throw new IllegalArgumentException("Patient not found or unavailable.");
        }
        if (doctor == null || doctor.getDoctorId() == null || doctor.getDoctorId().isBlank()) {
            throw new IllegalArgumentException("Doctor not found or unavailable.");
        }

        LocalDateTime startTime = request.getAppointmentDateTime();
        LocalDateTime endTime = startTime.plusMinutes(DEFAULT_DURATION_MINUTES);

        boolean doctorHasClash = !appointmentRepository
                .findByDoctorIdAndAppointmentDateTimeBetween(
                        request.getDoctorId(),
                        startTime.minusMinutes(DEFAULT_DURATION_MINUTES),
                        endTime)
                .isEmpty();

        if (doctorHasClash) {
            throw new IllegalArgumentException("Doctor already has an appointment in the selected time range.");
        }

        Appointment appointment = new Appointment();
        appointment.setPatientId(request.getPatientId());
        appointment.setDoctorId(request.getDoctorId());
        appointment.setDoctorName(resolveDoctorName(request, doctor));
        appointment.setDoctorSpecialty(resolveDoctorSpecialty(request, doctor));
        appointment.setDoctorHospital(resolveDoctorHospital(request, doctor));
        appointment.setConsultationFee(resolveConsultationFee(request, doctor));
        appointment.setAppointmentDateTime(startTime);
        appointment.setNotes(request.getNotes());
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

    private String resolveDoctorName(CreateAppointmentRequest request, DoctorDetails doctor) {
        if (doctor.getName() != null && !doctor.getName().isBlank()) {
            return doctor.getName();
        }
        return request.getDoctorName();
    }

    private String resolveDoctorSpecialty(CreateAppointmentRequest request, DoctorDetails doctor) {
        if (doctor.getSpecialty() != null && !doctor.getSpecialty().isBlank()) {
            return doctor.getSpecialty();
        }
        return request.getDoctorSpecialty();
    }

    private String resolveDoctorHospital(CreateAppointmentRequest request, DoctorDetails doctor) {
        if (doctor.getHospitalIds() != null && !doctor.getHospitalIds().isEmpty()) {
            return String.join(", ", doctor.getHospitalIds());
        }
        return request.getDoctorHospital();
    }

    private double resolveConsultationFee(CreateAppointmentRequest request, DoctorDetails doctor) {
        if (doctor.getFee() != null) {
            return doctor.getFee();
        }
        return request.getConsultationFee();
    }
}
