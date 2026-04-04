package com.medilink.patient_service.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
import java.util.ArrayList;

/**
 * PatientProfile represents the personal information of a patient.
 * It is stored as a document in the 'patients' collection in MongoDB.
 */
@Document(collection = "patients")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientProfile {

    @Id
    private String id; // Unique ID (MongoDB ObjectId)

    private String NIC;
    
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String dateOfBirth;
    
    // List of medical reports associated with this patient
    private List<MedicalRecord> medicalReports = new ArrayList<>();
}
