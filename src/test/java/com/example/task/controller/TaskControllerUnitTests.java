package com.example.task.controller;

import com.example.task.model.Task;
import com.example.task.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;

@WebMvcTest(TaskController.class)
public class TaskControllerUnitTests {
    @Autowired
    private MockMvc mockMvc; //Object used to make HTTP request on our API

    @MockitoBean
    private TaskService taskService; //Mock object TaskService automatically injected in TaskController instance

    @Test
    void hello_should_return_message() throws Exception {
        mockMvc.perform(get("/tasks/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("Welcome to the Task Manager API!"));
    }

    @Test
    void shouldReturnAllTasks() throws Exception {

        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task("Task 1"));
        tasks.add(new Task("Task 2"));

        when(taskService.getAllTasks()).thenReturn(tasks);

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].taskDescription").value("Task 1"))
                .andExpect(jsonPath("$[0].taskId").isNotEmpty())
                .andExpect(jsonPath("$[0].completed").value(false))
                .andExpect(jsonPath("$[1].taskDescription").value("Task 2"));

        verify(taskService, times(1)).getAllTasks();
    }

    @Test
    void shouldCreateTask() throws Exception {
        Task created = new Task("Nouvelle tâche");
        when(taskService.addTask("Nouvelle tâche")).thenReturn(created);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\": \"Nouvelle tâche\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.taskDescription").value("Nouvelle tâche"))
                .andExpect(jsonPath("$.completed").value(false));

        verify(taskService, times(1)).addTask("Nouvelle tâche");
    }

    @Test
    void shouldDeleteTask() throws Exception {
        String id = UUID.randomUUID().toString(); // String, pas UUID

        mockMvc.perform(delete("/tasks/" + id))
                .andExpect(status().isOk());

        verify(taskService, times(1)).deleteTask(any(String.class));
    }
    @Test
    void shouldReturn404WhenDeletingUnknownId() throws Exception {
        String id = UUID.randomUUID().toString();
        doThrow(new NoSuchElementException()).when(taskService).deleteTask(any(String.class));

        mockMvc.perform(delete("/tasks/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCompleteTask() throws Exception {
        String id = UUID.randomUUID().toString();

        doNothing().when(taskService).completeTask(any(String.class));

        mockMvc.perform(put("/tasks/" + id + "/complete"))
                .andExpect(status().isOk());

        verify(taskService, times(1)).completeTask(any(String.class));
    }

    @Test
    void shouldReturn404WhenCompletingUnknownId() throws Exception {
        String id = UUID.randomUUID().toString();
        doThrow(new NoSuchElementException()).when(taskService).completeTask(any(String.class));

        mockMvc.perform(put("/tasks/" + id + "/complete"))
                .andExpect(status().isNotFound());
    }

}
