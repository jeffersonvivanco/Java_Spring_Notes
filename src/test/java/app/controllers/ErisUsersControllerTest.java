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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringJUnitConfig
class ErisUsersControllerTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();
    private final String securePrefix = "/api/secure/users";
    private final String prefix = "/api/users";
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
        ErisUsersSecureController erisUsersSecureController() {
            return new ErisUsersSecureController(erisUserService);
        }
    }

    @Autowired
    ErisUsersController erisUsersController;
    @Autowired
    ErisUsersSecureController erisUsersSecureController;
    @Autowired
    ErisUserService erisUserService;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(erisUsersController, erisUsersSecureController).build();
        erisUser = new ErisUser();
        erisUser.setUsername("test_user");
    }

    @Test
    void signUp() throws Exception {
        when(erisUserService.signUp(any())).thenReturn(erisUser);
        mockMvc.perform(post(prefix + "/sign-up").contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(erisUser))).andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(erisUser)));
    }

    @Test
    void getUserInfo() throws Exception {
        when(erisUserService.getUserInfo()).thenReturn(erisUser);
        mockMvc.perform(get(securePrefix + "/getUserInfo")).andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(erisUser)));
    }

    @Test
    void updateUserInfo() throws Exception {
        erisUser.setFullName("Test User");
        when(erisUserService.updateUserDetails(any())).thenReturn(erisUser);
        mockMvc.perform(post(securePrefix +"/updateUserInfo").contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(erisUser))).andExpect(status().isOk());
    }
}