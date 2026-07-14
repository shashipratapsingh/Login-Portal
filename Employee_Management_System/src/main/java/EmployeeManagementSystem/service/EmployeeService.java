package EmployeeManagementSystem.service;

import EmployeeManagementSystem.dto.AnniversaryDTO;
import EmployeeManagementSystem.dto.BirthdayDTO;
import EmployeeManagementSystem.entity.Employee;
import EmployeeManagementSystem.entity.RegisterEmployee;
import EmployeeManagementSystem.repository.EmployeeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmployeeService {

    Employee saveEmployee(Employee employee);

    Employee updateEmployee(Long id, Employee employee);

    void deleteEmployee(Long id);

    Employee getEmployeeById(Long id);


    Page<Employee> getAllEmployees(int pageNo, int pageSize, String sortBy);


    Page<Employee> searchEmployee(String keyword, Pageable pageable);


    List<Employee> getAllEmployeesList();

    Employee findByEmail(String email);

    long totalEmployees();


    List<BirthdayDTO> getUpcomingBirthdays();
    List<AnniversaryDTO> getUpcomingAnniversaries();
}