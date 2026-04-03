package com.medilink.doctorservice.service;

import com.medilink.doctorservice.dto.ScheduleDTO;
import com.medilink.doctorservice.entity.Schedule;
import com.medilink.doctorservice.exception.ResourceNotFoundException;
import com.medilink.doctorservice.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public ScheduleDTO createSchedule(ScheduleDTO scheduleDTO) {
        Schedule schedule = Schedule.builder()
                .doctorId(scheduleDTO.getDoctorId())
                .day(scheduleDTO.getDay())
                .startTime(scheduleDTO.getStartTime())
                .endTime(scheduleDTO.getEndTime())
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

    public List<ScheduleDTO> getAllSchedules() {
        return scheduleRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ScheduleDTO updateSchedule(String scheduleId, ScheduleDTO scheduleDTO) {
        Schedule existingSchedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id: " + scheduleId));

        existingSchedule.setDoctorId(scheduleDTO.getDoctorId());
        existingSchedule.setDay(scheduleDTO.getDay());
        existingSchedule.setStartTime(scheduleDTO.getStartTime());
        existingSchedule.setEndTime(scheduleDTO.getEndTime());
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

    private ScheduleDTO convertToDTO(Schedule schedule) {
        return ScheduleDTO.builder()
                .scheduleId(schedule.getScheduleId())
                .doctorId(schedule.getDoctorId())
                .day(schedule.getDay())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .patientLimit(schedule.getPatientLimit())
                .build();
    }
}
