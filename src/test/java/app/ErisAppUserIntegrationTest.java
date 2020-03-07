package app;

import app.models.ErisUser;
import app.models.Task;
import app.security.constants.SecurityConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ErisAppUserIntegrationTest {

    ObjectMapper objectMapper;
    private static Logger logger = LoggerFactory.getLogger(ErisAppUserIntegrationTest.class);
    ErisAppAuthHelper userAuthHelper;
    ErisUser erisUser;
    private final String usersApiPrefix = "/api/auth/user";
    private final String adminApiPrefix = "/api/auth/admin";
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        erisUser = new ErisUser("test", "password", "Test 1");
        userAuthHelper = new ErisAppAuthHelper(mockMvc, objectMapper);
    }

    /*
    this test ensures that the user can sign up and login
    note: user can only sign up once, and it has to happen
    before all authenticated requests, so we set the order
    to 0
     */
    @Test
    @Order(0)
    void signUpAndLogin() throws Exception {
        userAuthHelper.signUp(erisUser);
        userAuthHelper.login(erisUser);
    }

    @Test
    void userInfo() throws Exception {
        // since we are performing authenticated requests, we have to login
        userAuthHelper.login(erisUser);

        // user is getting user info
        mockMvc.perform(userAuthHelper.makeRequest(HttpMethod.GET, usersApiPrefix + "/getUserInfo"))
                .andExpect(status().isOk());

        erisUser.setFullName("Test 2");
        String erisUserWithOutPassword = """
                        {
                        "username": "test",
                        "fullName": "Test 2"
                        }
                """;

        // user is updating user info
        mockMvc.perform(userAuthHelper.makeRequest(HttpMethod.POST, usersApiPrefix + "/updateUserInfo")
        .content(userAuthHelper.erisUserToString(erisUser)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()).andExpect(content().json(erisUserWithOutPassword));

    }

    @Test
    void getTasks() throws Exception {
        userAuthHelper.login(erisUser);
        // user is getting all tasks, should be empty since have not added task yet
        mockMvc.perform(userAuthHelper.makeRequest(HttpMethod.GET, usersApiPrefix + "/task/getAllTasks"))
                .andExpect(status().isOk()).andExpect(content().json("[]"));
    }

    /*
    testing to see if user with role USER can save a task which is only an ADMIN
    allowed action
     */
    @Test
    void saveTask() throws Exception {
        userAuthHelper.login(erisUser);
        // save task
        Task task = new Task("Route 6", "build new highway");
        mockMvc.perform(userAuthHelper.makeRequest(HttpMethod.POST, adminApiPrefix + "/task/saveTask")
        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isForbidden());
    }
}
