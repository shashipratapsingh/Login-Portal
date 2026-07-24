package EmployeeManagementSystem.kafkaConfig;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeEvent {

    private Long id;
    private String fullName;
    private String email;

}