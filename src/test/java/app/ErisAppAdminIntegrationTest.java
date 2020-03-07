package app;

import app.exceptions.ErisAppException;
import app.models.ErisUser;
import app.models.Task;
import app.services.ErisUserService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ErisAppAdminIntegrationTest {
    ErisUser admin;
    ErisAppAuthHelper adminAuthHelper;
    ObjectMapper objectMapper;
    private final String usersApiPrefix = "/api/auth/user";
    private final String adminApiPrefix = "/api/auth/admin";
    @Autowired
    private ErisUserService erisUserService;
    @Autowired
    private MockMvc mockMvc;
    @BeforeEach
    void setUp() {
        admin = new ErisUser("admin@gmail.com", "password", "Admin");
        objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        adminAuthHelper = new ErisAppAuthHelper(mockMvc, objectMapper);
    }

    @Test
    @Order(0)
    void signUpAndLogin() throws Exception {
        assertNotNull(erisUserService.signUpAdmin(admin));
        adminAuthHelper.login(admin);

        // testing signing up admin twicw, should throw exception
        ErisAppException erisAppException = assertThrows(ErisAppException.class, () -> erisUserService.signUpAdmin(admin));
        assertEquals("username already exists", erisAppException.getMessage());
    }

    @Test
    void adminInfo() throws Exception {
        adminAuthHelper.login(admin);
        // user is getting user info
        mockMvc.perform(adminAuthHelper.makeRequest(HttpMethod.GET, usersApiPrefix + "/getUserInfo"))
                .andExpect(status().isOk());

        admin.setFullName("Administrator");
        String expectedResponse = objectMapper.writeValueAsString(admin);
        mockMvc.perform(adminAuthHelper.makeRequest(HttpMethod.POST, usersApiPrefix + "/updateUserInfo")
        .contentType(MediaType.APPLICATION_JSON).content(adminAuthHelper.erisUserToString(admin)))
                .andExpect(status().isOk()).andExpect(content().json(expectedResponse));
    }

    @Test
    void saveTask() throws Exception {
        adminAuthHelper.login(admin);
        Task task = new Task("Route 6", "Build new highway");
        mockMvc.perform(adminAuthHelper.makeRequest(HttpMethod.POST, adminApiPrefix + "/task/saveTask")
        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isOk());
    }

}
