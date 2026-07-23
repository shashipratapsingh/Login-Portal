package EmployeeManagementSystem.config;


import EmployeeManagementSystem.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;


import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // ===== DISABLE UNNECESSARY FEATURES =====
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // ===== STATELESS SESSION =====
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // ===== AUTHORIZATION RULES =====
                .authorizeHttpRequests(auth -> auth

                        // ----- PUBLIC ENDPOINTS (NO AUTHENTICATION REQUIRED) -----
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/access-denied").permitAll()
                        .requestMatchers("/profile/**").permitAll()
                        .requestMatchers("/admin/all-profiles-as-employees").permitAll()
                        .requestMatchers("/admin/all/empployees").permitAll()
                        .requestMatchers("/admin/departments/**").permitAll()
                        .requestMatchers("/admin/salary/salary-dashboard").permitAll()
                        .requestMatchers("/notifications/**").permitAll()
                        .requestMatchers("/error").permitAll()

                        // ----- STATIC RESOURCES -----
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/static/**").permitAll()

                        // ----- EMPLOYEE ENDPOINTS (ROLE_EMPLOYEE required) -----
                        .requestMatchers("/employee/**").hasRole("EMPLOYEE")
                        .requestMatchers("/salary/slip/**").hasRole("EMPLOYEE")
                        .requestMatchers("/attendance/signoff-logs").hasRole("EMPLOYEE")
                        .requestMatchers("/leave/apply").hasRole("EMPLOYEE")
                        .requestMatchers("/leave/submit").hasRole("EMPLOYEE")

                        // ----- MANAGER ENDPOINTS (ROLE_MANAGER required) -----
                        .requestMatchers("/leave/manage").hasRole("MANAGER")
                        .requestMatchers("/leave/status/**").hasRole("MANAGER")
                        .requestMatchers("/timesheet/manage").hasRole("MANAGER")
                        .requestMatchers("/timesheet/status/**").hasRole("MANAGER")
                        .requestMatchers("/manager/profile").hasRole("MANAGER")

                        // ----- ADMIN ENDPOINTS (ROLE_ADMIN required) -----
                        // Salary Structure (MOST SPECIFIC FIRST)
                        .requestMatchers("/admin/salary-structure/**").hasRole("ADMIN")
                        .requestMatchers("/admin/salary/**").hasRole("ADMIN")
                        .requestMatchers("/admin/payroll/**").hasRole("ADMIN")

                        // Company Management
                        .requestMatchers("/admin/company/**").hasRole("ADMIN")
                        .requestMatchers("/admin/branches/**").hasRole("ADMIN")
                        .requestMatchers("/admin/company/locations/**").hasRole("ADMIN")

                        // Employee Management
                        .requestMatchers("/admin/employees/**").hasRole("ADMIN")
                        .requestMatchers("/admin/employee-directory/**").hasRole("ADMIN")
                        .requestMatchers("/admin/employee-status/**").hasRole("ADMIN")

                        // Department & Designation
                        .requestMatchers("/admin/departments/**").hasRole("ADMIN")
                        .requestMatchers("/admin/designations/**").hasRole("ADMIN")

                        // Project Management
                        .requestMatchers("/admin/projects/**").hasRole("ADMIN")

                        // Attendance & Leave
                        .requestMatchers("/admin/attendance-records/**").hasRole("ADMIN")
                        .requestMatchers("/admin/leave-records/**").hasRole("ADMIN")
                        .requestMatchers("/admin/wfh-requests/**").hasRole("ADMIN")

                        // Performance
                        .requestMatchers("/admin/performance-reviews/**").hasRole("ADMIN")

                        // Reports & Settings
                        .requestMatchers("/admin/reports/**").hasRole("ADMIN")
                        .requestMatchers("/admin/settings/**").hasRole("ADMIN")

                        // Dashboard
                        .requestMatchers("/admin/dashboard/**").hasRole("ADMIN")

                        // ----- CATCH-ALL ADMIN ROUTE (LESS SPECIFIC) -----
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // ----- AUTHENTICATED USERS (ANY ROLE) -----
                        .requestMatchers("/leave/**").authenticated()
                        .requestMatchers("/timesheet/**").authenticated()

                        // ----- ALL OTHER REQUESTS -----
                        .anyRequest().authenticated()
                )

                // ===== EXCEPTION HANDLING =====
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler())
                )

                // ===== ADD JWT FILTER =====
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"error\":\"Unauthorized - Please login\", \"timestamp\":\"" + System.currentTimeMillis() + "\"}");
        };
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"error\":\"Access Denied - Insufficient permissions\", \"timestamp\":\"" + System.currentTimeMillis() + "\"}");
        };
    }
}