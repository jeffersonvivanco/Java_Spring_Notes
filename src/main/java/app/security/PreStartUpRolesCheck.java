package app.security;

import app.security.models.Privilege;
import app.security.models.Role;
import app.security.repositories.PrivilegeRepository;
import app.security.repositories.RoleRepository;
import app.services.ErisUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import static app.security.constants.RolesAndPrivileges.*;

@Configuration
@Order(1)
public class PreStartUpRolesCheck implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;
    private final ErisUserService erisUserService;

    private static Logger logger = LoggerFactory.getLogger(PreStartUpRolesCheck.class);

    public PreStartUpRolesCheck(RoleRepository roleRepo, PrivilegeRepository prevRep, ErisUserService erisUserService) {
        this.roleRepository = roleRepo;
        this.privilegeRepository = prevRep;
        this.erisUserService = erisUserService;
    }

    @Override
    @Transactional
    public void run(String... args) {
        checkForRolesAndPrivileges();
    }
    private void checkForRolesAndPrivileges() {
        logger.info("checking if roles and privileges already exist");
        if (roleRepository.findAll().isEmpty()) {
            // setup privileges and adding them to roles
            // adding roles and privileges to appropriate repositories since we are using an in-memory db
            // note: if not using in-memory db might not need to do this

            Privilege readPrivilege = new Privilege(READ_PRIVILEGE.getValue());
            Privilege writePrivilege = new Privilege(WRITE_PRIVILEGE.getValue());
            privilegeRepository.save(readPrivilege);
            privilegeRepository.save(writePrivilege);

            logger.info("Successfully added privileges to privileges repo.");

            List<Privilege> adminPrivileges = List.of(readPrivilege, writePrivilege);
            Role adminRole = new Role(ROLE_ADMIN.getValue(), adminPrivileges);
            adminRole.setErisUsers(List.of());
            Role userRole = new Role(ROLE_USER.getValue(), List.of(readPrivilege));
            userRole.setErisUsers(List.of());

            roleRepository.save(adminRole);
            roleRepository.save(userRole);

            logger.info("Successfully added roles to roles repo.");
        } else {
            logger.info("roles already exist, skipping setup ...");
        }
    }
}
