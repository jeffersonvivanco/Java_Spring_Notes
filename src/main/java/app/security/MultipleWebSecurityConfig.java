package app.security;

import app.security.constants.RolesAndPrivileges;
import app.security.constants.SecurityConstants;
import app.security.services.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
public class MultipleWebSecurityConfig {

    // Authenticate some endpoints, most strict
    @Configuration
    @Order(1)
    public static class RoleWebSecurityConfig extends WebSecurityConfigurerAdapter {

        private final UserDetailsServiceImpl userDetailsService;
        private final BCryptPasswordEncoder bCryptPasswordEncoder;

        public RoleWebSecurityConfig(UserDetailsServiceImpl userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder) {
            this.userDetailsService = userDetailsService;
            this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        }

        /*
        Here we can define which resources are public and which are secured.
         */
        @Override
        protected void configure(HttpSecurity http) throws Exception {

            http.cors().and().csrf().disable().antMatcher("/api/auth/**")
                    .authorizeRequests()
                    // request only for admin
                    .antMatchers("/api/auth/admin/**").hasAuthority(RolesAndPrivileges.WRITE_PRIVILEGE.getValue())
                    .antMatchers("/api/auth/user/**").hasAuthority(RolesAndPrivileges.READ_PRIVILEGE.getValue())
                    .and()
                    .exceptionHandling().accessDeniedHandler(new RestAccessDeniedHandler()).and()
                    .addFilter(new JWTAuthenticationFilter(authenticationManager()))
                    .addFilter(new JWTAuthorizationFilter(authenticationManager()))
                    // this disables session creation on spring security
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        }

        /*
        Here we defined a custom implementation of UserDetailsService to load user specific data in the security framework.
        We also have used this method to set the encrypt method used by our application (BCryptPasswordEncoder).
         */
        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
        }
    }

    // make add filter for login endpoint /login
    @Configuration
    @Order(2)
    public static class OtherWebSecurity extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.cors().and().csrf().disable()
                    .authorizeRequests().antMatchers("/login").authenticated()
                    .and()
                    .addFilter(new JWTAuthenticationFilter(authenticationManager()))
                    .addFilter(new JWTAuthorizationFilter(authenticationManager()))
                    .authorizeRequests().antMatchers("/api/sign-up").permitAll()
                    .and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        }
    }

    /*
    Here we can allow/restrict our CORS support.
    */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addExposedHeader(SecurityConstants.HEADER_STRING.getValue());
        // we permit request from any source /**
        source.registerCorsConfiguration("/**", corsConfiguration.applyPermitDefaultValues());
        return source;
    }
}
