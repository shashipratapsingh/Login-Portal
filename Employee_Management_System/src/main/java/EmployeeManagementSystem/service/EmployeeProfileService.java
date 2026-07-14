package EmployeeManagementSystem.service;

import EmployeeManagementSystem.dto.EmployeeCredentialsDTO;
import EmployeeManagementSystem.entity.EmployeeProfile;
import EmployeeManagementSystem.repository.EmployeeProfileRepository;
import org.springframework.data.domain.Page;        // ✅ CORRECT
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface EmployeeProfileService {
//    public static List<EmployeeProfile> getAllProfiles() {
//        return EmployeeProfileRepository.findAll();   // ✅ REAL DATA FROM DB
//    }

    EmployeeProfile getProfileByUserId(String userId);
    EmployeeCredentialsDTO saveOrUpdateProfile(EmployeeProfile profile, String userId);
    List<EmployeeProfile> getAllProfiles();
    Page<EmployeeProfile> getAllProfiles(Pageable pageable);
    Page<EmployeeProfile> getActiveProfiles(Pageable pageable);
    Optional<EmployeeProfile> getProfileById(Long id);
    String uploadPhoto(MultipartFile file, String employeeId) throws IOException;
    boolean existsByUserId(String userId);
    boolean existsByEmail(String email);
    void deleteEmployee(Long id);
    void softDeleteEmployee(Long id);
    List<EmployeeProfile> searchEmployees(String keyword);
    String generateUserId();
    void updateEmployee(Long id, EmployeeProfile employee, MultipartFile photoFile) throws IOException;
}
