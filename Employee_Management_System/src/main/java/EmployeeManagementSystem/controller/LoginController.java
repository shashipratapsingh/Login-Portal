package EmployeeManagementSystem.controller;

import EmployeeManagementSystem.dto.LoginRequest;
import EmployeeManagementSystem.entity.AttendanceTracking;
import EmployeeManagementSystem.entity.RegisterEmployee;
import EmployeeManagementSystem.jwt.JwtUtil;
import EmployeeManagementSystem.repository.AttendanceTrackingRepository;
import EmployeeManagementSystem.service.AttendanceService;
import EmployeeManagementSystem.service.RegisterEmployeeService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class LoginController {
    private final RegisterEmployeeService service;
    private final AttendanceTrackingRepository attendanceTrackingRepository;
    private  final JwtUtil jwtUtil;

    @GetMapping("/employeeRegisterForm")
    public String openForm(Model model){
        model.addAttribute("registerEmployee",new RegisterEmployee());
        return "register";
    }
    @PostMapping("/register")
    public String createUser(@ModelAttribute RegisterEmployee registerEmployee,Model model)
    {
         String generatedPassword=service.registerEmployee(registerEmployee);
        model.addAttribute("message", "Employee Registered Successfully!");
        model.addAttribute("generatedId", registerEmployee.getUserId());
        model.addAttribute("generatedPassword", generatedPassword);

        return "register-success";
    }
    @GetMapping("/loginPage")
    public String openLoginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }
    @PostMapping("/login")
    public String login(@ModelAttribute("loginRequest") LoginRequest request,
                        HttpServletResponse response, Model model) {
        try {

            String token = service.login(request);

            Cookie jwtCookie = new Cookie("jwtToken", token);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(60 * 60);

            response.addCookie(jwtCookie);
            String role = jwtUtil.extractRole(token);

            System.out.println("ROLE = " + role);

//            HttpSession session = (HttpSession) request.getSession();

            Long employeeId = 1L;
            String employeeName = "Employee";

//            session.setAttribute("employeeId", employeeId);
//            session.setAttribute("employeeName", employeeName);

            AttendanceTracking attendance = new AttendanceTracking();
            attendance.setEmployeeId(employeeId);
            attendance.setEmployeeName(employeeName);
            attendance.setDate(LocalDate.now());
            attendance.setLoginTime(LocalDateTime.now());
            attendance.setStatus("Present");

            attendanceTrackingRepository.save(attendance);


            System.out.println("Attendance Saved Successfully");

            if ("ROLE_EMPLOYEE".equalsIgnoreCase(role)){
                return "redirect:/employee/dashboard";
            }
            if ("ROLE_MANAGER".equalsIgnoreCase(role)){
                return "redirect:/manager/dashboard";
            }
            if ("ROLE_ADMIN".equalsIgnoreCase(role)){
                return "redirect:/admin/dashboard";
            }
            if ("ROLE_HR".equalsIgnoreCase(role)){
                return "redirect:/hr/dashboard";
            }
            return "redirect:/login";

        } catch (Exception e) {

            model.addAttribute("error",
                    "Invalid Credentials: " + e.getMessage());
            model.addAttribute("loginRequest",new LoginRequest());

            return "login";
        }
    }
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, Model model) {
        try {
            service.sendOtpProcessing(email);
            model.addAttribute("email", email);
            return "reset-password";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "forgot-password";
        }
    }
    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("email") String email,
                                       @RequestParam("otp") String otp,
                                       @RequestParam("newPassword") String newPassword,
                                       Model model) {
        try {
            service.verifyOtpAndChangePassword(email, otp, newPassword);
            model.addAttribute("message", "Password changed successfully! Please login.");
            model.addAttribute("loginRequest", new LoginRequest());
            return "login";
        } catch (Exception e) {
            model.addAttribute("email", email);
            model.addAttribute("error", e.getMessage());

            return "reset-password";
        }
    }
    @GetMapping("/logout")
    public String logout(HttpServletRequest request,
                         HttpServletResponse response) {

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwtToken".equals(cookie.getName())) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    cookie.setHttpOnly(true);

                    response.addCookie(cookie);
                    break;
                }
            }
        }

        return "redirect:/auth/loginPage?logout=true";
    }
}
