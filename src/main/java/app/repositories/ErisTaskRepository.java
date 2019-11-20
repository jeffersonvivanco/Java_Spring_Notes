package app.repositories;

import app.models.entities.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ErisTaskRepository extends JpaRepository<TaskEntity, Long> {
    List<TaskEntity> findAllByAddedBy(String addedBy);
}
