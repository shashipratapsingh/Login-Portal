package EmployeeManagementSystem.repository;

import EmployeeManagementSystem.entity.LeaveRequest;
import EmployeeManagementSystem.enums.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<LeaveRequest,Long> {
    List<LeaveRequest> findByEmployeeId(String employeeId);
    long countByStatus(LeaveStatus status);
}
