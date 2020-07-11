package app.security;

import app.exceptions.ErisAppException;
import app.models.ErisUser;
import app.models.entities.ErisUserEntity;
import app.security.models.Role;
import app.security.repositories.RoleRepository;
import app.services.ErisUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Optional;

import static app.security.constants.RolesAndPrivileges.ROLE_ADMIN;

@Configuration
@Order(2)
@Profile("dev")
public class PreStartupAdminCheck implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final ErisUserService erisUserService;
    private static Logger logger = LoggerFactory.getLogger(PreStartupAdminCheck.class);

    public PreStartupAdminCheck(RoleRepository roleRepository, ErisUserService erisUserService) {
        this.roleRepository = roleRepository;
        this.erisUserService = erisUserService;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Optional<Role> adminRole = roleRepository.findByName(ROLE_ADMIN.getValue());
        if (adminRole.isPresent()) {
            Collection<ErisUserEntity> erisUsers = adminRole.get().getErisUsers();
            if (erisUsers.isEmpty()) promptAdminSignUp();
        }
    }
    private void promptAdminSignUp() {
        System.out.println("No admin account found. Please create an admin account.");
        Console console;
        String email = "";
        String fullName = "";
        String password = "";
        // check if we access to console
        if ((console = System.console()) != null) {
            char passwd[] = null;
            email = console.readLine("What is your email?\n");
            fullName = console.readLine("What is your full name?\n");
            passwd = console.readPassword("Please create a password.\n");
            password = new String(passwd);
        } else {
            System.out.println("console is not available. WARNING password will be echoed");
            try (BufferedReader is = new BufferedReader(new InputStreamReader(System.in))) {
                String inputLine;
                System.out.println("What is your email?");

                email = is.readLine();

                System.out.println("What is your full name?");
                fullName = is.readLine();

                System.out.println("Please create a password");
                password = is.readLine();

                System.out.println("Thank you!");
            } catch (IOException e) {
                System.err.println("IOException: " + e);
            }
        }
        logger.info("creating admin {}", email);
        ErisUser erisUser = new ErisUser(email, password, fullName);
        try {
            erisUserService.signUpAdmin(erisUser);
        } catch (ErisAppException e) {
            logger.error("error creating admin eris user {}, shutting down ...", email);
            System.exit(0);
        }
    }
}
