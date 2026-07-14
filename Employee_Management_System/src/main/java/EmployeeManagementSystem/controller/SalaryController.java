package EmployeeManagementSystem.controller;//package EmployeeManagementSystem.controller;
//
//import EmployeeManagementSystem.entity.Salary;
//import EmployeeManagementSystem.service.SalaryService;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//
//@Controller
//@RequestMapping("/salary")
//public class SalaryController {
//
//    private final SalaryService salaryService;
//
//    public SalaryController(SalaryService salaryService) {
//        this.salaryService = salaryService;
//    }
//
//    @GetMapping("/dashboard")
//    public String dashboard(Model model) {
//
//        model.addAttribute("salaryList",
//                salaryService.getAllSalaries());
//
//        model.addAttribute("totalEmployees",
//                salaryService.getTotalEmployees());
//
//        model.addAttribute("paidCount",
//                salaryService.getPaidEmployeesCount());
//
//        model.addAttribute("pendingCount",
//                salaryService.getPendingEmployeesCount());
//
//        model.addAttribute("totalPayroll",
//                salaryService.getTotalPayrollCost());
//
//        model.addAttribute("paidSalary",
//                salaryService.getTotalPaidSalary());
//
//        model.addAttribute("pendingSalary",
//                salaryService.getTotalPendingSalary());
//
//        return "salary-dashboard";
//    }
//
//    @GetMapping("/add")
//    public String addSalaryForm(Model model) {
//
//        model.addAttribute("salary", new Salary());
//
//        return "salary-form";
//    }
//
//    @PostMapping("/save")
//    public String saveSalary(@ModelAttribute Salary salary) {
//
//        salaryService.saveSalary(salary);
//
//        return "redirect:/salary/dashboard";
//    }
//
//    @GetMapping("/edit/{id}")
//    public String editSalary(@PathVariable Long id,
//                             Model model) {
//
//        model.addAttribute("salary",
//                salaryService.getSalaryById(id));
//
//        return "salary-form";
//    }
//
//    @GetMapping("/delete/{id}")
//    public String deleteSalary(@PathVariable Long id) {
//
//        salaryService.deleteSalary(id);
//
//        return "redirect:/salary/dashboard";
//    }
//
//    @GetMapping("/slip/{id}")
//    public String salarySlip(@PathVariable Long id,
//                             Model model) {
//
//        model.addAttribute("salarySlip",
//                salaryService.getSalarySlipById(id));
//
//        return "salary-slip";
//    }
//}









