package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "employees")
@Data
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate dateOfBirth;
    @Transient
    private long daysLeft;
    @Transient
    private String remainingText;

    @Column(unique = true)
    private String email;

    private String phone;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate joiningDate;

    private String workMode;

    @Lob
    private byte[] image;

    private String imageName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id")
    private Department department;


    @OneToMany(mappedBy = "employee",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Attendance> attendanceList = new ArrayList<>();


    @OneToMany(mappedBy = "employee",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Salary> salaries = new ArrayList<>();


    @OneToOne(mappedBy = "employee",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Salary salaryDetails;


//    public void setSalaryDetails(Salary salaryDetails) {
//        this.salaryDetails = salaryDetails;
//        if (salaryDetails != null) {
//            salaryDetails.setEmployee(this);
//        }
//    }
}