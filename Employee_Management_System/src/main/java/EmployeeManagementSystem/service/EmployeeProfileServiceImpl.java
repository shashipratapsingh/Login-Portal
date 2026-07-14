package EmployeeManagementSystem.service;

import EmployeeManagementSystem.dto.EmployeeCredentialsDTO;
import EmployeeManagementSystem.entity.Employee;
import EmployeeManagementSystem.entity.EmployeeProfile;
import EmployeeManagementSystem.repository.EmployeeProfileRepository;
import EmployeeManagementSystem.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmployeeProfileServiceImpl implements EmployeeProfileService {
    private final EmployeeProfileRepository repository;
    private final EmployeeRepository employeeRepo;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    @Override
    public EmployeeProfile getProfileByUserId(String userId) {
        return repository.findByUserId(userId).orElse(new EmployeeProfile());
    }

    @Override
    public EmployeeCredentialsDTO saveOrUpdateProfile(EmployeeProfile profile, String userId) {
        EmployeeProfile existingProfile = null;
        Employee newEmployee = null;
        String rawPassword = null;
        String generatedUserId = null;

        // If userId is provided, try to find existing profile
        if (userId != null && !userId.isEmpty()) {
            existingProfile = repository.findByUserId(userId).orElse(null);
        }

        if (existingProfile != null) {
            // Update existing profile
            profile.setId(existingProfile.getId());
            profile.setCreatedAt(existingProfile.getCreatedAt());
            profile.setUpdatedAt(LocalDateTime.now());
            profile.setUserId(existingProfile.getUserId());
            profile.setPassword(existingProfile.getPassword());
            profile.setRegisteredAt(existingProfile.getRegisteredAt());
            profile.setEmailSent(existingProfile.isEmailSent());
            repository.save(profile);

            return null; // No new credentials for update
        } else {
            // New employee - generate credentials
            generatedUserId = generateNextEmployeeId();
            profile.setUserId(generatedUserId);

            // Generate random password
            rawPassword = generateRandomPassword(8);
            profile.setPassword(passwordEncoder.encode(rawPassword));

            // Set timestamps
            profile.setCreatedAt(LocalDateTime.now());
            profile.setUpdatedAt(LocalDateTime.now());
            profile.setStatus("ACTIVE");
            profile.setRegisteredAt(LocalDateTime.now());
            profile.setEmailSent(false);

            // Send email with credentials
            sendCredentialsEmail(profile.getEmail(), generatedUserId, rawPassword);
            profile.setEmailSent(true);

            repository.save(profile);

            // Return credentials for popup
            return new EmployeeCredentialsDTO(
                    generatedUserId,
                    rawPassword,
                    profile.getEmail(),
                    profile.getFullName()
            );
        }
    }

    @Override
    public List<EmployeeProfile> getAllProfiles() {
        return repository.findAll();
    }

    @Override
    public Page<EmployeeProfile> getAllProfiles(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Page<EmployeeProfile> getActiveProfiles(Pageable pageable) {
        return repository.findByStatus("ACTIVE", pageable);
    }

    @Override
    public Optional<EmployeeProfile> getProfileById(Long id) {
        return repository.findById(id);
    }

    @Override
    public String uploadPhoto(MultipartFile file, String employeeId) throws IOException {
        EmployeeProfile employee = repository.findByUserId(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee Not Found"));

        String uploadDir = "C:/New folder/uploadFile/";
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFileName = file.getOriginalFilename();
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String fileName = employeeId + fileExtension;

        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        employee.setPhoto(fileName);
        repository.save(employee);

        return fileName;
    }

    @Override
    public boolean existsByUserId(String userId) {
        return repository.existsByUserId(userId);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public void deleteEmployee(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void softDeleteEmployee(Long id) {
        EmployeeProfile employee = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        employee.setStatus("INACTIVE");
        employee.setUpdatedAt(LocalDateTime.now());
        repository.save(employee);
    }

    @Override
    public List<EmployeeProfile> searchEmployees(String keyword) {
        return repository.searchEmployees(keyword);
    }

    @Override
    public String generateUserId() {
        return generateNextEmployeeId();
    }

    @Override
    public void updateEmployee(Long id, EmployeeProfile employee, MultipartFile photoFile) throws IOException {
        EmployeeProfile existingEmployee = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        existingEmployee.setFullName(employee.getFullName());
        existingEmployee.setPhoneNumber(employee.getPhoneNumber());
        existingEmployee.setDob(employee.getDob());
        existingEmployee.setGender(employee.getGender());
        existingEmployee.setBloodGroup(employee.getBloodGroup());
        existingEmployee.setEmail(employee.getEmail());
        existingEmployee.setMaritalStatus(employee.getMaritalStatus());
        existingEmployee.setFieldOfStudy(employee.getFieldOfStudy());
        existingEmployee.setHighestQualification(employee.getHighestQualification());
        existingEmployee.setUniversity(employee.getUniversity());
        existingEmployee.setPassingYear(employee.getPassingYear());
        existingEmployee.setBankName(employee.getBankName());
        existingEmployee.setAccountNumber(employee.getAccountNumber());
        existingEmployee.setAccountHolderName(employee.getAccountHolderName());
        existingEmployee.setIfscCode(employee.getIfscCode());
        existingEmployee.setCurrentAddress(employee.getCurrentAddress());
        existingEmployee.setPermanentAddress(employee.getPermanentAddress());
        existingEmployee.setDepartment(employee.getDepartment());
        existingEmployee.setDesignation(employee.getDesignation());
        existingEmployee.setUpdatedAt(LocalDateTime.now());

        if (photoFile != null && !photoFile.isEmpty()) {
            uploadPhoto(photoFile, existingEmployee.getUserId());
        }

        repository.save(existingEmployee);
    }

    // ===== HELPER METHODS =====

    private String generateNextEmployeeId() {
        return repository.findFirstByOrderByIdDesc()
                .map(lastEmp -> {
                    String lastUserId = lastEmp.getUserId();
                    if (lastUserId != null && lastUserId.startsWith("EMP")) {
                        try {
                            int lastNumber = Integer.parseInt(lastUserId.replace("EMP", ""));
                            return String.format("EMP%04d", lastNumber + 1);
                        } catch (NumberFormatException e) {
                            return "EMP0001";
                        }
                    }
                    return "EMP0001";
                })
                .orElse("EMP0001");
    }

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    private void sendCredentialsEmail(String email, String userId, String password) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Welcome to BlueThink Inc. - Your Employee Credentials");
            message.setText(String.format(
                    "Dear Employee,\n\n" +
                            "Welcome to BlueThink Inc.! Your employee account has been created.\n\n" +
                            "Your Login Credentials:\n" +
                            "------------------------\n" +
                            "User ID: %s\n" +
                            "Password: %s\n" +
                            "------------------------\n\n" +
                            "Please login to the employee portal using these credentials.\n" +
                            "It is recommended to change your password after first login.\n\n" +
                            "Login URL: http://localhost:8080/login\n\n" +
                            "Best Regards,\n" +
                            "BlueThink Inc. HR Team",
                    userId, password
            ));
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }
}