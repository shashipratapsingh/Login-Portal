package EmployeeManagementSystem.repository;

import EmployeeManagementSystem.entity.EmployeeProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeProfileRepository extends JpaRepository<EmployeeProfile, Long> {

    Optional<EmployeeProfile> findByUserId(String userId);

    Optional<EmployeeProfile> findByEmail(String email);

    List<EmployeeProfile> findByStatus(String status);

    Page<EmployeeProfile> findByStatus(String status, Pageable pageable);

    // ADD THIS METHOD - to get the last employee for ID generation
    Optional<EmployeeProfile> findFirstByOrderByIdDesc();

    @Query("SELECT e FROM EmployeeProfile e WHERE LOWER(e.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(e.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(e.userId) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<EmployeeProfile> searchEmployees(@Param("keyword") String keyword);

    boolean existsByUserId(String userId);

    boolean existsByEmail(String email);
}