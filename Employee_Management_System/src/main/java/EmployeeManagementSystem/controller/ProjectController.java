package EmployeeManagementSystem.controller;

import EmployeeManagementSystem.entity.EmployeeProfile;
import EmployeeManagementSystem.entity.Project;
import EmployeeManagementSystem.enums.ProjectStatus;
import EmployeeManagementSystem.service.DepartmentService;
import EmployeeManagementSystem.service.EmployeeProfileService;
import EmployeeManagementSystem.service.EmployeeService;
import EmployeeManagementSystem.service.ProjectService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeProfileService employeeProfileService;

    @Autowired
    private DepartmentService departmentService;

    @GetMapping("/add")
    public String showAddProjectForm(Model model) {
        // Add a new project object
        model.addAttribute("project", new Project());

        // Fetch all departments from database
        model.addAttribute("departments", departmentService.getAllDepartments());

        // Fetch all employee profiles (initially)
        Object allEmployees = employeeProfileService.getAllEmployeeProfiles();
        if (allEmployees != null) {
            model.addAttribute("employees", allEmployees);
        } else {
            model.addAttribute("employees", new ArrayList<>());
        }

        // Add statuses
        model.addAttribute("statuses", ProjectStatus.values());

        // Fetch all projects for the list
        model.addAttribute("projects", projectService.getAllProjects());

        return "admin/project-management/add-project";
    }

    @PostMapping("/save")
    public String saveProject(@Valid @ModelAttribute("project") Project project,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("departments", departmentService.getAllDepartments());
            Object allEmployees = employeeProfileService.getAllEmployeeProfiles();
            if (allEmployees != null) {
                model.addAttribute("employees", allEmployees);
            } else {
                model.addAttribute("employees", new ArrayList<>());
            }
            model.addAttribute("statuses", ProjectStatus.values());
            model.addAttribute("projects", projectService.getAllProjects());
            return "admin/project-management/add-project";
        }

        try {
            // Set employee name if assigned - using EmployeeProfileService
            if (project.getAssignedEmployeeId() != null) {
                try {
                    EmployeeProfile employeeProfile = employeeProfileService.getEmployeeProfileById(project.getAssignedEmployeeId());
                    if (employeeProfile != null) {
                        project.setAssignedEmployeeName(employeeProfile.getFullName());
                    }
                } catch (Exception e) {
                    project.setAssignedEmployeeName(null);
                }
            }

            projectService.saveProject(project);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Project created successfully!");
            return "redirect:/admin/projects/add";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error saving project: " + e.getMessage());
            model.addAttribute("departments", departmentService.getAllDepartments());
            Object allEmployees = employeeProfileService.getAllEmployeeProfiles();
            if (allEmployees != null) {
                model.addAttribute("employees", allEmployees);
            } else {
                model.addAttribute("employees", new ArrayList<>());
            }
            model.addAttribute("statuses", ProjectStatus.values());
            model.addAttribute("projects", projectService.getAllProjects());
            return "admin/project-management/add-project";
        }
    }

    @GetMapping
    public String listProjects(Model model) {
        List<Project> projects = projectService.getAllProjects();
        model.addAttribute("projects", projects);
        return "admin/project-management/project-list";
    }

    // Get employee profiles by department name (for AJAX calls)
    @GetMapping("/employees/by-department/{departmentId}")
    @ResponseBody
    public List<Map<String, Object>> getEmployeesByDepartment(@PathVariable Long departmentId) {
        System.out.println("=== Fetching employee profiles for department ID: " + departmentId);

        // Get department name from ID
        String departmentName = departmentService.getDepartmentById(departmentId).getDepartmentName();
        System.out.println("=== Department Name: " + departmentName);

        List<EmployeeProfile> employeeProfiles = employeeProfileService.getEmployeeProfilesByDepartment(departmentName);
        System.out.println("=== Found " + employeeProfiles.size() + " employee profiles");

        List<Map<String, Object>> result = new ArrayList<>();

        for (EmployeeProfile emp : employeeProfiles) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", emp.getId());
            map.put("fullName", emp.getFullName());
            result.add(map);
            System.out.println("=== Employee: " + emp.getId() + " - " + emp.getFullName());
        }

        return result;
    }
}