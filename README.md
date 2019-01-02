# Java_Spring_Notes
Some notes about Java Spring

## Planet Eris


## Implementing JWT Authentication on Spring Boot APIs

### Securing RESTful APIs with JWTs

* *JSON Web Tokens*, aka JWTs, are tokens that are used to authenticate users on applications. This technology has gained
popularity over the past few years because it enables backends to accept requests simply by validating the contents of
these JWTs. That is, application that use JWTs no longer have to hold cookies or other session data about their users. This
characteristic facilitates scalability while keeping applications secure.

* During the authentication process, when a user successfully logs in using their credentials, a JSON Web Token is returned
and must be saved locally (typically in local storage). Whenever the user wants to access a protected route or resource (an endpoint),
the user agent must send the JWT, usually the `Authorization` header using the *Bearer schema*, along with the request.

* When a backend server receives a request with a JWT, the first thing to do is to validate the token. This consists of 
a series of steps, and if any of these fails then, the request must be rejected. The following list shows the validation
steps needed:
  * Check the JWT is well formed
  * Check the signature
  * Validate the standard claims
  * Check the Client permissions (scopes)