package app.security;

import app.models.ErisUser;
import app.security.constants.SecurityConstants;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

import static app.security.constants.RolesAndPrivileges.AUTHORITIES;

/*
When we add a new filter to Spring Security, we can explicitly define where in the filter chain we want that filter,
or we can let the framework figure it out by itself. By extending the filter provided within the security framework, Spring
can automatically identify the best place to put it in the security chain.

This filter also registers itself as the one responsible for the "/login" endpoint. As such, whenever your backend API gets
a request to "/login", your specialization of this filter goes into action and handles the authentication attempt (through
the attemptAuthentication method).
 */

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private static Logger loggerz = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    /*
    We parse the user's credentials and issue them to the AuthenticationManager
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response){
        try {
            ErisUser creds = new ObjectMapper().readValue(request.getInputStream(), ErisUser.class);
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.getUsername(), creds.getPassword(), new ArrayList<>()
                    )
            );
        } catch (AuthenticationException e){
            loggerz.error("Authentication error: {{}}", e.getMessage());
            throw e;
        } catch (IOException e2){
            loggerz.error("Authentication error (IOException): {{}}", e2.getMessage());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "authentication error");
        }
    }

    /*
    This method is called when a user successfully logs in. It generates a JWT for the user.
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult){
        String authorities = authResult.getAuthorities().parallelStream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
        String token = JWT.create()
                .withSubject(((User) authResult.getPrincipal()).getUsername())
                .withClaim(AUTHORITIES.getValue(), authorities)
                .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME.getLongValue()))
                .sign(Algorithm.HMAC512(SecurityConstants.SECRET.getValue().getBytes()));
        loggerz.info("User {{}} successfully logged in!", ((User) authResult.getPrincipal()).getUsername());
        response.addHeader(SecurityConstants.HEADER_STRING.getValue(), SecurityConstants.TOKEN_PREFIX.getValue() + token);
    }


}
