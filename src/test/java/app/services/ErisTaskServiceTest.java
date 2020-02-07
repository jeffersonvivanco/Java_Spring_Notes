package app.services;

import app.models.Task;
import app.models.entities.TaskEntity;
import app.repositories.ErisTaskRepository;
import app.security.IAuthenticationFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringJUnitConfig
class ErisTaskServiceTest {

    Authentication authentication = Mockito.mock(Authentication.class);
    Task task;

    @TestConfiguration
    static class Config {
        @MockBean
        ErisTaskRepository erisTaskRepository;
        @MockBean
        IAuthenticationFacade authenticationFacade;
        @Bean
        ErisTaskService erisTaskService() {
            return new ErisTaskService(erisTaskRepository, authenticationFacade);
        }
    }

    @Autowired
    ErisTaskService erisTaskService;
    @Autowired
    IAuthenticationFacade authenticationFacade;
    @Autowired
    ErisTaskRepository erisTaskRepository;

    @BeforeEach
    void setUp() {
        when(authentication.getName()).thenReturn("test");
        when(authenticationFacade.getAuthentication()).thenReturn(authentication);
        task = new Task();
    }

    @Test
    @DisplayName("Save-Task-No-Name")
    void saveTaskWithNoName() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> erisTaskService.saveTask(task));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void saveTask() {
        task.setName("task");
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setName("task");
        when(erisTaskRepository.save(any())).thenReturn(taskEntity);
        assertEquals(task.getName(), erisTaskService.saveTask(task).getName());
    }

    @Test
    @DisplayName("Get-No-Tasks")
    void getNoTasks() {
        when(erisTaskRepository.findAllByAddedBy(anyString()))
                .thenReturn(List.of());
        assertEquals(0, erisTaskService.getTasks().size());
    }

    @Test
    void getTasks() {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setName("task");
        when(erisTaskRepository.findAllByAddedBy(anyString()))
                .thenReturn(List.of(taskEntity));
        assertEquals(1, erisTaskService.getTasks().size());
    }
}