package com.example.task.service;

import com.example.task.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;
import static org.junit.jupiter.api.Assertions.*;

public class TaskServiceTests {

    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskService = new TaskService();
    }

    @Test
    void shouldAddTask() {
        Task task = taskService.addTask("Ma tâche");
        assertNotNull(task.getTaskId());           // l'id ne doit pas être null
        assertEquals("Ma tâche", task.getTaskDescription()); // la description doit correspondre
        assertFalse(task.isCompleted());             // le statut doit être false
    }
    @Test
    void shouldReturnAllTasks() {
        taskService.addTask("Tâche 1");
        taskService.addTask("Tâche 2");
        List<Task> tasks = taskService.getAllTasks();
        assertEquals(2, tasks.size());
    }
    @Test
    void shouldReturnEmptyList() {
        List<Task> tasks = taskService.getAllTasks();
        assertNotNull(tasks);
        assertEquals(0, tasks.size());
    }

    @Test
    void shouldDeleteExistingTask() {
        Task task = taskService.addTask("Tâche à supprimer");
        taskService.deleteTask(task.getTaskId());
        assertEquals(0, taskService.getAllTasks().size());
    }
    @Test
    void shouldNotDeleteInexistingTask() {
        assertThrows(NoSuchElementException.class, () -> taskService.deleteTask("id-qui-nexiste-pas"));
    }

    @Test
    void CompleteTask() {
        Task task = taskService.addTask("Tâche à compléter");
        taskService.completeTask(task.getTaskId());
        assertTrue(task.isCompleted());
    }
    @Test
    void CompleteInexistingTask() {
        assertThrows(NoSuchElementException.class, () -> taskService.completeTask("id-qui-nexiste-pas"));
    }
}