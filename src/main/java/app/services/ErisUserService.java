package app.services;

import app.exceptions.ErisAppException;
import app.models.ErisUser;
import app.models.entities.ErisUserEntity;
import app.repositories.ErisUserRepository;
import app.security.IAuthenticationFacade;
import app.security.services.ErisUserSecurityService;
import app.utilities.ObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class ErisUserService {

    private final ErisUserRepository erisUserRepository;
    private final ErisUserSecurityService erisUserSecurityService;
    private final IAuthenticationFacade authenticationFacade;

    private static Logger logger = LoggerFactory.getLogger(ErisUserService.class);

    public ErisUserService(ErisUserRepository erisUserRepository, ErisUserSecurityService erisUserSecurityService, IAuthenticationFacade authenticationFacade) {
        this.erisUserRepository = erisUserRepository;
        this.erisUserSecurityService = erisUserSecurityService;
        this.authenticationFacade = authenticationFacade;
    }

    /*
    Sign up
     */
    public ErisUser signUp(ErisUser erisUser) {
        try {
            validateSignUp(erisUser);
        } catch (ErisAppException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        ErisUserEntity userEntity = new ErisUserEntity();
        ObjectUtil.copyProperties(erisUser, userEntity);
        erisUserSecurityService.setupUser(userEntity);
        erisUserRepository.save(userEntity);
        logger.info("User {} signed up successfully!", userEntity.getUsername());
        return erisUser;
    }

    public ErisUser signUpAdmin(ErisUser erisUser) throws ErisAppException {
        validateSignUp(erisUser);
        ErisUserEntity userEntity = new ErisUserEntity();
        ObjectUtil.copyProperties(erisUser, userEntity);
        erisUserSecurityService.setupAdmin(userEntity);
        erisUserRepository.save(userEntity);
        logger.info("User {} signed up successfully as admin!", userEntity.getUsername());
        return erisUser;
    }

    /*
    Update user details
     */
    public ErisUser updateUserDetails(ErisUser erisUser) {
        ErisUserEntity userEntity = this.getUserEntity();
        // Updating certain fields only
        userEntity.setFullName(erisUser.getFullName());
        erisUserRepository.save(userEntity);
        logger.info("User {} updated successfully", userEntity.getUsername());
        return getUserInfo();
    }

    /*
    Retrieve user details
     */
    public ErisUser getUserInfo() {
        ErisUserEntity userEntity = this.getUserEntity();
        ErisUser erisUser = new ErisUser();
        ObjectUtil.copyProperties(userEntity, erisUser);
        return erisUser;
    }

    private ErisUserEntity getUserEntity() {
        String userName = authenticationFacade.getAuthentication().getName();
        Optional<ErisUserEntity> isThereUser= erisUserRepository.findByUsername(userName);
        if (isThereUser.isEmpty()) {
            logger.error("User {} not found", userName);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not exist");
        }
        return isThereUser.get();
    }

    /*
    Validate data functions
     */
    private void validateSignUp(ErisUser erisUser) throws ErisAppException {
        // Check that username not null
        // also that they are all at least 4 chars long
        // also that the username doesn't exist in db
        if (erisUser.getUsername() == null || erisUser.getUsername().isEmpty()) {
            logger.error("username cannot be empty");
            throw new ErisAppException("username cannot be empty");
        }
        if (erisUser.getUsername().length() < 4) {
            logger.error("username is less than 4 characters");
            throw new ErisAppException("username has to be more than 4 characters");
        }
        Optional<ErisUserEntity> check = erisUserRepository.findByUsername(erisUser.getUsername());
        // check if user exists, if it does, maybe admin, check if questions match
        if (check.isPresent()) {
            logger.error("username {} already exists", erisUser.getUsername());
            throw new ErisAppException("username already exists");
        }
    }
}

