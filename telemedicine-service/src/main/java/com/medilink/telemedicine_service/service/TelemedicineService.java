package com.medilink.telemedicine_service.service;

import com.medilink.telemedicine_service.model.Telemedicine;
import com.medilink.telemedicine_service.dto.CreateTelemedicineRequest;
import com.medilink.telemedicine_service.repository.TelemedicineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * TelemedicineService handles telemedicine session management and Jitsi Meet links.
 */
@Service
public class TelemedicineService {

    private final TelemedicineRepository telemedicineRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    public TelemedicineService(TelemedicineRepository telemedicineRepository) {
        this.telemedicineRepository = telemedicineRepository;
    }

    /**
     * Creates a new telemedicine session with Jitsi URL.
     */
    public Telemedicine createTelemedicineSession(CreateTelemedicineRequest request) {
        Telemedicine telemedicine = new Telemedicine();
        telemedicine.setDoctorId(request.getDoctorId());
        telemedicine.setPatientId(request.getPatientId());
        telemedicine.setPatientName(request.getPatientName());
        telemedicine.setDoctorName(request.getDoctorName());
        telemedicine.setDoctorSpecialty(request.getDoctorSpecialty());
        telemedicine.setConsultationType(request.getConsultationType());
        telemedicine.setAppointmentDateTime(request.getAppointmentDateTime());
        telemedicine.setDurationMinutes(30);
        telemedicine.setJitsiUrl(generateMeetingUrl(UUID.randomUUID().toString()));
        telemedicine.setStatus("SCHEDULED");
        telemedicine.setNotes(request.getNotes());
        telemedicine.setCreatedAt(LocalDateTime.now());
        telemedicine.setUpdatedAt(LocalDateTime.now());

        return telemedicineRepository.save(telemedicine);
    }

    /**
     * Gets all telemedicine sessions for a specific doctor.
     */
    public List<Telemedicine> getDoctorTelemedicineSessions(String doctorId) {
        return telemedicineRepository.findByDoctorId(doctorId);
    }

    /**
     * Gets all telemedicine sessions for a specific patient.
     */
    public List<Telemedicine> getPatientTelemedicineSessions(String patientId) {
        return telemedicineRepository.findByPatientId(patientId);
    }

    /**
     * Generates a unique Jitsi Meet URL for a telemedicine session.
     */
    public String generateMeetingUrl(String sessionId) {
        String uniqueRoom = "medilink-" + sessionId + "-" + UUID.randomUUID().toString().substring(0, 8);
        return "https://meet.jit.si/" + uniqueRoom;
    }

    /**
     * Updates telemedicine session status.
     */
    public Telemedicine updateSessionStatus(String sessionId, String status) {
        return telemedicineRepository.findById(sessionId).map(session -> {
            session.setStatus(status);
            session.setUpdatedAt(LocalDateTime.now());
            return telemedicineRepository.save(session);
        }).orElse(null);
    }
}
