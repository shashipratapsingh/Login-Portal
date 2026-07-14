//package EmployeeManagementSystem.entity;
//
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.Setter;
//
//import java.time.LocalDate;
//
//@Getter
//@Setter
//@Entity
//@Table(name = "salary")
//public class Salary {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name = "employee_id")
//    private Employee employee;
//
//    @Column(name = "employee_code")
//    private String employeeId;
//
//    private String employeeName;
//    private String department;
//    private String designation;
//
//    private Double basicSalary;
//    private Double hra;
//    private Double allowance;
//    private Double bonus;
//    private Double deductions;
//
//    private Double grossSalary;
//    private Double netSalary;
//
//    private String paymentStatus;
//
//    private String month;
//    private Integer year;
//
//    private LocalDate paymentDate;
//}





package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "salaries")
@Data
public class Salary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", unique = true)
    private Employee employee;

    @Column(nullable = false)
    private Double baseSalary;

    private Double hra;
    private Double da;
    private Double ta;
    private Double otherAllowances;
    private Double pfDeduction;
    private Double professionalTax;
    private Double incomeTax;
    private Double bonus;

    @Column(nullable = false)
    private Double grossSalary;

    @Column(nullable = false)
    private Double deductions;

    @Column(nullable = false)
    private Double netSalary;

    private String paymentStatus;
    private String month;
    private Integer year;

    private LocalDate generatedDate;
    private LocalDate updatedDate;

    @PrePersist
    protected void onCreate() {
        generatedDate = LocalDate.now();
        updatedDate = LocalDate.now();
        calculateSalary();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDate.now();
        calculateSalary();
    }

    // ADD THIS METHOD
    public void calculateSalary() {
        if (baseSalary == null) {
            baseSalary = 0.0;
        }

        double hraAmount = hra != null ? hra : baseSalary * 0.30;
        double daAmount = da != null ? da : baseSalary * 0.10;
        double taAmount = ta != null ? ta : baseSalary * 0.08;
        double otherAmount = otherAllowances != null ? otherAllowances : 0.0;
        double bonusAmount = bonus != null ? bonus : 0.0;

        this.grossSalary = baseSalary + hraAmount + daAmount + taAmount + otherAmount + bonusAmount;

        double pfAmount = pfDeduction != null ? pfDeduction : grossSalary * 0.12;
        double ptAmount = professionalTax != null ? professionalTax : 200.0;
        double itAmount = incomeTax != null ? incomeTax : calculateIncomeTax(grossSalary);

        this.deductions = pfAmount + ptAmount + itAmount;
        this.netSalary = grossSalary - deductions;
    }

    private double calculateIncomeTax(double gross) {
        if (gross <= 250000) return 0.0;
        if (gross <= 500000) return (gross - 250000) * 0.05;
        if (gross <= 1000000) return 12500 + (gross - 500000) * 0.20;
        return 112500 + (gross - 1000000) * 0.30;
    }
}