package com.medilink.paymentservice.repository;

import com.medilink.paymentservice.model.Payment;
import com.medilink.paymentservice.model.PaymentStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {

    Optional<Payment> findByAppointmentId(String appointmentId);

    List<Payment> findByPatientId(String patientId);

    List<Payment> findByStatus(PaymentStatus status);
}
