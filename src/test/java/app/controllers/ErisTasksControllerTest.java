package app.controllers;

import app.models.Task;
import app.services.ErisTaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

@SpringJUnitConfig
public class ErisTasksControllerTest {

    private MockMvc mockMvc;
    private Task task;
    private final String usersApiPrefix = "/api/auth/user";
    private final String adminApiPrefix = "/api/auth/admin";
    private ObjectMapper objectMapper = new ObjectMapper();

    @TestConfiguration
    static class Config {
        @MockBean
        private ErisTaskService erisTaskService;
        @Bean
        ErisTasksController erisTasksController() {
            return new ErisTasksController(erisTaskService);
        }
        @Bean
        ErisTasksAdminController erisTasksAdminController() {
            return new ErisTasksAdminController(erisTaskService);
        }
    }

    @Autowired
    private ErisTasksController erisTasksController;
    @Autowired
    private ErisTasksAdminController erisTasksAdminController;
    @Autowired
    private ErisTaskService erisTaskService;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(erisTasksController, erisTasksAdminController).build();
        task = new Task();
        task.setName("test");
        task.setDescription("write tests");
    }

    @Test
    void getAllTasks() throws Exception {
        List<Task> tasks = List.of(task);
        when(erisTaskService.getTasks()).thenReturn(tasks);
        this.mockMvc.perform(get(usersApiPrefix + "/task/getAllTasks"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(tasks)));
    }

    @Test
    void saveTask() throws Exception {
        this.task.setName("new name"); // changing name, testing this
        when(erisTaskService.saveTask(any())).thenReturn(this.task);
        this.mockMvc.perform(post(adminApiPrefix + "/task/saveTask")
                .content(objectMapper.writeValueAsString(this.task))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(this.task)));
    }
}
