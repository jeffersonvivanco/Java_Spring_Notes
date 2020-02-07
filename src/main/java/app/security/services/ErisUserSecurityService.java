package app.security.services;

import app.models.entities.ErisUserEntity;
import app.security.constants.RolesAndPrivileges;
import app.security.models.Role;
import app.security.repositories.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Optional;

import static app.security.constants.RolesAndPrivileges.ROLE_ADMIN;
import static app.security.constants.RolesAndPrivileges.ROLE_USER;

@Service
public class ErisUserSecurityService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RoleRepository roleRepository;
    private static Logger logger = LoggerFactory.getLogger(ErisUserSecurityService.class);

    public ErisUserSecurityService(BCryptPasswordEncoder bCryptPasswordEncoder, RoleRepository roleRepository) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.roleRepository = roleRepository;
    }

    // setup privileges and roles in the system for the user
    // encrypt password before saving to db
    public void setupUser(ErisUserEntity erisUser) {
        setupUserPassword(erisUser);
        Role userRole = this.getRole(ROLE_USER);
        erisUser.setRoles(Collections.singleton(userRole));
    }

    // validates and encrypts user's password
    // this function is used both when a user signs up or when a user updates his/her password
    private void setupUserPassword(ErisUserEntity erisUser) {
        if (erisUser.getPassword() == null || erisUser.getPassword().isEmpty()) {
            logger.error("password was null when setting up user");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "password cannot be null");
        }
        if (erisUser.getPassword().length() < 4) {
            logger.error("password was less than 4 characters long");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "password cannot be less than 4 characters long");
        }

        erisUser.setPassword(bCryptPasswordEncoder.encode(erisUser.getPassword()));
    }

    public void setupAdmin(ErisUserEntity erisUser) {
        setupUserPassword(erisUser);
        Role adminRole = this.getRole(ROLE_ADMIN);
        erisUser.setRoles(Collections.singleton(adminRole));
    }

    private Role getRole(RolesAndPrivileges role){
        Optional<Role> isThereRole = roleRepository.findByName(role.getValue());
        if (isThereRole.isEmpty()) {
            logger.error("failed to get {} role from db", role.getValue());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "unable to sign up user at this time");
        }
        return isThereRole.get();
    }
}
