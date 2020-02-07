package app.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private static Logger logger = LoggerFactory.getLogger(RestAccessDeniedHandler.class);
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        String username = request.getUserPrincipal().getName();
        logger.error("User {} received 403 FORBIDDEN while attempting to access endpoint {}", username, request.getServletPath());
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");
        OutputStream output = response.getOutputStream();
        objectMapper.writeValue(output, "Access denied!");
        output.flush();
    }
}
