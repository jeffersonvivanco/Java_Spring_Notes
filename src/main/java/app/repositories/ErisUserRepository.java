package app.repositories;

import app.model.ErisUser;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ErisUserRepository extends JpaRepository<ErisUser, Long> {
    ErisUser findByUsername(String username);
}
