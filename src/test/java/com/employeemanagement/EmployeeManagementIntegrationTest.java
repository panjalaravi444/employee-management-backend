package com.employeemanagement;

import com.employeemanagement.dto.DepartmentDTO;
import com.employeemanagement.dto.EmployeeDTO;
import com.employeemanagement.entity.Department;
import com.employeemanagement.entity.Employee;
import com.employeemanagement.repository.DepartmentRepository;
import com.employeemanagement.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Employee Management Integration Tests")
class EmployeeManagementIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private Department department;

    @BeforeEach
    void setUp() {
        // Clean up before each test
        employeeRepository.deleteAll();
        departmentRepository.deleteAll();

        // Create test department
        department = Department.builder()
                .name("Information Technology")
                .description("IT Department")
                .build();
        departmentRepository.save(department);
    }

    @Test
    @DisplayName("Should create department through API")
    void testCreateDepartmentIntegration() {
        DepartmentDTO newDept = DepartmentDTO.builder()
                .name("Human Resources")
                .description("HR Department")
                .build();

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/departments",
                newDept,
                String.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().contains("Human Resources"));
    }

    @Test
    @DisplayName("Should get all departments through API")
    void testGetAllDepartmentsIntegration() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/departments",
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Information Technology"));
    }

    @Test
    @DisplayName("Should create employee through API")
    void testCreateEmployeeIntegration() {
        EmployeeDTO newEmp = EmployeeDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .phone("+1-555-0100")
                .dateOfBirth(LocalDate.of(1990, 1, 15))
                .position("Senior Developer")
                .departmentId(department.getId())
                .salary(120000.0)
                .hireDate(LocalDate.of(2020, 1, 20))
                .build();

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/employees",
                newEmp,
                String.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().contains("john@example.com"));
    }

    @Test
    @DisplayName("Should get all employees through API")
    void testGetAllEmployeesIntegration() {
        // Create an employee first
        Employee emp = Employee.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .phone("+1-555-0100")
                .dateOfBirth(LocalDate.of(1990, 1, 15))
                .position("Senior Developer")
                .department(department)
                .salary(120000.0)
                .hireDate(LocalDate.of(2020, 1, 20))
                .isActive(true)
                .build();
        employeeRepository.save(emp);

        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/employees",
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("john@example.com"));
    }

    @Test
    @DisplayName("Should get employee by ID through API")
    void testGetEmployeeByIdIntegration() {
        Employee emp = Employee.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane@example.com")
                .phone("+1-555-0101")
                .dateOfBirth(LocalDate.of(1995, 5, 20))
                .position("Junior Developer")
                .department(department)
                .salary(80000.0)
                .hireDate(LocalDate.of(2022, 3, 15))
                .isActive(true)
                .build();
        Employee savedEmp = employeeRepository.save(emp);

        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/employees/" + savedEmp.getId(),
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("jane@example.com"));
    }

    @Test
    @DisplayName("Should search employees through API")
    void testSearchEmployeesIntegration() {
        Employee emp = Employee.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .phone("+1-555-0100")
                .dateOfBirth(LocalDate.of(1990, 1, 15))
                .position("Senior Developer")
                .department(department)
                .salary(120000.0)
                .hireDate(LocalDate.of(2020, 1, 20))
                .isActive(true)
                .build();
        employeeRepository.save(emp);

        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/employees/search?term=john",
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("john@example.com"));
    }

    @Test
    @DisplayName("Should update employee through API")
    void testUpdateEmployeeIntegration() {
        Employee emp = Employee.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .phone("+1-555-0100")
                .dateOfBirth(LocalDate.of(1990, 1, 15))
                .position("Senior Developer")
                .department(department)
                .salary(120000.0)
                .hireDate(LocalDate.of(2020, 1, 20))
                .isActive(true)
                .build();
        Employee savedEmp = employeeRepository.save(emp);

        EmployeeDTO updateEmp = EmployeeDTO.builder()
                .firstName("John")
                .lastName("Updated")
                .email("john@example.com")
                .phone("+1-555-0100")
                .dateOfBirth(LocalDate.of(1990, 1, 15))
                .position("Lead Developer")
                .departmentId(department.getId())
                .salary(150000.0)
                .hireDate(LocalDate.of(2020, 1, 20))
                .build();

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/employees/" + savedEmp.getId(),
                org.springframework.http.HttpMethod.PUT,
                new org.springframework.http.HttpEntity<>(updateEmp),
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Lead Developer"));
    }

    @Test
    @DisplayName("Should delete employee through API")
    void testDeleteEmployeeIntegration() {
        Employee emp = Employee.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .phone("+1-555-0100")
                .dateOfBirth(LocalDate.of(1990, 1, 15))
                .position("Senior Developer")
                .department(department)
                .salary(120000.0)
                .hireDate(LocalDate.of(2020, 1, 20))
                .isActive(true)
                .build();
        Employee savedEmp = employeeRepository.save(emp);

        restTemplate.delete("/api/v1/employees/" + savedEmp.getId());

        assertFalse(employeeRepository.findById(savedEmp.getId()).isPresent());
    }

    @Test
    @DisplayName("Should return 404 for non-existent employee")
    void testGetNonExistentEmployeeIntegration() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/employees/9999",
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
