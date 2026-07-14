////package EmployeeManagementSystem.repository;
////
//////import com.ems.entity.Employee;
////import EmployeeManagementSystem.entity.Employee;
////import org.springframework.data.domain.Page;
////import org.springframework.data.domain.Pageable;
////import org.springframework.data.jpa.repository.JpaRepository;
////import org.springframework.stereotype.Repository;
////
////@Repository
////public interface EmployeeRepository
////        extends JpaRepository<Employee, Long> {
////
////    Page<Employee> findByNameContainingIgnoreCase(
////            String name,
////            Pageable pageable);
////
////    Page<Employee> findByEmailContainingIgnoreCase(
////            String email,
////            Pageable pageable);
////
////    Page<Employee> findByDepartmentDepartmentNameContainingIgnoreCase(
////            String departmentName,
////            Pageable pageable);
////
////}
//
//
//
//
//
//
//package EmployeeManagementSystem.repository;
//
//import EmployeeManagementSystem.controller.EmployeeController;
//import EmployeeManagementSystem.entity.Employee;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.*;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public interface EmployeeRepository extends JpaRepository<Employee, Long> {
//    //EmployeeController findByUserId(String userId);
//
//
//    @Query("SELECT e FROM Employee e " +
//            "LEFT JOIN e.department d " +
//            "WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
//            "OR LOWER(e.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
//            "OR LOWER(e.phone) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
//            "OR LOWER(COALESCE(d.departmentName, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))")
//    Page<Employee> searchAll(@Param("keyword") String keyword, Pageable pageable);
//
//    Optional<Object> findByEmail(String email);
//
//
//    @Query("SELECT e FROM Employee e WHERE e.dateOfBirth IS NOT NULL")
//    List<Employee> findAllWithDob();
//
//    @Query("SELECT e FROM Employee e WHERE e.joiningDate IS NOT NULL")
//    List<Employee> findAllWithJoiningDate();
//    // Is month ke saare birthdays nikalne ke liye
//    @Query("SELECT e FROM Employee e WHERE MONTH(e.dateOfBirth) = MONTH(CURRENT_DATE)")
//    List<Employee> findUpcomingBirthdays();
//
//    // WFH employees ke liye
//    List<Employee> findByWorkMode(String workMode); // e.g., "WFH"
//
//
//    static List<Employee> findAll() {
//        return null;
//    }
//}






package EmployeeManagementSystem.repository;

import EmployeeManagementSystem.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // Global Search
    @Query("SELECT e FROM Employee e " +
            "LEFT JOIN e.department d " +
            "WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(e.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(e.phone) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(COALESCE(d.departmentName, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Employee> searchAll(@Param("keyword") String keyword,
                             Pageable pageable);

    // Find employee by email
    Optional<Employee> findByEmail(String email);

    // Employees having DOB
    @Query("SELECT e FROM Employee e WHERE e.dateOfBirth IS NOT NULL")
    List<Employee> findAllWithDob();

    // Employees having Joining Date
    @Query("SELECT e FROM Employee e WHERE e.joiningDate IS NOT NULL")
    List<Employee> findAllWithJoiningDate();

    // Current Month Birthdays
    @Query("SELECT e FROM Employee e WHERE MONTH(e.dateOfBirth) = MONTH(CURRENT_DATE)")
    List<Employee> findUpcomingBirthdays();

    // Work From Home Employees
    List<Employee> findByWorkMode(String workMode);
}