package com.example.task.controller;

import com.example.task.model.Task;
import com.example.task.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskService taskService; // real TaskService object automatically injected in TaskController instance

    @BeforeEach
    void setUp() {
        new ArrayList<>(taskService.getAllTasks()).forEach(task ->
                taskService.deleteTask(task.getTaskId())
        );
    }

    @Test
    void shouldReturnAllTasks() throws Exception {

        taskService.addTask("Task 1");

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].taskDescription").value("Task 1"))
                .andExpect(jsonPath("$[0].taskId").isNotEmpty());
    }
    @Test void shouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty()); }

    @Test
    void shouldCreateTask() throws Exception {
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\": \"Nouvelle tâche\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.taskDescription").value("Nouvelle tâche"))
                .andExpect(jsonPath("$.taskId").isNotEmpty());
    }

    @Test
    void shouldDeleteTask() throws Exception {
        Task created = taskService.addTask("Task à supprimer");

        mockMvc.perform(delete("/tasks/"+ created.getTaskId()))
                .andExpect(status().isOk());
    }
    @Test
    void shouldReturn404WhenDeletingUnknownId() throws Exception {
        mockMvc.perform(delete("/tasks/id-qui-nexiste-pas"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCompleteTask() throws Exception {
        Task created = taskService.addTask("Task à compléter");
        mockMvc.perform(put("/tasks/" + created.getTaskId() + "/complete"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn404WhenCompletingUnknownId() throws Exception {
        mockMvc.perform(put("/tasks/id-qui-nexiste-pas/complete"))
                .andExpect(status().isNotFound());
    }
}
