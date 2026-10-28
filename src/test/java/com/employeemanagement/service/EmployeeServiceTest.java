package com.employeemanagement.service;

import com.employeemanagement.dto.EmployeeDTO;
import com.employeemanagement.entity.Employee;
import com.employeemanagement.entity.Department;
import com.employeemanagement.exception.ResourceNotFoundException;
import com.employeemanagement.exception.BadRequestException;
import com.employeemanagement.repository.EmployeeRepository;
import com.employeemanagement.repository.DepartmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("Employee Service Tests")
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee employee;
    private EmployeeDTO employeeDTO;
    private Department department;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        department = Department.builder()
                .id(1L)
                .name("IT")
                .build();

        employee = Employee.builder()
                .id(1L)
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

        employeeDTO = EmployeeDTO.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .phone("+1-555-0100")
                .dateOfBirth(LocalDate.of(1990, 1, 15))
                .position("Senior Developer")
                .departmentId(1L)
                .departmentName("IT")
                .salary(120000.0)
                .hireDate(LocalDate.of(2020, 1, 20))
                .isActive(true)
                .build();
    }

    @Test
    @DisplayName("Should create employee successfully")
    void testCreateEmployee() {
        when(employeeRepository.findByEmail(employeeDTO.getEmail())).thenReturn(Optional.empty());
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(modelMapper.map(employeeDTO, Employee.class)).thenReturn(employee);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        when(modelMapper.map(employee, EmployeeDTO.class)).thenReturn(employeeDTO);

        EmployeeDTO result = employeeService.createEmployee(employeeDTO);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("john@example.com", result.getEmail());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void testCreateEmployeeWithDuplicateEmail() {
        when(employeeRepository.findByEmail(employeeDTO.getEmail())).thenReturn(Optional.of(employee));

        assertThrows(BadRequestException.class, () -> {
            employeeService.createEmployee(employeeDTO);
        });
        verify(employeeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update employee successfully")
    void testUpdateEmployee() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.findByEmail(employeeDTO.getEmail())).thenReturn(Optional.of(employee));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        when(modelMapper.map(employee, EmployeeDTO.class)).thenReturn(employeeDTO);

        EmployeeDTO result = employeeService.updateEmployee(1L, employeeDTO);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    @DisplayName("Should throw exception when employee not found for update")
    void testUpdateEmployeeNotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            employeeService.updateEmployee(1L, employeeDTO);
        });
    }

    @Test
    @DisplayName("Should get employee by ID successfully")
    void testGetEmployeeById() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(modelMapper.map(employee, EmployeeDTO.class)).thenReturn(employeeDTO);

        EmployeeDTO result = employeeService.getEmployeeById(1L);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        verify(employeeRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when employee not found")
    void testGetEmployeeByIdNotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            employeeService.getEmployeeById(1L);
        });
    }

    @Test
    @DisplayName("Should get all employees")
    void testGetAllEmployees() {
        List<Employee> employees = Arrays.asList(employee);
        when(employeeRepository.findAll()).thenReturn(employees);
        when(modelMapper.map(employee, EmployeeDTO.class)).thenReturn(employeeDTO);

        List<EmployeeDTO> result = employeeService.getAllEmployees();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should delete employee successfully")
    void testDeleteEmployee() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        doNothing().when(employeeRepository).delete(employee);

        employeeService.deleteEmployee(1L);

        verify(employeeRepository, times(1)).delete(employee);
    }

    @Test
    @DisplayName("Should deactivate employee successfully")
    void testDeactivateEmployee() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        employeeService.deactivateEmployee(1L);

        assertFalse(employee.getIsActive());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    @DisplayName("Should search employees successfully")
    void testSearchEmployees() {
        List<Employee> employees = Arrays.asList(employee);
        when(employeeRepository.searchEmployees("John")).thenReturn(employees);
        when(modelMapper.map(employee, EmployeeDTO.class)).thenReturn(employeeDTO);

        List<EmployeeDTO> result = employeeService.searchEmployees("John");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(employeeRepository, times(1)).searchEmployees("John");
    }
}
