package app.repositories;

import app.models.entities.ErisUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ErisUserRepository extends JpaRepository<ErisUserEntity, Long> {
    Optional<ErisUserEntity> findByUsername(String username);
}
