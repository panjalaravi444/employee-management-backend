package com.employeemanagement.service;

import com.employeemanagement.dto.EmployeeDTO;
import com.employeemanagement.entity.Employee;
import com.employeemanagement.entity.Department;
import com.employeemanagement.exception.ResourceNotFoundException;
import com.employeemanagement.exception.BadRequestException;
import com.employeemanagement.repository.EmployeeRepository;
import com.employeemanagement.repository.DepartmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private ModelMapper modelMapper;

    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        log.info("Creating employee with email: {}", employeeDTO.getEmail());

        if (employeeRepository.findByEmail(employeeDTO.getEmail()).isPresent()) {
            throw new BadRequestException("Employee with email " + employeeDTO.getEmail() + " already exists");
        }

        Department department = departmentRepository.findById(employeeDTO.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", employeeDTO.getDepartmentId()));

        Employee employee = modelMapper.map(employeeDTO, Employee.class);
        employee.setDepartment(department);

        Employee savedEmployee = employeeRepository.save(employee);
        log.info("Employee created successfully with id: {}", savedEmployee.getId());

        return mapToDTO(savedEmployee);
    }

    public EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDTO) {
        log.info("Updating employee with id: {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));

        if (!employee.getEmail().equals(employeeDTO.getEmail()) &&
            employeeRepository.findByEmail(employeeDTO.getEmail()).isPresent()) {
            throw new BadRequestException("Employee with email " + employeeDTO.getEmail() + " already exists");
        }

        Department department = departmentRepository.findById(employeeDTO.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", employeeDTO.getDepartmentId()));

        employee.setFirstName(employeeDTO.getFirstName());
        employee.setLastName(employeeDTO.getLastName());
        employee.setEmail(employeeDTO.getEmail());
        employee.setPhone(employeeDTO.getPhone());
        employee.setDateOfBirth(employeeDTO.getDateOfBirth());
        employee.setPosition(employeeDTO.getPosition());
        employee.setDepartment(department);
        employee.setSalary(employeeDTO.getSalary());
        employee.setHireDate(employeeDTO.getHireDate());
        employee.setIsActive(employeeDTO.getIsActive());

        Employee updatedEmployee = employeeRepository.save(employee);
        log.info("Employee updated successfully with id: {}", id);

        return mapToDTO(updatedEmployee);
    }

    public EmployeeDTO getEmployeeById(Long id) {
        log.info("Fetching employee with id: {}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        return mapToDTO(employee);
    }

    @Transactional(readOnly = true)
    public List<EmployeeDTO> getAllEmployees() {
        log.info("Fetching all employees");
        return employeeRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EmployeeDTO> getActiveEmployees() {
        log.info("Fetching all active employees");
        return employeeRepository.findAllActive().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EmployeeDTO> getEmployeesByDepartment(Long departmentId) {
        log.info("Fetching employees for department: {}", departmentId);
        return employeeRepository.findActiveEmployeesByDepartment(departmentId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EmployeeDTO> searchEmployees(String searchTerm) {
        log.info("Searching employees with term: {}", searchTerm);
        return employeeRepository.searchEmployees(searchTerm).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void deleteEmployee(Long id) {
        log.info("Deleting employee with id: {}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        employeeRepository.delete(employee);
        log.info("Employee deleted successfully with id: {}", id);
    }

    public void deactivateEmployee(Long id) {
        log.info("Deactivating employee with id: {}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        employee.setIsActive(false);
        employeeRepository.save(employee);
        log.info("Employee deactivated successfully with id: {}", id);
    }

    private EmployeeDTO mapToDTO(Employee employee) {
        EmployeeDTO dto = modelMapper.map(employee, EmployeeDTO.class);
        if (employee.getDepartment() != null) {
            dto.setDepartmentName(employee.getDepartment().getName());
        }
        return dto;
    }
}
