package com.employeemanagement.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeDTO {

    private Long id;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50)
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[\\d\\s\\-\\+\\(\\)]{10,}$")
    private String phone;

    @NotNull(message = "Date of birth is required")
    @PastOrPresent(message = "Date of birth should not be in future")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Position is required")
    private String position;

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    private String departmentName;

    @NotNull(message = "Salary is required")
    @DecimalMin(value = "0.0", inclusive = false)
    private Double salary;

    @NotNull(message = "Hire date is required")
    @PastOrPresent(message = "Hire date should not be in future")
    private LocalDate hireDate;

    @Builder.Default
    private Boolean isActive = true;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
