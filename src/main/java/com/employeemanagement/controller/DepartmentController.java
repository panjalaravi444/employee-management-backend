package com.employeemanagement.controller;

import com.employeemanagement.dto.DepartmentDTO;
import com.employeemanagement.dto.ApiResponse;
import com.employeemanagement.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/departments")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @PostMapping
    public ResponseEntity<ApiResponse<DepartmentDTO>> createDepartment(
            @Valid @RequestBody DepartmentDTO departmentDTO) {
        log.info("POST request to create department");
        DepartmentDTO createdDepartment = departmentService.createDepartment(departmentDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Department created successfully", createdDepartment));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DepartmentDTO>> updateDepartment(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentDTO departmentDTO) {
        log.info("PUT request to update department with id: {}", id);
        DepartmentDTO updatedDepartment = departmentService.updateDepartment(id, departmentDTO);
        return ResponseEntity
                .ok(ApiResponse.success("Department updated successfully", updatedDepartment));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DepartmentDTO>> getDepartmentById(@PathVariable Long id) {
        log.info("GET request to fetch department with id: {}", id);
        DepartmentDTO department = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(ApiResponse.success("Department fetched successfully", department));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DepartmentDTO>>> getAllDepartments() {
        log.info("GET request to fetch all departments");
        List<DepartmentDTO> departments = departmentService.getAllDepartments();
        return ResponseEntity.ok(ApiResponse.success("All departments fetched successfully", departments));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<DepartmentDTO>>> searchDepartments(
            @RequestParam String term) {
        log.info("GET request to search departments with term: {}", term);
        List<DepartmentDTO> departments = departmentService.searchDepartments(term);
        return ResponseEntity.ok(ApiResponse.success("Departments search successful", departments));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDepartment(@PathVariable Long id) {
        log.info("DELETE request to delete department with id: {}", id);
        departmentService.deleteDepartment(id);
        return ResponseEntity.ok(ApiResponse.success("Department deleted successfully", null));
    }
}
