package app.security;

import app.models.entities.ErisUserEntity;
import app.repositories.ErisUserRepository;
import app.security.models.Privilege;
import app.security.models.Role;
import app.security.repositories.PrivilegeRepository;
import app.security.repositories.RoleRepository;
import app.security.services.ErisUserSecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static app.security.constants.RolesAndPrivileges.*;

@Configuration
public class PreStartSetupSecurity implements CommandLineRunner {

    private final ErisUserSecurityService erisUserSecurityService;
    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;
    private final ErisUserRepository erisUserRepository;

    private static Logger logger = LoggerFactory.getLogger(PreStartSetupSecurity.class);

    public PreStartSetupSecurity(ErisUserSecurityService securityService, RoleRepository roleRepo, PrivilegeRepository prevRep, ErisUserRepository userRepo) {
        this.erisUserSecurityService = securityService;
        this.roleRepository = roleRepo;
        this.privilegeRepository = prevRep;
        this.erisUserRepository = userRepo;
    }

    @Override
    public void run(String... args) throws Exception {
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
        Role userRole = new Role(ROLE_USER.getValue(), List.of(readPrivilege));

        roleRepository.save(adminRole);
        roleRepository.save(userRole);

        logger.info("Successfully added roles to roles repo.");

        // create admin
        ErisUserEntity adminUser = new ErisUserEntity();
        adminUser.setUsername("user1");
        adminUser.setPassword("password");
        erisUserSecurityService.setupAdmin(adminUser);
        erisUserRepository.save(adminUser);

        logger.info("Successfully saved admin {{}} to eris user repo", adminUser.getUsername());
    }
}
