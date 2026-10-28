package com.employeemanagement.service;

import com.employeemanagement.dto.DepartmentDTO;
import com.employeemanagement.entity.Department;
import com.employeemanagement.exception.ResourceNotFoundException;
import com.employeemanagement.exception.BadRequestException;
import com.employeemanagement.repository.DepartmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("Department Service Tests")
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private DepartmentService departmentService;

    private Department department;
    private DepartmentDTO departmentDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        department = Department.builder()
                .id(1L)
                .name("Information Technology")
                .description("IT Department")
                .employees(Collections.emptyList())
                .build();

        departmentDTO = DepartmentDTO.builder()
                .id(1L)
                .name("Information Technology")
                .description("IT Department")
                .build();
    }

    @Test
    @DisplayName("Should create department successfully")
    void testCreateDepartment() {
        when(departmentRepository.findByName(departmentDTO.getName())).thenReturn(Optional.empty());
        when(modelMapper.map(departmentDTO, Department.class)).thenReturn(department);
        when(departmentRepository.save(any(Department.class))).thenReturn(department);
        when(modelMapper.map(department, DepartmentDTO.class)).thenReturn(departmentDTO);

        DepartmentDTO result = departmentService.createDepartment(departmentDTO);

        assertNotNull(result);
        assertEquals("Information Technology", result.getName());
        verify(departmentRepository, times(1)).save(any(Department.class));
    }

    @Test
    @DisplayName("Should throw exception when department name already exists")
    void testCreateDepartmentWithDuplicateName() {
        when(departmentRepository.findByName(departmentDTO.getName())).thenReturn(Optional.of(department));

        assertThrows(BadRequestException.class, () -> {
            departmentService.createDepartment(departmentDTO);
        });
        verify(departmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update department successfully")
    void testUpdateDepartment() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(departmentRepository.findByName(departmentDTO.getName())).thenReturn(Optional.of(department));
        when(departmentRepository.save(any(Department.class))).thenReturn(department);
        when(modelMapper.map(department, DepartmentDTO.class)).thenReturn(departmentDTO);

        DepartmentDTO result = departmentService.updateDepartment(1L, departmentDTO);

        assertNotNull(result);
        assertEquals("Information Technology", result.getName());
        verify(departmentRepository, times(1)).save(any(Department.class));
    }

    @Test
    @DisplayName("Should throw exception when department not found for update")
    void testUpdateDepartmentNotFound() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            departmentService.updateDepartment(1L, departmentDTO);
        });
    }

    @Test
    @DisplayName("Should get department by ID successfully")
    void testGetDepartmentById() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(modelMapper.map(department, DepartmentDTO.class)).thenReturn(departmentDTO);

        DepartmentDTO result = departmentService.getDepartmentById(1L);

        assertNotNull(result);
        assertEquals("Information Technology", result.getName());
        verify(departmentRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when department not found")
    void testGetDepartmentByIdNotFound() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            departmentService.getDepartmentById(1L);
        });
    }

    @Test
    @DisplayName("Should get all departments")
    void testGetAllDepartments() {
        List<Department> departments = Arrays.asList(department);
        when(departmentRepository.findAll()).thenReturn(departments);
        when(modelMapper.map(department, DepartmentDTO.class)).thenReturn(departmentDTO);

        List<DepartmentDTO> result = departmentService.getAllDepartments();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(departmentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should delete department successfully")
    void testDeleteDepartment() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        doNothing().when(departmentRepository).delete(department);

        departmentService.deleteDepartment(1L);

        verify(departmentRepository, times(1)).delete(department);
    }

    @Test
    @DisplayName("Should throw exception when deleting department with employees")
    void testDeleteDepartmentWithEmployees() {
        department.setEmployees(Arrays.asList(new Employee())); // Has employees
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        assertThrows(BadRequestException.class, () -> {
            departmentService.deleteDepartment(1L);
        });
        verify(departmentRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should search departments successfully")
    void testSearchDepartments() {
        List<Department> departments = Arrays.asList(department);
        when(departmentRepository.searchDepartments("IT")).thenReturn(departments);
        when(modelMapper.map(department, DepartmentDTO.class)).thenReturn(departmentDTO);

        List<DepartmentDTO> result = departmentService.searchDepartments("IT");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(departmentRepository, times(1)).searchDepartments("IT");
    }
}
