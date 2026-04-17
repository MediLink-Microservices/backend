package com.medilink.appointmentservice.service;

import com.medilink.appointmentservice.client.DoctorServiceClient;
import com.medilink.appointmentservice.client.PatientServiceClient;
import com.medilink.appointmentservice.client.dto.DoctorDetails;
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
    private static final String DEFAULT_HOSPITAL_LABEL = "Assigned Hospital";

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
        LocalDateTime startTime = request.getAppointmentDateTime();
        validateBookingRequest(request, startTime);

        DoctorDetails doctorDetails = doctorServiceClient.getDoctorById(request.getDoctorId());
        patientServiceClient.getPatientById(request.getPatientId());

        Appointment appointment = new Appointment();
        appointment.setPatientId(request.getPatientId());
        appointment.setDoctorId(doctorDetails.getDoctorId());
        appointment.setDoctorName(resolveDoctorName(request, doctorDetails));
        appointment.setDoctorSpecialty(resolveDoctorSpecialty(request, doctorDetails));
        appointment.setDoctorHospital(resolveDoctorHospital(request));
        appointment.setConsultationFee(resolveConsultationFee(request, doctorDetails));
        appointment.setConsultationType(request.getConsultationType());
        appointment.setAppointmentDateTime(startTime);
        appointment.setNotes(request.getNotes());
        appointment.setAppointmentNumber(resolveAppointmentNumber(request, startTime));
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

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .sorted((first, second) -> {
                    LocalDateTime firstDate = first.getAppointmentDateTime();
                    LocalDateTime secondDate = second.getAppointmentDateTime();

                    if (firstDate == null && secondDate == null) {
                        return 0;
                    }
                    if (firstDate == null) {
                        return 1;
                    }
                    if (secondDate == null) {
                        return -1;
                    }
                    return secondDate.compareTo(firstDate);
                })
                .toList();
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

    private void validateBookingRequest(CreateAppointmentRequest request, LocalDateTime appointmentDateTime) {
        if (request.getConsultationType() == null || request.getConsultationType().isBlank()) {
            throw new IllegalArgumentException("Consultation type is required.");
        }

        boolean slotTaken = appointmentRepository.findByDoctorIdAndAppointmentDateTime(
                        request.getDoctorId(), appointmentDateTime)
                .stream()
                .anyMatch(existing -> existing.getStatus() != AppointmentStatus.CANCELLED);

        if (slotTaken) {
            throw new IllegalStateException("This doctor already has an active appointment for the selected time.");
        }
    }

    private String resolveDoctorName(CreateAppointmentRequest request, DoctorDetails doctorDetails) {
        if (doctorDetails.getName() != null && !doctorDetails.getName().isBlank()) {
            return doctorDetails.getName();
        }
        return request.getDoctorName();
    }

    private String resolveDoctorSpecialty(CreateAppointmentRequest request, DoctorDetails doctorDetails) {
        if (doctorDetails.getSpecialty() != null && !doctorDetails.getSpecialty().isBlank()) {
            return doctorDetails.getSpecialty();
        }
        return request.getDoctorSpecialty();
    }

    private String resolveDoctorHospital(CreateAppointmentRequest request) {
        if (request.getDoctorHospital() != null && !request.getDoctorHospital().isBlank()) {
            return request.getDoctorHospital();
        }
        return DEFAULT_HOSPITAL_LABEL;
    }

    private double resolveConsultationFee(CreateAppointmentRequest request, DoctorDetails doctorDetails) {
        if (doctorDetails.getFee() != null && doctorDetails.getFee() > 0) {
            return doctorDetails.getFee();
        }
        return request.getConsultationFee();
    }

    private Integer resolveAppointmentNumber(CreateAppointmentRequest request, LocalDateTime appointmentDateTime) {
        if (request.getAppointmentNumber() != null && request.getAppointmentNumber() > 0) {
            return request.getAppointmentNumber();
        }

        return appointmentRepository.findByDoctorId(request.getDoctorId()).stream()
                .filter(existing -> existing.getAppointmentDateTime() != null)
                .filter(existing -> existing.getAppointmentDateTime().toLocalDate().equals(appointmentDateTime.toLocalDate()))
                .filter(existing -> existing.getAppointmentNumber() != null)
                .filter(existing -> existing.getStatus() != AppointmentStatus.CANCELLED)
                .mapToInt(Appointment::getAppointmentNumber)
                .max()
                .orElse(0) + 1;
    }
}
