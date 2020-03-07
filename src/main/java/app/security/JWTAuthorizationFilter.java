package app.security;

import app.security.constants.RolesAndPrivileges;
import app.security.constants.SecurityConstants;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private static Logger loggerz = LoggerFactory.getLogger(JWTAuthorizationFilter.class);

    public JWTAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader(SecurityConstants.HEADER_STRING.getValue());
        if (header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX.getValue())) {
            chain.doFilter(request, response);
            return;
        }
        UsernamePasswordAuthenticationToken authenticationToken = null;
        try {
            authenticationToken = getAuthenticationToken(request);
        } catch (JWTVerificationException e){
            loggerz.error("Invalid token error when accessing endpoint {}", request.getServletPath());
        }
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        chain.doFilter(request, response);
    }

    /*
    This method reads the JWT from the Authorization header, and then uses JWT to validate token. If everything is in
    place, we set the user in the SecurityContext and allow the request to move on.
     */
    private UsernamePasswordAuthenticationToken getAuthenticationToken(HttpServletRequest request){
        String token = request.getHeader(SecurityConstants.HEADER_STRING.getValue());
        if (token != null){
            // parse the token
            DecodedJWT verification = JWT.require(Algorithm.HMAC512(SecurityConstants.SECRET.getValue().getBytes()))
                    .build().verify(token.replace(SecurityConstants.TOKEN_PREFIX.getValue(), ""));
            String user = verification.getSubject();
            // getting authorities from token
            String authorities[] = verification.getClaim(RolesAndPrivileges.AUTHORITIES.getValue()).asString().split(",");
            final List<GrantedAuthority> grantedAuthorities = Arrays.stream(authorities).map(SimpleGrantedAuthority::new).collect(Collectors.toUnmodifiableList());
            if (user != null){
                return new UsernamePasswordAuthenticationToken(user, null, grantedAuthorities);
            }
        }
        return null;
    }

}
