package com.employeemanagement.repository;

import com.employeemanagement.entity.Department;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("Department Repository Integration Tests")
class DepartmentRepositoryTest {

    @Autowired
    private DepartmentRepository departmentRepository;

    private Department department;

    @BeforeEach
    void setUp() {
        department = Department.builder()
                .name("Information Technology")
                .description("IT Department")
                .build();
        departmentRepository.save(department);
    }

    @Test
    @DisplayName("Should find department by name")
    void testFindByName() {
        Optional<Department> result = departmentRepository.findByName("Information Technology");

        assertTrue(result.isPresent());
        assertEquals("Information Technology", result.get().getName());
        assertEquals("IT Department", result.get().getDescription());
    }

    @Test
    @DisplayName("Should return empty when name not found")
    void testFindByNameNotFound() {
        Optional<Department> result = departmentRepository.findByName("Non-existent");

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should search departments by name")
    void testSearchDepartmentsByName() {
        List<Department> result = departmentRepository.searchDepartments("Information");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(d -> d.getName().contains("Information")));
    }

    @Test
    @DisplayName("Should search departments case insensitive")
    void testSearchDepartmentsCaseInsensitive() {
        List<Department> result = departmentRepository.searchDepartments("it");

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Should save and persist department")
    void testSaveDepartment() {
        Department newDept = Department.builder()
                .name("Human Resources")
                .description("HR Department")
                .build();

        Department saved = departmentRepository.save(newDept);

        assertNotNull(saved.getId());
        assertEquals("Human Resources", saved.getName());
        assertEquals("HR Department", saved.getDescription());
    }

    @Test
    @DisplayName("Should update department")
    void testUpdateDepartment() {
        department.setDescription("Updated IT Department");
        Department updated = departmentRepository.save(department);

        Optional<Department> result = departmentRepository.findById(updated.getId());
        assertTrue(result.isPresent());
        assertEquals("Updated IT Department", result.get().getDescription());
    }

    @Test
    @DisplayName("Should delete department")
    void testDeleteDepartment() {
        departmentRepository.delete(department);

        Optional<Department> result = departmentRepository.findById(department.getId());
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should find all departments")
    void testFindAll() {
        List<Department> result = departmentRepository.findAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(d -> d.getId().equals(department.getId())));
    }
}