//package EmployeeManagementSystem.controller;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import java.util.*;
//
//@Controller
//@RequestMapping("/admin/salary")
//public class SalaryController {
//
//    // ========== MAIN DASHBOARD WITH HARDCODED DATA ==========
//    @GetMapping("/dashboard")
//    public String salaryDashboard(Model model) {
//
//        // 1. Dashboard Statistics (matching your image)
//        DashboardStats stats = new DashboardStats(
//                150,                    // Total Employees
//                1250000.0,              // Total Payroll Cost ₹12,50,000
//                1020000.0,              // Paid Salary ₹10,20,000
//                230000.0,               // Pending Salary ₹2,30,000
//                5.2                     // Active Employee Growth 5.2%
//        );
//        model.addAttribute("stats", stats);
//
//        // 2. Payroll Summary by Department (matching your image)
//        List<PayrollSummary> payrollSummary = Arrays.asList(
//                new PayrollSummary("Information Technology", 45, 450000.0),
//                new PayrollSummary("Human Resources", 20, 160000.0),
//                new PayrollSummary("Finance", 15, 200000.0),
//                new PayrollSummary("Marketing", 25, 180000.0),
//                new PayrollSummary("Operations", 30, 260000.0)
//        );
//        model.addAttribute("payrollSummary", payrollSummary);
//
//        // 3. Employee Salary List (matching your image)
//        List<SalaryRecord> employees = Arrays.asList(
//                new SalaryRecord("EMP001", "Rahul Sharma", "IT", "Software Engineer", 50000.0, 3000.0, 47000.0, "Paid"),
//                new SalaryRecord("EMP002", "Aman Verma", "HR", "HR Executive", 35000.0, 1500.0, 33500.0, "Pending"),
//                new SalaryRecord("EMP003", "Neha Singh", "Finance", "Accountant", 40000.0, 2000.0, 38000.0, "Paid"),
//                new SalaryRecord("EMP004", "Vikram Patel", "Marketing", "Marketing Executive", 38000.0, 1800.0, 36200.0, "Processing"),
//                new SalaryRecord("EMP005", "Pooja Desai", "Operations", "Operations Manager", 60000.0, 4000.0, 56000.0, "Paid")
//        );
//        model.addAttribute("employees", employees);
//
//        // 4. Payroll Overview with Percentages (matching your image)
//        Map<String, Object> overview = new LinkedHashMap<>();
//        overview.put("totalPayroll", 1250000.0);
//
//        Map<String, Double> percentages = new LinkedHashMap<>();
//        percentages.put("HR", 12.8);
//        percentages.put("Finance", 16.0);
//        percentages.put("Marketing", 14.4);
//        percentages.put("Operations", 20.8);
//        overview.put("departmentPercentages", percentages);
//        model.addAttribute("overview", overview);
//
//        // 5. Recent Activities (matching your image)
//        List<ActivityLog> activities = Arrays.asList(
//                new ActivityLog("Rahul Sharma salary paid", "Today, 10:30 AM"),
//                new ActivityLog("Payroll generated for July 2024", "Today, 09:15 AM"),
//                new ActivityLog("Neha Singh downloaded payslip", "Yesterday, 04:45 PM"),
//                new ActivityLog("Aman Verma salary pending", "Yesterday, 11:20 AM")
//        );
//        model.addAttribute("activities", activities);
//
//        // 6. Current Month/Year
//        model.addAttribute("currentMonth", "July");
//        model.addAttribute("currentYear", 2024);
//
//        // 7. Months and Years for dropdown
//        model.addAttribute("months", Arrays.asList("January", "February", "March", "April", "May", "June",
//                "July", "August", "September", "October", "November", "December"));
//        model.addAttribute("years", Arrays.asList(2022, 2023, 2024, 2025));
//
//        model.addAttribute("pageTitle", "Salary Management Dashboard");
//        return "admin/salary/salary-dashboard";
//    }
//
//    // ========== INNER CLASSES (DTOs) ==========
//    public static class DashboardStats {
//        private int totalEmployees;
//        private double totalPayrollCost;
//        private double paidSalary;
//        private double pendingSalary;
//        private double activeEmployeeGrowth;
//
//        public DashboardStats(int totalEmployees, double totalPayrollCost, double paidSalary,
//                              double pendingSalary, double activeEmployeeGrowth) {
//            this.totalEmployees = totalEmployees;
//            this.totalPayrollCost = totalPayrollCost;
//            this.paidSalary = paidSalary;
//            this.pendingSalary = pendingSalary;
//            this.activeEmployeeGrowth = activeEmployeeGrowth;
//        }
//
//        public int getTotalEmployees() {
//            return totalEmployees;
//        }
//
//        public double getTotalPayrollCost() {
//            return totalPayrollCost;
//        }
//
//        public double getPaidSalary() {
//            return paidSalary;
//        }
//
//        public double getPendingSalary() {
//            return pendingSalary;
//        }
//
//        public double getActiveEmployeeGrowth() {
//            return activeEmployeeGrowth;
//        }
//    }
//
//    public static class PayrollSummary {
//        private String department;
//        private int employees;
//        private double payrollCost;
//
//        public PayrollSummary(String department, int employees, double payrollCost) {
//            this.department = department;
//            this.employees = employees;
//            this.payrollCost = payrollCost;
//        }
//
//        public String getDepartment() {
//            return department;
//        }
//
//        public int getEmployees() {
//            return employees;
//        }
//
//        public double getPayrollCost() {
//            return payrollCost;
//        }
//    }
//
//    public static class SalaryRecord {
//        private String employeeId;
//        private String employeeName;
//        private String department;
//        private String designation;
//        private double grossSalary;
//        private double deductions;
//        private double netSalary;
//        private String paymentStatus;
//
//        public SalaryRecord(String employeeId, String employeeName, String department,
//                            String designation, double grossSalary, double deductions,
//                            double netSalary, String paymentStatus) {
//            this.employeeId = employeeId;
//            this.employeeName = employeeName;
//            this.department = department;
//            this.designation = designation;
//            this.grossSalary = grossSalary;
//            this.deductions = deductions;
//            this.netSalary = netSalary;
//            this.paymentStatus = paymentStatus;
//        }
//
//        public String getEmployeeId() {
//            return employeeId;
//        }
//
//        public String getEmployeeName() {
//            return employeeName;
//        }
//
//        public String getDepartment() {
//            return department;
//        }
//
//        public String getDesignation() {
//            return designation;
//        }
//
//        public double getGrossSalary() {
//            return grossSalary;
//        }
//
//        public double getDeductions() {
//            return deductions;
//        }
//
//        public double getNetSalary() {
//            return netSalary;
//        }
//
//        public String getPaymentStatus() {
//            return paymentStatus;
//        }
//    }
//
//    public static class ActivityLog {
//        private String description;
//        private String timestamp;
//
//        public ActivityLog(String description, String timestamp) {
//            this.description = description;
//            this.timestamp = timestamp;
//        }
//
//        public String getDescription() {
//            return description;
//        }
//
//        public String getTimestamp() {
//            return timestamp;
//        }
//    }
//}


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/salary")
public class SalaryController {

    @GetMapping("/salary-dashboard")  // Match the URL you're trying to access
    public String salaryDashboard(Model model) {
        // ... your code
        return "admin/salary/salary-dashboard";
    }
}