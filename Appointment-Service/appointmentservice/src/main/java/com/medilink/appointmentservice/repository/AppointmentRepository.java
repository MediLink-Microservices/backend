package com.medilink.appointmentservice.repository;

import com.medilink.appointmentservice.model.Appointment;
import com.medilink.appointmentservice.model.AppointmentStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends MongoRepository<Appointment, String> {

    List<Appointment> findByPatientId(String patientId);

    List<Appointment> findByDoctorId(String doctorId);

    List<Appointment> findByStatus(AppointmentStatus status);

    List<Appointment> findByDoctorIdAndAppointmentDateTimeBetween(
        String doctorId,
        LocalDateTime startDate,
        LocalDateTime endDate
    );

    List<Appointment> findByPatientIdAndStatus(String patientId, AppointmentStatus status);
}
