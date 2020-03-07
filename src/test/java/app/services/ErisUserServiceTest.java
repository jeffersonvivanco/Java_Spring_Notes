package app.services;

import app.models.ErisUser;
import app.models.entities.ErisUserEntity;
import app.repositories.ErisUserRepository;
import app.security.IAuthenticationFacade;
import app.security.services.ErisUserSecurityService;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringJUnitConfig
class ErisUserServiceTest {

    Authentication authentication = Mockito.mock(Authentication.class);
    ErisUserEntity erisUserEntity;

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
    @Autowired
    ErisUserService erisUserService;
    @Autowired
    IAuthenticationFacade authenticationFacade;
    @Autowired
    ErisUserRepository erisUserRepository;

    @BeforeEach
    void setUp() {
        when(authentication.getName()).thenReturn("test");
        when(authenticationFacade.getAuthentication()).thenReturn(authentication);
        erisUserEntity = new ErisUserEntity();
        erisUserEntity.setUsername("test");
    }

    @Test
    @DisplayName("Sign-Up-Fail-Validation")
    void signUpFailValidation() {
        ErisUser erisUser = new ErisUser();
        erisUser.setUsername("");
        erisUser.setPassword("password");
        // username is empty
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> erisUserService.signUp(erisUser));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

        erisUser.setUsername("t1");
        // username length < 4
        ResponseStatusException exception2 = assertThrows(ResponseStatusException.class,
                () -> erisUserService.signUp(erisUser));
        assertEquals(HttpStatus.BAD_REQUEST, exception2.getStatus());

        when(erisUserRepository.findByUsername("test")).thenReturn(Optional.of(erisUserEntity));
        // user already exists
        erisUser.setUsername("test");
        ResponseStatusException exception3 = assertThrows(ResponseStatusException.class,
                () -> erisUserService.signUp(erisUser));
        assertEquals(HttpStatus.BAD_REQUEST, exception3.getStatus());
    }

    @Test
    void signUp() {
        ErisUser erisUser = new ErisUser();
        erisUser.setUsername("test");
        erisUser.setPassword("password");
        assertEquals(erisUser, erisUserService.signUp(erisUser));
    }

    @Test
    void updateUserDetails() {
        ErisUser erisUser = new ErisUser();
        erisUser.setFullName("Test 1");
        erisUser.setUsername("test");
        when(erisUserRepository.findByUsername(any())).thenReturn(Optional.of(erisUserEntity));
        assertEquals(erisUser.getFullName(),
                erisUserService.updateUserDetails(erisUser).getFullName());
    }

    @Test
    @DisplayName("User-Info-User-Not-Exist")
    void getUserInfoNotExist() {
        when(erisUserRepository.findByUsername(any())).thenReturn(Optional.empty());
        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class,
                () -> erisUserService.getUserInfo());
        assertEquals(HttpStatus.BAD_REQUEST, responseStatusException.getStatus());
    }

    @Test
    void getUserInfo() {
        when(erisUserRepository.findByUsername(any())).thenReturn(Optional.of(erisUserEntity));
        assertEquals(erisUserEntity.getUsername(),
                erisUserService.getUserInfo().getUsername());
    }
}