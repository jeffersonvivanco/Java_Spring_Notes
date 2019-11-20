package app.security;

import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

/*
@EnableGlobalMethodSecurity - enables us to secure in the service layer per method
  * prePostEnabled - enables SpringSecurity pre/post annotations
  * securedEnabled - determines if the @Secured annotation should be enabled
  * jsr250Enabled - allows us to use the @RoleAllowed annotation
 */
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true
)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration { }
