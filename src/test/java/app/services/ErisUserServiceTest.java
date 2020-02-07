package app.services;

import app.repositories.ErisUserRepository;
import app.security.IAuthenticationFacade;
import app.security.services.ErisUserSecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig
class ErisUserServiceTest {

    @TestConfiguration
    static class Config {
        @MockBean
        ErisUserRepository erisUserRepository;
        @MockBean
        ErisUserSecurityService erisUserSecurityService;
        @MockBean
        IAuthenticationFacade authenticationFacade;

        @Bean
        ErisUserService erisUserService() {
            return new ErisUserService(erisUserRepository, erisUserSecurityService, authenticationFacade);
        }
    }



    @BeforeEach
    void setUp() {

    }

    @Test
    void signUp() {

    }

    @Test
    void updateUserDetails() {

    }

    @Test
    void getUserInfo() {

    }
}