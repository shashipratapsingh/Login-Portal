//package EmployeeManagementSystem.service.admin_salary.impl;
//
//import EmployeeManagementSystem.entity.Employee;
//import EmployeeManagementSystem.entity.Salary;
//import EmployeeManagementSystem.entity.admin_salary.SalaryStructure;
//import EmployeeManagementSystem.repository.EmployeeRepository;
//import EmployeeManagementSystem.repository.admin_salaryRepo.AdminSalaryRepo;
//import EmployeeManagementSystem.repository.admin_salaryRepo.AdminSalaryStructureRepo;
//import EmployeeManagementSystem.service.admin_salary.MonthlySalaryService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//public class MonthlySalaryServiceImpl implements MonthlySalaryService {
//
//    private final AdminSalaryRepo salaryRepository;
//    private final AdminSalaryStructureRepo salaryStructureRepository;
//    private final EmployeeRepository employeeRepository;
//
//    @Override
//    @Transactional
//    public Salary generateMonthlySalary(Long employeeId, String month, Integer year) {
//        // Check if salary already exists for this month
//        Optional<Salary> existingSalary = salaryRepository.findByEmployeeIdAndMonthAndYear(employeeId, month, year);
//        if (existingSalary.isPresent()) {
//            return existingSalary.get();
//        }
//
//        // Get employee
//        Employee employee = employeeRepository.findById(employeeId)
//                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));
//
//        // Get salary structure
//        SalaryStructure salaryStructure = salaryStructureRepository.findByEmployeeId(employeeId)
//                .orElseThrow(() -> new RuntimeException("Salary structure not found for employee: " + employeeId));
//
//        // Create Salary record
//        Salary salary = new Salary();
//        salary.setEmployee(employee);
//
//        // Set earnings
//        salary.setBaseSalary(salaryStructure.getBasicSalary().doubleValue());
//        salary.setHra(salaryStructure.getHra().doubleValue());
//
//        // DA and TA are not in SalaryStructure, using defaults or 0
//        salary.setDa(0.0);
//        salary.setTa(0.0);
//
//        salary.setOtherAllowances(salaryStructure.getSpecialAllowance()
//                .add(salaryStructure.getOtherAllowance())
//                .add(salaryStructure.getConveyance())
//                .add(salaryStructure.getMedicalAllowance())
//                .doubleValue());
//
//        // Set deductions
//        salary.setPfDeduction(salaryStructure.getPf().doubleValue());
//        salary.setProfessionalTax(salaryStructure.getProfessionalTax().doubleValue());
//        salary.setIncomeTax(salaryStructure.getTds().doubleValue());
//
//        // Bonus not in structure, default 0
//        salary.setBonus(0.0);
//
//        // Set month and year
//        salary.setMonth(month);
//        salary.setYear(year);
//        salary.setPaymentStatus("Pending");
//
//        // Calculate salary
//        salary.calculateSalary();
//
//        return salaryRepository.save(salary);
//    }
//
//    @Override
//    @Transactional
//    public List<Salary> generateMonthlySalaryForAllEmployees(String month, Integer year) {
//        List<Employee> employees = employeeRepository.findAll();
//        List<Salary> generatedSalaries = new ArrayList<>();
//
//        for (Employee employee : employees) {
//            try {
//                Salary salary = generateMonthlySalary(employee.getId(), month, year);
//                generatedSalaries.add(salary);
//            } catch (Exception e) {
//                System.err.println("Error generating salary for employee " + employee.getId() + ": " + e.getMessage());
//            }
//        }
//
//        return generatedSalaries;
//    }
//
//    @Override
//    public Salary getSalaryByEmployeeAndMonth(Long employeeId, String month, Integer year) {
//        return salaryRepository.findByEmployeeIdAndMonthAndYear(employeeId, month, year)
//                .orElseThrow(() -> new RuntimeException("Salary not found for employee: " + employeeId +
//                        ", month: " + month + ", year: " + year));
//    }
//
//    @Override
//    public List<Salary> getSalariesByMonth(String month, Integer year) {
//        // We need to filter by month and year since the repository doesn't have this method
//        // We'll fetch all and filter, or you can add a custom query in the repository
//        List<Salary> allSalaries = salaryRepository.findAll();
//        return allSalaries.stream()
//                .filter(s -> month.equals(s.getMonth()) && year.equals(s.getYear()))
//                .toList();
//    }
//
//    @Override
//    public List<Salary> getSalariesByEmployee(Long employeeId) {
//        return salaryRepository.findByEmployeeId(employeeId);
//    }
//}