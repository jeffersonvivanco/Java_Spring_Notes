package app.controllers;

import app.models.ErisUser;
import app.services.ErisUserService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringJUnitConfig
class ErisUsersControllerTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();
    private final String usersApiPrefix = "/api/auth/user";
    private ErisUser erisUser;

    @TestConfiguration
    static class Config {
        @MockBean
        ErisUserService erisUserService;
        @Bean
        ErisUsersController erisUsersController() {
            return new ErisUsersController(erisUserService);
        }
        @Bean
        ErisUsersAdminController erisUsersAdminController() {
            return new ErisUsersAdminController(erisUserService);
        }
        @Bean
        ErisSignUpController erisSignUpController() {
            return new ErisSignUpController(erisUserService);
        }
    }

    @Autowired
    ErisUsersController erisUsersController;
    @Autowired
    ErisUsersAdminController erisUsersAdminController;
    @Autowired
    ErisSignUpController erisSignUpController;
    @Autowired
    ErisUserService erisUserService;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(erisUsersController, erisUsersAdminController, erisSignUpController).build();
        erisUser = new ErisUser();
        erisUser.setUsername("test_user");
        erisUser.setPassword("password");
    }

    @Test
    void signUp() throws Exception {
        when(erisUserService.signUp(any())).thenReturn(erisUser);
        mockMvc.perform(post("/api/sign-up").contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(erisUser))).andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(erisUser)));
    }

    @Test
    void getUserInfo() throws Exception {
        when(erisUserService.getUserInfo()).thenReturn(erisUser);
        ErisUser erisUserResponse = new ErisUser();
        erisUserResponse.setUsername("test_user");
        mockMvc.perform(get(usersApiPrefix + "/getUserInfo")).andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(erisUserResponse)));
    }

    @Test
    void updateUserInfo() throws Exception {
        erisUser.setFullName("Test User");
        when(erisUserService.updateUserDetails(any())).thenReturn(erisUser);
        mockMvc.perform(post(usersApiPrefix +"/updateUserInfo").contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(erisUser))).andExpect(status().isOk());
    }
}