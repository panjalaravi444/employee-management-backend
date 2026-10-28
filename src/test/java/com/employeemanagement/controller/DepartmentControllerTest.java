package com.employeemanagement.controller;

import com.employeemanagement.dto.DepartmentDTO;
import com.employeemanagement.service.DepartmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DepartmentController.class)
@DisplayName("Department Controller Tests")
class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DepartmentService departmentService;

    @Autowired
    private ObjectMapper objectMapper;

    private DepartmentDTO departmentDTO;

    @BeforeEach
    void setUp() {
        departmentDTO = DepartmentDTO.builder()
                .id(1L)
                .name("Information Technology")
                .description("IT Department")
                .build();
    }

    @Test
    @DisplayName("Should create department and return 201")
    void testCreateDepartment() throws Exception {
        when(departmentService.createDepartment(any(DepartmentDTO.class))).thenReturn(departmentDTO);

        mockMvc.perform(post("/v1/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(departmentDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Information Technology"));

        verify(departmentService, times(1)).createDepartment(any(DepartmentDTO.class));
    }

    @Test
    @DisplayName("Should update department and return 200")
    void testUpdateDepartment() throws Exception {
        when(departmentService.updateDepartment(eq(1L), any(DepartmentDTO.class))).thenReturn(departmentDTO);

        mockMvc.perform(put("/v1/departments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(departmentDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Information Technology"));

        verify(departmentService, times(1)).updateDepartment(eq(1L), any(DepartmentDTO.class));
    }

    @Test
    @DisplayName("Should get department by ID and return 200")
    void testGetDepartmentById() throws Exception {
        when(departmentService.getDepartmentById(1L)).thenReturn(departmentDTO);

        mockMvc.perform(get("/v1/departments/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Information Technology"));

        verify(departmentService, times(1)).getDepartmentById(1L);
    }

    @Test
    @DisplayName("Should get all departments and return 200")
    void testGetAllDepartments() throws Exception {
        List<DepartmentDTO> departments = Arrays.asList(departmentDTO);
        when(departmentService.getAllDepartments()).thenReturn(departments);

        mockMvc.perform(get("/v1/departments")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value("Information Technology"));

        verify(departmentService, times(1)).getAllDepartments();
    }

    @Test
    @DisplayName("Should search departments and return 200")
    void testSearchDepartments() throws Exception {
        List<DepartmentDTO> departments = Arrays.asList(departmentDTO);
        when(departmentService.searchDepartments("IT")).thenReturn(departments);

        mockMvc.perform(get("/v1/departments/search?term=IT")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());

        verify(departmentService, times(1)).searchDepartments("IT");
    }

    @Test
    @DisplayName("Should delete department and return 200")
    void testDeleteDepartment() throws Exception {
        doNothing().when(departmentService).deleteDepartment(1L);

        mockMvc.perform(delete("/v1/departments/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(departmentService, times(1)).deleteDepartment(1L);
    }
}
