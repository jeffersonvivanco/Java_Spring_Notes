package app;

import app.exceptions.ErisAppRuntimeException;
import app.models.ErisUser;
import app.security.constants.SecurityConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ErisAppAuthHelper {

    private String token;
    private MockMvc mockMvc;
    private ObjectMapper mapper;

    public ErisAppAuthHelper(MockMvc mockMvc, ObjectMapper mapper) {
        this.mockMvc = mockMvc;
        this.mapper = mapper;
    }

    public void signUp(ErisUser erisUser) throws Exception {
        mockMvc.perform(post("/api/sign-up")
                .content(erisUserToString(erisUser))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    public void login(ErisUser erisUser) throws Exception {
        // user is signing in
        MvcResult result = mockMvc.perform(post("/login")
                .content(erisUserToString(erisUser)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(header().exists(SecurityConstants.HEADER_STRING.getValue()))
                .andReturn();

        // user signed in, retrieved token to make secure calls
        this.token = result.getResponse().getHeader(SecurityConstants.HEADER_STRING.getValue());
    }

    public MockHttpServletRequestBuilder makeRequest(HttpMethod method, String url, Object... uriVars) {
        if (token == null || token.isEmpty())
            throw new ErisAppRuntimeException("token is null or empty");
        return MockMvcRequestBuilders.request(method, url, uriVars).header(SecurityConstants.HEADER_STRING.getValue(), token);
    }

    /*
    returns a map repr of ErisUser, this is so object mapper includes all fields
    including password when converting object to string
     */
    public String erisUserToString(ErisUser erisUser) throws Exception {
        return mapper.writeValueAsString(Map.of(
                "username", erisUser.getUsername(),
                "password", erisUser.getPassword(),
                "fullName", erisUser.getFullName()
        ));
    }


}
