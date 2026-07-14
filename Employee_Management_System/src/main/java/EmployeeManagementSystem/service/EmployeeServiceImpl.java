package EmployeeManagementSystem.service;

import EmployeeManagementSystem.dto.AnniversaryDTO;
import EmployeeManagementSystem.dto.BirthdayDTO;
import EmployeeManagementSystem.entity.Employee;
import EmployeeManagementSystem.entity.Salary;
import EmployeeManagementSystem.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;



    @Override
    public Employee saveEmployee(Employee employee) {

        if (employee.getSalaryDetails() != null) {
            employee.getSalaryDetails().setEmployee(employee);
        }

        if (employee.getAttendanceList() != null) {
            employee.getAttendanceList().forEach(a -> a.setEmployee(employee));
        }

        return employeeRepository.save(employee);
    }

    @Override
    public Employee updateEmployee(Long id, Employee employee) {

        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        existing.setName(employee.getName());
        existing.setEmail(employee.getEmail());
        existing.setPhone(employee.getPhone());
        existing.setJoiningDate(employee.getJoiningDate());
        existing.setDepartment(employee.getDepartment());

        if (employee.getSalaryDetails() != null) {

            Salary newSalary = employee.getSalaryDetails();

            if (existing.getSalaryDetails() != null) {

                Salary existingSalary = existing.getSalaryDetails();

//                existingSalary.setBasicSalary(newSalary.getBasicSalary());
                existingSalary.setBonus(newSalary.getBonus());
//                existingSalary.setDeductions(newSalary.getDeduction());
                existingSalary.setDeductions(newSalary.getDeductions());

            } else {

                newSalary.setEmployee(existing);
                existing.setSalaryDetails(newSalary);
            }
        }

        if (employee.getAttendanceList() != null) {

            existing.getAttendanceList().clear();

            employee.getAttendanceList().forEach(a -> {
                a.setEmployee(existing);
                existing.getAttendanceList().add(a);
            });
        }

        return employeeRepository.save(existing);
    }

    @Override
    public void deleteEmployee(Long id) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
        employeeRepository.delete(emp);
    }

    @Override
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
    }

    @Override
    public List<Employee> getAllEmployeesList() {
        return employeeRepository.findAll();
    }

    @Override
    public Page<Employee> getAllEmployees(int pageNo, int pageSize, String sortBy) {

        Pageable pageable = PageRequest.of(
                pageNo,
                pageSize,
                Sort.by(sortBy == null || sortBy.isBlank() ? "id" : sortBy).ascending()
        );

        return employeeRepository.findAll(pageable);
    }

    @Override
    public Page<Employee> searchEmployee(String keyword, Pageable pageable) {
        return employeeRepository.searchAll(keyword, pageable);
    }


    @Override
    public Employee findByEmail(String email) {
        return (Employee) employeeRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }


    @Override
    public long totalEmployees() {
        return employeeRepository.count();
    }


    @Override
    public List<BirthdayDTO> getUpcomingBirthdays() {

        LocalDate today = LocalDate.now();

        return employeeRepository.findAll()
                .stream()
                .filter(emp -> emp.getDateOfBirth() != null)
                .map(emp -> {

                    LocalDate nextBirthday = emp.getDateOfBirth().withYear(today.getYear());

                    if (nextBirthday.isBefore(today)) {
                        nextBirthday = nextBirthday.plusYears(1);
                    }

                    long days = ChronoUnit.DAYS.between(today, nextBirthday);

                    BirthdayDTO dto = new BirthdayDTO();
                    dto.setName(emp.getName());
                    dto.setDateOfBirth(emp.getDateOfBirth());
                    dto.setRemainingDays(days);
                    dto.setNextDate(nextBirthday);

                    return dto;
                })
                .sorted(Comparator.comparingLong(BirthdayDTO::getRemainingDays))
                .limit(5)
                .toList();
    }

//    @Override
//    public List<Employee> getUpcomingAnniversaries() {
//        return employeeRepository.findAll()
//                .stream()
//                .filter(e -> e.getJoiningDate() != null)
//                .limit(5)
//                .toList();
//    }

    @Override
    public List<AnniversaryDTO> getUpcomingAnniversaries() {

        LocalDate today = LocalDate.now();

        return employeeRepository.findAll()
                .stream()
                .filter(emp -> emp.getJoiningDate() != null)
                .map(emp -> {

                    LocalDate nextAnniversary = emp.getJoiningDate().withYear(today.getYear());

                    if (nextAnniversary.isBefore(today)) {
                        nextAnniversary = nextAnniversary.plusYears(1);
                    }

                    long days = ChronoUnit.DAYS.between(today, nextAnniversary);

                    AnniversaryDTO dto = new AnniversaryDTO();
                    dto.setName(emp.getName());
                    dto.setJoiningDate(emp.getJoiningDate());
                    dto.setRemainingDays(days);
                    dto.setNextDate(nextAnniversary);

                    return dto;
                })
                .sorted(Comparator.comparingLong(AnniversaryDTO::getRemainingDays))
                .limit(5)
                .toList();
    }

}