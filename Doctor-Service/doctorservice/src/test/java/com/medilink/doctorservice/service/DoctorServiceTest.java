package com.medilink.doctorservice.service;

import com.medilink.doctorservice.dto.DoctorRequestDTO;
import com.medilink.doctorservice.dto.DoctorResponseDTO;
import com.medilink.doctorservice.entity.Doctor;
import com.medilink.doctorservice.entity.DoctorStatus;
import com.medilink.doctorservice.repository.DoctorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private DoctorService doctorService;

    private Doctor doctor;
    private DoctorRequestDTO doctorRequestDTO;

    @BeforeEach
    void setUp() {
        doctor = Doctor.builder()
                .doctorId("1")
                .name("Dr. John Doe")
                .email("john.doe@example.com")
                .specialty("Cardiology")
                .hospital("General Hospital")
                .fee(500.0)
                .status(DoctorStatus.PENDING)
                .build();

        doctorRequestDTO = DoctorRequestDTO.builder()
                .name("Dr. John Doe")
                .email("john.doe@example.com")
                .specialty("Cardiology")
                .hospital("General Hospital")
                .fee(500.0)
                .build();
    }

    @Test
    void createDoctor_ShouldReturnDoctorResponseDTO() {
        when(doctorRepository.save(any(Doctor.class))).thenReturn(doctor);

        DoctorResponseDTO result = doctorService.createDoctor(doctorRequestDTO);

        assertNotNull(result);
        assertEquals("Dr. John Doe", result.getName());
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals("Cardiology", result.getSpecialty());
        assertEquals(DoctorStatus.PENDING, result.getStatus());
        verify(doctorRepository, times(1)).save(any(Doctor.class));
    }

    @Test
    void getDoctorById_WhenDoctorExists_ShouldReturnDoctorResponseDTO() {
        when(doctorRepository.findById("1")).thenReturn(Optional.of(doctor));

        DoctorResponseDTO result = doctorService.getDoctorById("1");

        assertNotNull(result);
        assertEquals("1", result.getDoctorId());
        assertEquals("Dr. John Doe", result.getName());
        verify(doctorRepository, times(1)).findById("1");
    }

    @Test
    void getAllDoctors_ShouldReturnListOfDoctorResponseDTOs() {
        List<Doctor> doctors = Arrays.asList(doctor);
        when(doctorRepository.findAll()).thenReturn(doctors);

        List<DoctorResponseDTO> result = doctorService.getAllDoctors();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Dr. John Doe", result.get(0).getName());
        verify(doctorRepository, times(1)).findAll();
    }

    @Test
    void getDoctorsBySpecialty_ShouldReturnListOfDoctorResponseDTOs() {
        List<Doctor> doctors = Arrays.asList(doctor);
        when(doctorRepository.findBySpecialty("Cardiology")).thenReturn(doctors);

        List<DoctorResponseDTO> result = doctorService.getDoctorsBySpecialty("Cardiology");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Cardiology", result.get(0).getSpecialty());
        verify(doctorRepository, times(1)).findBySpecialty("Cardiology");
    }

    @Test
    void updateDoctorStatus_ShouldReturnUpdatedDoctorResponseDTO() {
        when(doctorRepository.findById("1")).thenReturn(Optional.of(doctor));
        when(doctorRepository.save(any(Doctor.class))).thenReturn(doctor);

        DoctorResponseDTO result = doctorService.updateDoctorStatus("1", DoctorStatus.APPROVED);

        assertNotNull(result);
        assertEquals(DoctorStatus.APPROVED, result.getStatus());
        verify(doctorRepository, times(1)).findById("1");
        verify(doctorRepository, times(1)).save(any(Doctor.class));
    }
}
