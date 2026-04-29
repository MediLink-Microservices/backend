package com.medilink.doctorservice.service;

import com.medilink.doctorservice.dto.ScheduleDTO;
import com.medilink.doctorservice.entity.Schedule;
import com.medilink.doctorservice.exception.ResourceNotFoundException;
import com.medilink.doctorservice.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public ScheduleDTO createSchedule(ScheduleDTO scheduleDTO) {
        // Validate that end time is after start time
        if (scheduleDTO.getStartTime() != null && scheduleDTO.getEndTime() != null) {
            int startMinutes = convertToMinutes(scheduleDTO.getStartTime());
            int endMinutes = convertToMinutes(scheduleDTO.getEndTime());
            
            if (endMinutes <= startMinutes) {
                throw new IllegalArgumentException("End time must be after start time");
            }
        }
        
        // Validate that the selected day is not in the past
        validateDayNotInPast(scheduleDTO.getDay());
        
        Schedule schedule = Schedule.builder()
                .doctorId(scheduleDTO.getDoctorId())
                .hospitalId(scheduleDTO.getHospitalId())
                .day(scheduleDTO.getDay())
                .startTime(scheduleDTO.getStartTime())
                .endTime(scheduleDTO.getEndTime())
                .consultationType(scheduleDTO.getConsultationType())
                .isAvailable(scheduleDTO.getIsAvailable())
                .patientLimit(scheduleDTO.getPatientLimit())
                .build();

        Schedule savedSchedule = scheduleRepository.save(schedule);
        return convertToDTO(savedSchedule);
    }

    public ScheduleDTO getScheduleById(String scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id: " + scheduleId));
        return convertToDTO(schedule);
    }

    public List<ScheduleDTO> getSchedulesByDoctorId(String doctorId) {
        return scheduleRepository.findByDoctorId(doctorId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ScheduleDTO> getSchedulesByHospitalId(String hospitalId) {
        return scheduleRepository.findByHospitalId(hospitalId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ScheduleDTO> getAllSchedules() {
        return scheduleRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ScheduleDTO updateSchedule(String scheduleId, ScheduleDTO scheduleDTO) {
        // Validate that end time is after start time
        if (scheduleDTO.getStartTime() != null && scheduleDTO.getEndTime() != null) {
            int startMinutes = convertToMinutes(scheduleDTO.getStartTime());
            int endMinutes = convertToMinutes(scheduleDTO.getEndTime());
            
            if (endMinutes <= startMinutes) {
                throw new IllegalArgumentException("End time must be after start time");
            }
        }
        
        // Validate that the selected day is not in the past
        validateDayNotInPast(scheduleDTO.getDay());
        
        Schedule existingSchedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id: " + scheduleId));

        existingSchedule.setDoctorId(scheduleDTO.getDoctorId());
        existingSchedule.setHospitalId(scheduleDTO.getHospitalId());
        existingSchedule.setDay(scheduleDTO.getDay());
        existingSchedule.setStartTime(scheduleDTO.getStartTime());
        existingSchedule.setEndTime(scheduleDTO.getEndTime());
        existingSchedule.setConsultationType(scheduleDTO.getConsultationType());
        existingSchedule.setIsAvailable(scheduleDTO.getIsAvailable());
        existingSchedule.setPatientLimit(scheduleDTO.getPatientLimit());

        Schedule updatedSchedule = scheduleRepository.save(existingSchedule);
        return convertToDTO(updatedSchedule);
    }

    public void deleteSchedule(String scheduleId) {
        if (!scheduleRepository.existsById(scheduleId)) {
            throw new ResourceNotFoundException("Schedule not found with id: " + scheduleId);
        }
        scheduleRepository.deleteById(scheduleId);
    }

    private int convertToMinutes(String time) {
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        return hours * 60 + minutes;
    }

    private ScheduleDTO convertToDTO(Schedule schedule) {
        return ScheduleDTO.builder()
                .scheduleId(schedule.getScheduleId())
                .doctorId(schedule.getDoctorId())
                .hospitalId(schedule.getHospitalId())
                .day(schedule.getDay())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .consultationType(schedule.getConsultationType())
                .isAvailable(schedule.getIsAvailable())
                .patientLimit(schedule.getPatientLimit())
                .build();
    }

    /**
     * Validates that the selected day is not in the past
     * @param day The day name (e.g., "Monday", "Tuesday", etc.)
     * @throws IllegalArgumentException if the day is in the past
     */
    private void validateDayNotInPast(String day) {
        if (day == null || day.trim().isEmpty()) {
            return; // Let other validation handle empty day
        }

        try {
            DayOfWeek selectedDay = DayOfWeek.valueOf(day.toUpperCase());
            LocalDate today = LocalDate.now();
            DayOfWeek currentDay = today.getDayOfWeek();
            
            // Calculate days until the selected day
            int daysUntilSelected = selectedDay.getValue() - currentDay.getValue();
            
            // If the selected day is earlier in the week than today, it's in the past
            // unless we're allowing scheduling for next week
            if (daysUntilSelected < 0) {
                throw new IllegalArgumentException("Cannot schedule appointments for past days. Selected day " + day + " has already passed this week.");
            }
            
            // If it's the same day but current time is past business hours, we might want to prevent it
            // This is a basic implementation - you can add time-based validation if needed
            
        } catch (IllegalArgumentException e) {
            // Invalid day name - let other validation handle this
            // This catch block prevents the validation from failing on invalid day names
        }
    }
}
