package EmployeeManagementSystem.service;

import EmployeeManagementSystem.controller.AttendanceTrackingController;
import EmployeeManagementSystem.entity.Attendance;
import EmployeeManagementSystem.entity.AttendanceTracking;
import EmployeeManagementSystem.entity.Employee;
import EmployeeManagementSystem.repository.AttendanceTrackingRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttendanceTrackingServiceImpl implements AttendanceTrackingService {

    private final AttendanceTrackingRepository attendanceTrackingRepository;



    @Override
    public void markLogin(Employee employee) {

        System.out.println("===== markLogin Called =====");

        AttendanceTracking attendanceTracking = new AttendanceTracking();

        attendanceTracking.setEmployeeId(employee.getId());
        attendanceTracking.setEmployeeName(employee.getName());
        attendanceTracking.setDate(LocalDate.now());
        attendanceTracking.setLoginTime(LocalDateTime.now());
        attendanceTracking.setStatus("Present");

        attendanceTrackingRepository.save(attendanceTracking);

        System.out.println("===== Attendance Saved Successfully =====");
    }

    @Override
    public void markLogout(Long employeeId) {

        AttendanceTracking attendanceTracking =
                attendanceTrackingRepository.findTopByEmployeeIdAndDateOrderByIdDesc(
                        employeeId,
                        LocalDate.now()
                );

        if (attendanceTracking != null) {

            LocalDateTime logoutTime = LocalDateTime.now();
            attendanceTracking.setLogoutTime(logoutTime);

            Duration duration = Duration.between(
                    attendanceTracking.getLoginTime(),
                    logoutTime
            );

            double workingHours = duration.toMinutes() / 60.0;
            attendanceTracking.setWorkingHours(workingHours);

//            attendanceTrackingRepository.save(attendanceTracking);
            attendanceTrackingRepository.save(attendanceTracking);

            System.out.println("Saved ID = " + attendanceTracking.getId());
        }
    }

    @Override
    public AttendanceTracking getTodayAttendance(Long employeeId) {
        return attendanceTrackingRepository
                .findTopByEmployeeIdAndDateOrderByIdDesc(
                        employeeId,
                        LocalDate.now());
    }

//    @Override
//    public List<AttendanceTracking> getAttendanceHistory(Long employeeId) {
//        return attendanceTrackingRepository.findByEmployeeIdAndDateOrderByIdDesc(employeeId);
//    }
@Override
public List<AttendanceTracking> getAttendanceHistory(Long employeeId) {
    return attendanceTrackingRepository
            .findByEmployeeIdOrderByDateDesc(employeeId);
}
    @Override
    public LocalDateTime getPunchInTimeForToday(Long employeeId) {

        Optional<AttendanceTracking> attendanceOpt = attendanceTrackingRepository.findTodayAttendanceByEmployeeId(employeeId);
        if (attendanceOpt.isPresent()) {
            return attendanceOpt.get().getLoginTime();
        }

        return null;
    }
    public List<AttendanceTracking> getAttendanceLogsByEmployeeId(Long employeeId) {
        // 1. Database se saare logs nikalen
        List<AttendanceTracking> logs = attendanceTrackingRepository.findByEmployeeId(employeeId);

        // 2. (Optional Fix) Agar working hours DB me null hain aur logout time maujood hai,
        // toh hum runtime par calculate karke UI ke liye set kar sakte hain.
        for (AttendanceTracking log : logs) {
            LocalDateTime logoutTime = LocalDateTime.now();
            log.setLogoutTime(logoutTime);

            Duration duration = Duration.between(
                    log.getLoginTime(),
                    logoutTime
            );

            double workingHours = duration.toMinutes() / 60.0;
            log.setWorkingHours(workingHours);

            attendanceTrackingRepository.save(log);

//            if (log.getWorkingHours() == null && log.getLoginTime() != null && log.getLogoutTime() != null) {
//                try {
//                    // Maan lijiye aapka time String format me hai ya LocalTime me,
//                    // Calculate duration logic (agar jarurat ho):
//                    // long mins = Duration.between(log.getLoginTime(), log.getLogoutTime()).toMinutes();
//                    // log.setWorkHours((mins / 60) + "h " + (mins % 60) + "m");
//                } catch (Exception e) {
//                    log.setWorkingHours(Double.valueOf("-"));
//                }
//            }
        }

        return logs;
    }

//    @Override
//    public List<AttendanceTracking> getAttendanceHistory(Long employeeId) {
//
//        return attendanceTrackingRepository
//                .findByEmployeeIdOrderByDateDesc(employeeId);
//    }
}