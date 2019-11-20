package app.services;

import app.exceptions.ErisAppException;
import app.models.ErisUser;
import app.models.entities.ErisUserEntity;
import app.models.Status;
import app.repositories.ErisUserRepository;
import app.security.IAuthenticationFacade;
import app.security.services.ErisUserSecurityService;
import app.utilities.ObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ErisUserService {

    private final ErisUserRepository erisUserRepository;
    private final ErisUserSecurityService erisUserSecurityService;
    private final IAuthenticationFacade authenticationFacade;

    private static Logger logger = LoggerFactory.getLogger(ErisTaskService.class);

    public ErisUserService(ErisUserRepository erisUserRepository, ErisUserSecurityService erisUserSecurityService, IAuthenticationFacade authenticationFacade) {
        this.erisUserRepository = erisUserRepository;
        this.erisUserSecurityService = erisUserSecurityService;
        this.authenticationFacade = authenticationFacade;
    }

    /*
    Sign up
     */
    public Status signUp(ErisUser erisUser) throws ErisAppException {
        validateSignUp(erisUser);
        ErisUserEntity userEntity = new ErisUserEntity();
        ObjectUtil.copyProperties(erisUser, userEntity);
        erisUserSecurityService.setupUser(userEntity);
        erisUserRepository.save(userEntity);
        logger.info("User {{}} signed up successfully!", userEntity.getUsername());
        return new Status(HttpStatus.OK.value(), String.format("User {%s} signed up successfully", erisUser.getUsername()));
    }


    /*
    Update user details
     */
    public Status updateUserDetails(ErisUser erisUser) throws ErisAppException {
        ErisUserEntity userEntity = erisUserRepository.findByUsername(authenticationFacade.getAuthentication().getName())
                .orElseThrow(() -> new ErisAppException(String.format("User {%s} not found, update failed", authenticationFacade.getAuthentication().getName())));
        // Updating certain fields only
        userEntity.setFullName(erisUser.getFullName());
        erisUserRepository.save(userEntity);
        logger.info("User {{}} updated successfully", userEntity.getUsername());
        return new Status(HttpStatus.OK.value(), String.format("User {%s} updated successfully", userEntity.getUsername()));
    }

    /*
    Retrieve user details
     */
    public ErisUser getUserInfo() throws ErisAppException {
        String userName = authenticationFacade.getAuthentication().getName();
        ErisUserEntity userEntity = erisUserRepository.findByUsername(userName)
                .orElseThrow(() -> new ErisAppException(String.format("Error fetching user data for user {%s}", userName)));
        // VERY IMPORTANT
        // setting certain values to null to not expose them
        ErisUser erisUser = new ErisUser();
        ObjectUtil.copyProperties(userEntity, erisUser);
        erisUser.setPassword(null);
        return erisUser;
    }

    /*
    Validate data functions
     */
    private void validateSignUp(ErisUser erisUser) throws ErisAppException {
        // Check that username not null
        // also that they are all at least 4 chars long
        // also that the username doesn't exist in db
        String username = Optional.ofNullable(erisUser.getUsername())
                .orElseThrow(() -> new ErisAppException("Username cannot be null"));
        if (username.length() < 4)
            throw new ErisAppException("Validation error while user was signing up, username has to be more than 4 characters");
        Optional<ErisUserEntity> check = erisUserRepository.findByUsername(username);
        if (check.isPresent() && check.get().getUsername().equals(username))
            throw new ErisAppException(String.format("Validation error while user was signing up, username {%s} already exists.", username));
    }
}

