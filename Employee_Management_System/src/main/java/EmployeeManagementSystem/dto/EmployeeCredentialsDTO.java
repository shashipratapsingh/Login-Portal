package EmployeeManagementSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmployeeCredentialsDTO {
    private String userId;
    private String password;
    private String email;
    private String fullName;
}