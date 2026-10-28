package com.employeemanagement.service;

import com.employeemanagement.dto.DepartmentDTO;
import com.employeemanagement.entity.Department;
import com.employeemanagement.exception.ResourceNotFoundException;
import com.employeemanagement.exception.BadRequestException;
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
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private ModelMapper modelMapper;

    public DepartmentDTO createDepartment(DepartmentDTO departmentDTO) {
        log.info("Creating department with name: {}", departmentDTO.getName());

        if (departmentRepository.findByName(departmentDTO.getName()).isPresent()) {
            throw new BadRequestException("Department with name " + departmentDTO.getName() + " already exists");
        }

        Department department = modelMapper.map(departmentDTO, Department.class);
        Department savedDepartment = departmentRepository.save(department);
        log.info("Department created successfully with id: {}", savedDepartment.getId());

        return modelMapper.map(savedDepartment, DepartmentDTO.class);
    }

    public DepartmentDTO updateDepartment(Long id, DepartmentDTO departmentDTO) {
        log.info("Updating department with id: {}", id);

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));

        if (!department.getName().equals(departmentDTO.getName()) &&
            departmentRepository.findByName(departmentDTO.getName()).isPresent()) {
            throw new BadRequestException("Department with name " + departmentDTO.getName() + " already exists");
        }

        department.setName(departmentDTO.getName());
        department.setDescription(departmentDTO.getDescription());

        Department updatedDepartment = departmentRepository.save(department);
        log.info("Department updated successfully with id: {}", id);

        return modelMapper.map(updatedDepartment, DepartmentDTO.class);
    }

    public DepartmentDTO getDepartmentById(Long id) {
        log.info("Fetching department with id: {}", id);
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));
        return modelMapper.map(department, DepartmentDTO.class);
    }

    @Transactional(readOnly = true)
    public List<DepartmentDTO> getAllDepartments() {
        log.info("Fetching all departments");
        return departmentRepository.findAll().stream()
                .map(dept -> modelMapper.map(dept, DepartmentDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DepartmentDTO> searchDepartments(String searchTerm) {
        log.info("Searching departments with term: {}", searchTerm);
        return departmentRepository.searchDepartments(searchTerm).stream()
                .map(dept -> modelMapper.map(dept, DepartmentDTO.class))
                .collect(Collectors.toList());
    }

    public void deleteDepartment(Long id) {
        log.info("Deleting department with id: {}", id);
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));

        if (department.getEmployees() != null && !department.getEmployees().isEmpty()) {
            throw new BadRequestException("Cannot delete department that has employees");
        }

        departmentRepository.delete(department);
        log.info("Department deleted successfully with id: {}", id);
    }
}
