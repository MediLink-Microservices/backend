package com.medilink.patient_service.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.medilink.patient_service.model.MedicalRecord;
import com.medilink.patient_service.model.PatientProfile;
import com.medilink.patient_service.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * PatientService handles the core business logic: patient management and file uploads.
 */
@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private Cloudinary cloudinary;

    /**
     * Finds a patient by their ID.
     */
    public Optional<PatientProfile> findById(String id) {
        return patientRepository.findById(id);
    }

    /**
     * Creates or updates a patient profile.
     */
    public PatientProfile saveProfile(PatientProfile profile) {
        return patientRepository.save(profile);
    }

    /**
     * Deletes a patient profile.
     */
    public void deleteProfile(String id) {
        patientRepository.deleteById(id);
    }

    /**
     * Upload medical report to Cloudinary and update patient record.
     */
    public PatientProfile uploadReport(String patientId, String title, MultipartFile file) throws IOException {
        // Upload the file to Cloudinary with specific tags or folders
        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "resource_type", "auto",
                "folder", "medilink/reports/" + patientId
        ));

        // Create MedicalRecord metadata with Cloudinary URL
        MedicalRecord record = new MedicalRecord();
        record.setRecordId(UUID.randomUUID().toString());
        record.setTitle(title);
        record.setFileUrl((String) uploadResult.get("url"));
        record.setFileType(file.getContentType());
        record.setUploadDate(new Date());

        // Update the patient's record list in MongoDB
        PatientProfile patient = findById(patientId).orElseThrow(() -> new RuntimeException("Patient not found"));
        patient.getMedicalReports().add(record);
        return patientRepository.save(patient);
    }
}
