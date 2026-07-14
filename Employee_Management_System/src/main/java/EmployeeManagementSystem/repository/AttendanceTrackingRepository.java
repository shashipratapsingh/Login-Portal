package EmployeeManagementSystem.repository;

import EmployeeManagementSystem.entity.Attendance;
import EmployeeManagementSystem.entity.AttendanceTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceTrackingRepository
        extends JpaRepository<AttendanceTracking, Long> {

    AttendanceTracking findTopByEmployeeIdAndDateOrderByIdDesc(
            Long employeeId,
            LocalDate date
    );

   //List<AttendanceTracking> findByEmployeeIdAndDateOrderByIdDesc(Long employeeId);

    List<AttendanceTracking> findByEmployeeIdOrderByDateDesc(Long employeeId);
    //@Query("SELECT a FROM Attendance a WHERE a.employeeId = :empId AND FUNCTION('DATE', a.punchInTime) = CURRENT_DATE")
    Optional<AttendanceTracking> findTodayAttendanceByEmployeeId(Long empId);
    List<AttendanceTracking> findByEmployeeId(Long employeeId);
//    AttendanceTracking findTopByEmployeeIdAndDateOrderByIdDesc(
//            String employeeId,
//            LocalDate date
//    );
// Repository mein single first record query limit lagayein
Optional<AttendanceTracking> findFirstByEmployeeIdAndDateOrderByLoginTimeAsc(String employeeId, LocalDate date);
}