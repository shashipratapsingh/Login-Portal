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

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
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
                .csrf(csrf -> csrf.disable())

                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**","/access-denied","/profile/**","/employees/**").permitAll()

                        // PUBLIC ENDPOINTS
                        .requestMatchers("/auth/**", "/access-denied", "/profile/**").permitAll()
                        .requestMatchers("/admin/employees").permitAll()
                        .requestMatchers("/admin/**").permitAll()
//                        .requestMatchers("/admin/salary/salary-dashboard").hasRole("ADMIN")
                        .requestMatchers("/admin/all-profiles-as-employees").permitAll()
                        .requestMatchers("/admin/all/empployees").permitAll()
                                .requestMatchers("/admin/departments/**").permitAll()
                        .requestMatchers("/admin/salary/salary-dashboard").permitAll()
                        .requestMatchers("/notifications/**").permitAll()
                        .requestMatchers("/employee/**").permitAll()
                        .requestMatchers("/employee/attendance-tracking").permitAll()
                        .requestMatchers("/employee/**","/salary/slip/**","/attendance/signoff-logs").hasRole("EMPLOYEE")

                        .requestMatchers("/admin/**").permitAll()

                        .requestMatchers(
                                "/leave/manage",
                                "/leave/status/**",
                                "/timesheet/manage",
                                "/timesheet/status/**",
                                "/manager/profile"
                        ).hasRole("MANAGER")

                        .requestMatchers(
                                "/leave/manage",
                                "/leave/status/**"
                        ).permitAll()


                        .requestMatchers("/leave/apply", "/leave/submit").authenticated()

                        .anyRequest().authenticated()
                )

                // ✅ IMPORTANT FIX: no redirect (prevents response committed error)
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(accessDeniedHandler())
                )

                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Access Denied\"}");
        };
    }
}