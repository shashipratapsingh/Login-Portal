package EmployeeManagementSystem.controller;//package EmployeeManagementSystem.controller;

import EmployeeManagementSystem.entity.Employee;
import EmployeeManagementSystem.entity.Salary;
import EmployeeManagementSystem.repository.EmployeeRepository;
import EmployeeManagementSystem.service.SalaryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("employee/salary")
public class SalaryController {

    private final SalaryService salaryService;

    private final EmployeeRepository employeeRepository;

    public SalaryController(SalaryService salaryService, EmployeeRepository employeeRepository) {
        this.salaryService = salaryService;
        this.employeeRepository = employeeRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        model.addAttribute("salaryList",
                salaryService.getAllSalaries());

        model.addAttribute("totalEmployees",
                salaryService.getTotalEmployees());

        model.addAttribute("paidCount",
                salaryService.getPaidEmployeesCount());

        model.addAttribute("pendingCount",
                salaryService.getPendingEmployeesCount());

        model.addAttribute("totalPayroll",
                salaryService.getTotalPayrollCost());

        model.addAttribute("paidSalary",
                salaryService.getTotalPaidSalary());

        model.addAttribute("pendingSalary",
                salaryService.getTotalPendingSalary());

        return "salary-dashboard";
    }

    @GetMapping("/add")
    public String addSalaryForm(Model model) {

        model.addAttribute("salary", new Salary());

        return "salary-form";
    }

    @PostMapping("/save")
    public String saveSalary(@ModelAttribute Salary salary) {

        salaryService.saveSalary(salary);

        return "redirect:/salary/dashboard";
    }

    @GetMapping("/edit/{id}")
    public String editSalary(@PathVariable Long id,
                             Model model) {

        model.addAttribute("salary",
                salaryService.getSalaryById(id));

        return "salary-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteSalary(@PathVariable Long id) {

        salaryService.deleteSalary(id);

        return "redirect:/salary/dashboard";
    }

//    @GetMapping("/slip/{id}")
//    public String salarySlip(@PathVariable Long id,
//                             Model model) {
//
//        model.addAttribute("salarySlip",
//                salaryService.getSalarySlipById(id));
//
//        return "salary-slip";
//    }

    @GetMapping("/slip/{id}")
    public String salarySlip(@PathVariable Long id,
                             Model model) {

        Salary salary = salaryService.getSalaryById(id);

        System.out.println("Salary Found : " + salary);

        model.addAttribute("salary", salary);

        return "salary-slip";
    }


    @GetMapping("/salary-list")
    public String salaryList(Model model) {

        List<Salary> salaries = salaryService.getAllSalaries();

        model.addAttribute("salaries", salaries);

        return "salary-list";
    }



}




//
//package EmployeeManagementSystem.controller;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Controller
//@RequestMapping("/employee/salary")
//public class SalaryController {
//
//    @GetMapping("/salary-slip")
//    public String salarySlip(Model model) {
//
//        // Create a Map for slip data
//        Map<String, Object> slip = new HashMap<>();
//        slip.put("employeeName", "Rahul Sharma");
//        slip.put("designation", "Software Engineer");
//        slip.put("month", "July");
//        slip.put("year", "2026");
//        slip.put("basicDA", 50000);
//        slip.put("hra", 10000);
//        slip.put("conveyance", 1000);
//        slip.put("totalEarnings", 61000);
//        slip.put("pf", 2000);
//        slip.put("esi", 3000);
//        slip.put("loan", 0);
//        slip.put("professionTax", 0);
//        slip.put("tds", 0);
//        slip.put("totalDeductions", 5000);
//        slip.put("netSalary", 56000);
//        slip.put("amountInWords", "Fifty Six Thousand Only");
//        slip.put("chequeNo", "123456");
//        slip.put("bankName", "State Bank of India");
//        slip.put("paymentDate", "15/07/2026");
//
//        model.addAttribute("slip", slip);
//        model.addAttribute("pageTitle", "Salary Slip");
//
//        return "salary-slip";
//    }
//}