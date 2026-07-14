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
    public List<Attendance> getAttendanceLogsByEmployeeId(String employeeId) {
        // 1. Database se saare logs nikalen
        List<Attendance> logs = attendanceRepository.findByEmployeeId(employeeId);

        // 2. (Optional Fix) Agar working hours DB me null hain aur logout time maujood hai,
        // toh hum runtime par calculate karke UI ke liye set kar sakte hain.
        for (Attendance log : logs) {
            if (log.getWorkingHours() == null && log.getCheckInTime() != null && log.getCheckOutTime() != null) {
                try {
                    // Maan lijiye aapka time String format me hai ya LocalTime me,
                    // Calculate duration logic (agar jarurat ho):
                    // long mins = Duration.between(log.getLoginTime(), log.getLogoutTime()).toMinutes();
                    // log.setWorkHours((mins / 60) + "h " + (mins % 60) + "m");
                } catch (Exception e) {
                    log.setWorkingHours(Double.valueOf("-"));
                }
            }
        }

        return logs;
    }


}