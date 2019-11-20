package app.security;

import org.springframework.security.core.Authentication;

/*
Get the user via an interface
* The facade exposes the Authentication object while hiding the static state and keeping the code decoupled and
fully testable.
 */
public interface IAuthenticationFacade {
    Authentication getAuthentication();
}
