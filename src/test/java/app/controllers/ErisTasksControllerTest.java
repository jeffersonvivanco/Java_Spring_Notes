package app.controllers;

import app.models.Status;
import app.models.entities.TaskEntity;
import app.repositories.ErisUserRepository;
import app.security.repositories.PrivilegeRepository;
import app.security.repositories.RoleRepository;
import app.services.ErisTaskService;
import app.services.ErisUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@ExtendWith(SpringExtension.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = PlanetErisApp.class)
@SpringJUnitWebConfig
//@WebMvcTest(ErisTasksController.class)
class ErisTasksControllerTest {

    @TestConfiguration
    @ComponentScan(basePackages = {"app.security"})
    static class Config {
        @MockBean
        ErisTaskService erisTaskService;

        @Bean
        ErisTasksController erisTasksController(){
            return new ErisTasksController(erisTaskService);
        }
    }

    private MockMvc mockMvc;
    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private ErisTaskService erisTaskService;

    @MockBean
    private ErisUserService erisUserService;

    @MockBean
    private ErisUserRepository erisUserRepository;

    @MockBean
    private PrivilegeRepository privilegeRepository;

    @MockBean
    private RoleRepository roleRepository;

    @BeforeEach
    void setup(WebApplicationContext wac){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).apply(springSecurity()).build();
//        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    @DisplayName("Not valid user test")
    void testSecuredEndpoints_NotValidUser() throws Exception {
        given(erisTaskService.getTasks()).willReturn(Collections.emptyList());
        mockMvc.perform(get("/api/secure/tasks/getAllTasks").accept(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Valid user but not authorized test")
    @WithMockUser(authorities = "READ_PRIVILEGE")
    void testSecuredEndpoints_ValidUser_NotAuthorized() throws Exception {
        TaskEntity task = new TaskEntity("Build Schools", "Build new elementary schools for kids");
//        given(erisTaskService.saveTask(any(TaskEntity.class))).willReturn(new Status());
        mockMvc.perform(post("/api/secure/tasks/saveTask").content(mapper.writeValueAsString(task)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Valid User")
    @WithMockUser(authorities = "WRITE_PRIVILEGE")
    void testSecuredEndpoints_ValidUser() throws Exception{
        TaskEntity t = new TaskEntity("test", "test description");
        t.setId(0);
        t.setAddedBy("test");
        ArrayList<TaskEntity> tasks = new ArrayList<>();
        tasks.add(t);
//        given(erisTaskService.getTasks()).willReturn(tasks);
        mockMvc.perform(get("/api/secure/tasks/getAllTasks")).andExpect(status().isOk());

//        given(erisTaskService.saveTask(any(Task.class))).willReturn(new Status());
//        mockMvc.perform(post("/api/secure/tasks/saveTask").accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(t)))
//                .andExpect(status().isOk());
    }

}