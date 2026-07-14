package EmployeeManagementSystem.controller;

import EmployeeManagementSystem.entity.AttendanceTracking;
import EmployeeManagementSystem.entity.EmployeeProfile;
import EmployeeManagementSystem.entity.RegisterEmployee;
import EmployeeManagementSystem.jwt.JwtUtil;
import EmployeeManagementSystem.repository.EmployeeProfileRepository;
import EmployeeManagementSystem.service.AttendanceService;
import EmployeeManagementSystem.service.AttendanceTrackingService;
import EmployeeManagementSystem.service.RegisterEmployeeService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CurrentTimestamp;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@ControllerAdvice
@RequiredArgsConstructor
public class SidebarGlobalAdvice {
    private final JwtUtil jwtUtil;
    private final RegisterEmployeeService employeeService;
    private final EmployeeProfileRepository employeeProfileRepository;
    private final AttendanceTrackingService attendanceTrackingService;

    @ModelAttribute
    public void addSidebarData(HttpServletRequest request, Model model) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwtToken".equals(cookie.getName()) && cookie.getValue() != null && !cookie.getValue().isEmpty()) {
                    try {
                        String employeeId = jwtUtil.extractUsername(cookie.getValue());
                        RegisterEmployee emp = employeeService.getEmployeeById(employeeId);

                        if (emp != null) {
                            model.addAttribute("loggedInEmpName", emp.getName());
                            model.addAttribute("loggedInEmpId", emp.getId());
                            model.addAttribute("loggedInEmpDesignation", emp.getDesignation());
                            EmployeeProfile empProfile = employeeProfileRepository.findByUserId(employeeId).orElse(null);
                            model.addAttribute("loggedInEmpPhoto", empProfile != null ? empProfile.getPhoto() : null);
                        }
                        // ─── 3. TIMER IMPLEMENTATION (UPDATED WITH MILLISECONDS) ───
                        long totalSeconds = 0;
                        long loginTimeEpochMilli = 0; // String ki jagah long use karein
                        Long empId=emp.getId();
                        LocalDateTime loginTime = attendanceTrackingService.getPunchInTimeForToday(empId);
                        System.out.println(loginTime);
                        if (loginTime != null) {
                            totalSeconds = Duration.between(loginTime, LocalDateTime.now()).getSeconds();

                            // LocalDateTime ko System ki default timezone ke sath Epoch Milliseconds me badlein
                            loginTimeEpochMilli = loginTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
                        }

                        model.addAttribute("backendTotalSeconds", totalSeconds > 0 ? totalSeconds : 0);
                        model.addAttribute("checkInTime", loginTimeEpochMilli > 0 ? loginTimeEpochMilli : null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

}
}
