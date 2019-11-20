package app.services;

import app.models.Status;
import app.models.entities.TaskEntity;
import app.repositories.ErisTaskRepository;
import app.security.ErisUserAuth;
import app.security.IAuthenticationFacade;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@SpringJUnitConfig(classes = {ErisTaskService.class})
class ErisTaskServiceTest {

    @Autowired
    private ErisTaskService erisTaskService;

    @MockBean
    private ErisTaskRepository erisTaskRepository;

    @MockBean
    private IAuthenticationFacade authenticationFacade;

    @Test
    void saveTask_Test(){
        ErisUserAuth erisUserAuth = new ErisUserAuth("user", "password", Collections.EMPTY_LIST);
        given(authenticationFacade.getAuthentication()).willReturn(new UsernamePasswordAuthenticationToken(erisUserAuth, "password", erisUserAuth.getAuthorities()));
        TaskEntity t = new TaskEntity();
        t.setName("Build schools");
        t.setDescription("Build new elementary schools");
//        Status resp = erisTaskService.saveTask(t);
//        assertEquals(200, resp.getStatus());
    }
}