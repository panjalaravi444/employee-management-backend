package com.employeemanagement.repository;

import com.employeemanagement.entity.Employee;
import com.employeemanagement.entity.Department;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("Employee Repository Integration Tests")
class EmployeeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private Department department;
    private Employee employee;

    @BeforeEach
    void setUp() {
        department = Department.builder()
                .name("Information Technology")
                .description("IT Department")
                .build();
        departmentRepository.save(department);

        employee = Employee.builder()
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
        employeeRepository.save(employee);
    }

    @Test
    @DisplayName("Should find employee by email")
    void testFindByEmail() {
        Optional<Employee> result = employeeRepository.findByEmail("john@example.com");

        assertTrue(result.isPresent());
        assertEquals("John", result.get().getFirstName());
        assertEquals("john@example.com", result.get().getEmail());
    }

    @Test
    @DisplayName("Should return empty when email not found")
    void testFindByEmailNotFound() {
        Optional<Employee> result = employeeRepository.findByEmail("notfound@example.com");

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should find employees by department")
    void testFindByDepartmentId() {
        List<Employee> result = employeeRepository.findByDepartmentId(department.getId());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
    }

    @Test
    @DisplayName("Should find active employees")
    void testFindByIsActive() {
        List<Employee> result = employeeRepository.findByIsActive(true);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().allMatch(e -> e.getIsActive()));
    }

    @Test
    @DisplayName("Should find employees by first name")
    void testFindByFirstNameContaining() {
        List<Employee> result = employeeRepository.findByFirstNameContainingIgnoreCase("john");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should find employees by last name")
    void testFindByLastNameContaining() {
        List<Employee> result = employeeRepository.findByLastNameContainingIgnoreCase("doe");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should find all active employees")
    void testFindAllActive() {
        List<Employee> result = employeeRepository.findAllActive();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().allMatch(e -> e.getIsActive()));
    }

    @Test
    @DisplayName("Should search employees by name")
    void testSearchEmployeesByName() {
        List<Employee> result = employeeRepository.searchEmployees("John");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(e -> e.getFirstName().contains("John")));
    }

    @Test
    @DisplayName("Should search employees by email")
    void testSearchEmployeesByEmail() {
        List<Employee> result = employeeRepository.searchEmployees("john@example");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(e -> e.getEmail().contains("john@example")));
    }

    @Test
    @DisplayName("Should find active employees by department")
    void testFindActiveEmployeesByDepartment() {
        List<Employee> result = employeeRepository.findActiveEmployeesByDepartment(department.getId());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().allMatch(e -> e.getIsActive()));
    }

    @Test
    @DisplayName("Should save and persist employee")
    void testSaveEmployee() {
        Employee newEmployee = Employee.builder()
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

        Employee saved = employeeRepository.save(newEmployee);

        assertNotNull(saved.getId());
        assertEquals("Jane", saved.getFirstName());
        assertEquals("jane@example.com", saved.getEmail());
    }

    @Test
    @DisplayName("Should delete employee")
    void testDeleteEmployee() {
        employeeRepository.delete(employee);

        Optional<Employee> result = employeeRepository.findById(employee.getId());
        assertFalse(result.isPresent());
    }
}
