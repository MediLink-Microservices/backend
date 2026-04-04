package com.medilink.patient_service.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;

/**
 * MedicalRecord represents a single medical report metadata.
 * It is embedded within the PatientProfile document in MongoDB.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecord {

    private String recordId; // Unique ID for this specific record
    private String title;    // Type or title of report (e.g. Blood Test, MRI)
    private String fileUrl;  // Public URL from Cloudinary (Cloud-storage URL)
    private String fileType; // PDF, Image, etc.
    private Date uploadDate; // Date when information was uploaded
}
