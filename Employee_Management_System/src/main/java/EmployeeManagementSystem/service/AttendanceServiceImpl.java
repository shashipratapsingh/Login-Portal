package EmployeeManagementSystem.service;


import EmployeeManagementSystem.entity.Attendance;
import EmployeeManagementSystem.entity.WfhRequest;
import EmployeeManagementSystem.enums.WorkMode;
import EmployeeManagementSystem.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {
    //just for trail
    private final AttendanceRepository attendanceRepository;


    @Override
    public Attendance saveAttendance(Attendance attendance) {

        return attendanceRepository.save(
                attendance);
    }

    @Override
    public List<Attendance> getAllAttendance() {

        return attendanceRepository.findAll();
    }

    @Override
    public Attendance getAttendanceById(
            Long id) {

        return attendanceRepository.findById(id)
                .orElse(null);
    }

    @Override
    public void deleteAttendance(Long id) {

        attendanceRepository.deleteById(id);
    }
    public List<Attendance> getTodayAttendance() {
        return attendanceRepository.findByAttendanceDate(LocalDate.now());
    }

    public List<Attendance> getTodayWFHEmployees() {

        return attendanceRepository
                .findByAttendanceDateAndWorkMode(
                        LocalDate.now(),
                        WorkMode.WFH
                );

    }


}