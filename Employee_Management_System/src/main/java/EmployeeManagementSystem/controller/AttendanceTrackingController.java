package EmployeeManagementSystem.controller;

import EmployeeManagementSystem.entity.AttendanceTracking;
import EmployeeManagementSystem.entity.RegisterEmployee;
import EmployeeManagementSystem.service.AttendanceTrackingService;
import EmployeeManagementSystem.service.RegisterEmployeeService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/employee")
@RequiredArgsConstructor
public class AttendanceTrackingController {

    private final AttendanceTrackingService attendanceTrackingService;
    private final RegisterEmployeeService registerEmployeeService;

    @GetMapping("/attendance-tracking")
    public String attendanceTracking(HttpSession session,
                                     Model model) {

        Long employeeId = (Long) session.getAttribute("employeeId");

        System.out.println("Employee ID: " + employeeId);

        if (employeeId != null) {

            AttendanceTracking attendance =
                    attendanceTrackingService.getTodayAttendance(employeeId);

            model.addAttribute("attendance", attendance);
        }

        return "attendanceTracking";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request,
                         HttpServletResponse response) {

        HttpSession session = request.getSession(false);

        if (session != null) {

            Long employeeId =
                    (Long) session.getAttribute("employeeId");

            if (employeeId != null) {
                attendanceTrackingService.markLogout(employeeId);
            }

            session.invalidate();
        }

        Cookie cookie = new Cookie("jwtToken", "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);

        return "redirect:/auth/loginPage?logout=true";
    }

    @GetMapping("/signoff-logs")
    public String getSignoffLogs(Model model) {
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        AttendanceTracking attendance = new AttendanceTracking();
        attendance.setLogoutTime(LocalDateTime.now());

        String employeeId = authentication.getName(); // e.g., BT001
        RegisterEmployee employee=registerEmployeeService.getEmployeeById(employeeId);
        Long id=employee.getId();

        // 1. Database se is employee ke saare attendance records nikalen
        // (Aap apne actual service/repository method ka naam use karein)
        List<AttendanceTracking> logs = attendanceTrackingService.getAttendanceLogsByEmployeeId(id);

        model.addAttribute("attendanceLogs", logs);
        model.addAttribute("currentPage", "signoff"); // Sidebar ko active dikhane ke liye

        return "signoff-details";
    }
}