package app.security.services;

import app.exceptions.ErisAppException;
import app.models.entities.ErisUserEntity;
import app.security.models.Role;
import app.security.repositories.RoleRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

import static app.security.constants.RolesAndPrivileges.ROLE_ADMIN;
import static app.security.constants.RolesAndPrivileges.ROLE_USER;

@Service
public class ErisUserSecurityService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RoleRepository roleRepository;

    public ErisUserSecurityService(BCryptPasswordEncoder bCryptPasswordEncoder, RoleRepository roleRepository) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.roleRepository = roleRepository;
    }

    // setup privileges and roles in the system for the user
    // encrypt password before saving to db
    public void setupUser(ErisUserEntity erisUser) throws ErisAppException {
        setupUserPassword(erisUser);
        Role userRole = roleRepository.findByName(ROLE_USER.getValue())
                .orElseThrow(() -> new ErisAppException("Error fetching roles"));
        erisUser.setRoles(Collections.singleton(userRole));
    }

    // validates and encrypts user's password
    // this function is used both when a user signs up or when a user updates his/her password
    private void setupUserPassword(ErisUserEntity erisUser) throws ErisAppException {
        String password = Optional.ofNullable(erisUser.getPassword())
                .orElseThrow(() -> new ErisAppException(String.format("Password validation error while setting up password for user {%s}, password cannot be null", erisUser.getUsername())));
        if (password.length() < 4)
            throw new ErisAppException(String.format("Password validation error while setting up password for user {%s}, password length < 4 characters.", erisUser.getUsername()));

        erisUser.setPassword(bCryptPasswordEncoder.encode(erisUser.getPassword()));
    }

    public void setupAdmin(ErisUserEntity erisUser) throws ErisAppException {
        setupUserPassword(erisUser);
        Role adminRole = roleRepository.findByName(ROLE_ADMIN.getValue())
                .orElseThrow(() -> new ErisAppException("No role found"));
        erisUser.setRoles(Collections.singleton(adminRole));
    }
}
