package EmployeeManagementSystem.controller;

import EmployeeManagementSystem.dto.EmployeeCredentialsDTO;
import EmployeeManagementSystem.entity.EmployeeProfile;
import EmployeeManagementSystem.service.DepartmentService;
import EmployeeManagementSystem.service.EmployeeProfileServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class EmployeeProfileController {

    private final EmployeeProfileServiceImpl employeeProfileService;
    private final DepartmentService departmentService;

    // Redirect from employee-directory to list
    @GetMapping("/employee-directory")
    public String redirectToEmployeeList() {
        return "redirect:/admin/employees/list";
    }

//    @PostMapping("/employees/test")
//    public String testAddEmployee(@ModelAttribute EmployeeProfile employee,
//                                  RedirectAttributes redirectAttributes) {
//        System.out.println("===== TEST METHOD CALLED =====");
//        System.out.println("Name: " + employee.getFullName());
//        System.out.println("Email: " + employee.getEmail());
//        System.out.println("Department: " + employee.getDepartment());
//        System.out.println("Designation: " + employee.getDesignation());
//
//        redirectAttributes.addFlashAttribute("successMessage", "Test successful! Name: " + employee.getFullName());
//        return "redirect:/admin/employees/list";
//    }

    @GetMapping("/employees/add")
    public String showAddEmployeeForm(@RequestParam(required = false) String department,
                                      Model model) {
        EmployeeProfile employee = new EmployeeProfile();

        if (department != null && !department.isEmpty()) {
            employee.setDepartment(department);
        }

        model.addAttribute("employee", employee);
        model.addAttribute("departments", departmentService.getActiveDepartments());
        model.addAttribute("genders", Arrays.asList("Male", "Female", "Other"));
        model.addAttribute("bloodGroups", Arrays.asList("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"));
        model.addAttribute("maritalStatuses", Arrays.asList("Single", "Married", "Divorced", "Widowed"));
        model.addAttribute("qualifications", Arrays.asList("10th", "12th", "Diploma", "Bachelor's", "Master's", "PhD", "Other"));
        model.addAttribute("pageTitle", "Add Employee");

        return "admin/employee-management/add-employee";
    }

    @PostMapping("/employees/add")
    public String addEmployee(@Valid @ModelAttribute("employee") EmployeeProfile employee,
                              BindingResult bindingResult,
                              @RequestParam(value = "photoFile", required = false) MultipartFile photoFile,
                              RedirectAttributes redirectAttributes,
                              Model model) {

        System.out.println("===== ADD EMPLOYEE POST METHOD CALLED =====");
        System.out.println("Full Name: " + employee.getFullName());
        System.out.println("Email: " + employee.getEmail());
        System.out.println("Department: " + employee.getDepartment());
        System.out.println("Designation: " + employee.getDesignation());

        if (employeeProfileService.existsByEmail(employee.getEmail())) {
            bindingResult.rejectValue("email", "error.employee", "Email already registered");
            System.out.println("Email already exists: " + employee.getEmail());
        }

        if (bindingResult.hasErrors()) {
            System.out.println("Binding errors: " + bindingResult.getAllErrors());
            model.addAttribute("departments", departmentService.getActiveDepartments());
            model.addAttribute("genders", Arrays.asList("Male", "Female", "Other"));
            model.addAttribute("bloodGroups", Arrays.asList("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"));
            model.addAttribute("maritalStatuses", Arrays.asList("Single", "Married", "Divorced", "Widowed"));
            model.addAttribute("qualifications", Arrays.asList("10th", "12th", "Diploma", "Bachelor's", "Master's", "PhD", "Other"));
            model.addAttribute("pageTitle", "Add Employee");
            return "admin/employee-management/add-employee";
        }

        try {
            EmployeeCredentialsDTO credentials = employeeProfileService.saveOrUpdateProfile(employee, null);
            System.out.println("Employee saved with ID: " + credentials.getUserId());

            if (photoFile != null && !photoFile.isEmpty()) {
                employeeProfileService.uploadPhoto(photoFile, employee.getUserId());
                System.out.println("Photo uploaded for employee: " + employee.getUserId());
            }

            redirectAttributes.addFlashAttribute("showCredentialsPopup", true);
            redirectAttributes.addFlashAttribute("credentials", credentials);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Employee added successfully! Credentials have been sent to the employee's email.");

            System.out.println("Redirecting to employee list with credentials popup");
            return "redirect:/admin/employees/list";
        } catch (Exception e) {
            System.err.println("Error adding employee: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("errorMessage", "Error adding employee: " + e.getMessage());
            model.addAttribute("departments", departmentService.getActiveDepartments());
            model.addAttribute("genders", Arrays.asList("Male", "Female", "Other"));
            model.addAttribute("bloodGroups", Arrays.asList("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"));
            model.addAttribute("maritalStatuses", Arrays.asList("Single", "Married", "Divorced", "Widowed"));
            model.addAttribute("qualifications", Arrays.asList("10th", "12th", "Diploma", "Bachelor's", "Master's", "PhD", "Other"));
            model.addAttribute("pageTitle", "Add Employee");
            return "admin/employee-management/add-employee";
        }
    }

    @GetMapping("/employees/list")
    public String listEmployees(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                @RequestParam(defaultValue = "id") String sortBy,
                                @RequestParam(defaultValue = "asc") String direction,
                                Model model) {

        Sort sort = direction.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<EmployeeProfile> employeePage = employeeProfileService.getAllProfiles(pageable);

        model.addAttribute("employees", employeePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", employeePage.getTotalPages());
        model.addAttribute("totalItems", employeePage.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        model.addAttribute("pageTitle", "Employee List");

        return "admin/employee-management/employee-list";
    }

    @GetMapping("/employees/edit/{id}")
    public String showEditEmployeeForm(@PathVariable Long id, Model model) {
        EmployeeProfile employee = employeeProfileService.getProfileById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        model.addAttribute("employee", employee);
        model.addAttribute("genders", Arrays.asList("Male", "Female", "Other"));
        model.addAttribute("bloodGroups", Arrays.asList("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"));
        model.addAttribute("maritalStatuses", Arrays.asList("Single", "Married", "Divorced", "Widowed"));
        model.addAttribute("qualifications", Arrays.asList("10th", "12th", "Diploma", "Bachelor's", "Master's", "PhD", "Other"));
        model.addAttribute("pageTitle", "Edit Employee");

        return "admin/employee-management/add-employee";
    }

    @PostMapping("/employees/update/{id}")
    public String updateEmployee(@PathVariable Long id,
                                 @Valid @ModelAttribute("employee") EmployeeProfile employee,
                                 BindingResult bindingResult,
                                 @RequestParam("photoFile") MultipartFile photoFile,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {

        EmployeeProfile existingEmployee = employeeProfileService.getProfileById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (!existingEmployee.getEmail().equals(employee.getEmail()) &&
                employeeProfileService.existsByEmail(employee.getEmail())) {
            bindingResult.rejectValue("email", "error.employee", "Email already registered");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("genders", Arrays.asList("Male", "Female", "Other"));
            model.addAttribute("bloodGroups", Arrays.asList("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"));
            model.addAttribute("maritalStatuses", Arrays.asList("Single", "Married", "Divorced", "Widowed"));
            model.addAttribute("qualifications", Arrays.asList("10th", "12th", "Diploma", "Bachelor's", "Master's", "PhD", "Other"));
            model.addAttribute("pageTitle", "Edit Employee");
            return "admin/employee-management/add-employee";
        }

        try {
            employeeProfileService.updateEmployee(id, employee, photoFile);
            redirectAttributes.addFlashAttribute("successMessage", "Employee updated successfully!");
            return "redirect:/admin/employees/list";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error updating employee: " + e.getMessage());
            model.addAttribute("genders", Arrays.asList("Male", "Female", "Other"));
            model.addAttribute("bloodGroups", Arrays.asList("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"));
            model.addAttribute("maritalStatuses", Arrays.asList("Single", "Married", "Divorced", "Widowed"));
            model.addAttribute("qualifications", Arrays.asList("10th", "12th", "Diploma", "Bachelor's", "Master's", "PhD", "Other"));
            model.addAttribute("pageTitle", "Edit Employee");
            return "admin/employee-management/add-employee";
        }
    }

    @GetMapping("/employees/delete/{id}")
    public String deleteEmployee(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            employeeProfileService.deleteEmployee(id);
            redirectAttributes.addFlashAttribute("successMessage", "Employee deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting employee: " + e.getMessage());
        }
        return "redirect:/admin/employees/list";
    }

    @GetMapping("/employees/view/{id}")
    public String viewEmployee(@PathVariable Long id, Model model) {
        EmployeeProfile employee = employeeProfileService.getProfileById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        model.addAttribute("employee", employee);
        model.addAttribute("pageTitle", "Employee Details");

        return "admin/employee-management/employee-view";
    }

    @GetMapping("/employees/search")
    public String searchEmployees(@RequestParam String keyword, Model model) {
        List<EmployeeProfile> employees = employeeProfileService.searchEmployees(keyword);
        model.addAttribute("employees", employees);
        model.addAttribute("searchKeyword", keyword);
        model.addAttribute("pageTitle", "Search Results");
        return "admin/employee-management/employee-list";
    }

    @GetMapping("/employees/toggle-status/{id}")
    public String toggleEmployeeStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            EmployeeProfile employee = employeeProfileService.getProfileById(id)
                    .orElseThrow(() -> new RuntimeException("Employee not found"));

            if ("ACTIVE".equals(employee.getStatus())) {
                employeeProfileService.softDeleteEmployee(id);
                redirectAttributes.addFlashAttribute("successMessage", "Employee deactivated successfully!");
            } else {
                employee.setStatus("ACTIVE");
                employeeProfileService.updateEmployee(id, employee, null);
                redirectAttributes.addFlashAttribute("successMessage", "Employee activated successfully!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error toggling employee status: " + e.getMessage());
        }
        return "redirect:/admin/employees/list";
    }
}