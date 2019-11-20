package app.services;

import app.models.Status;
import app.models.Task;
import app.models.entities.TaskEntity;
import app.repositories.ErisTaskRepository;
import app.security.IAuthenticationFacade;
import app.utilities.ObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ErisTaskService {

    private final ErisTaskRepository erisTaskRepository;
    private final IAuthenticationFacade authenticationFacade;
    private static Logger logger = LoggerFactory.getLogger(ErisTaskService.class);

    public ErisTaskService(ErisTaskRepository erisTaskRepository, IAuthenticationFacade authenticationFacade) {
        this.erisTaskRepository = erisTaskRepository;
        this.authenticationFacade = authenticationFacade;
    }

    public Status saveTask(Task task){
        String userName = authenticationFacade.getAuthentication().getName();
        String taskName = task.getName();
        logger.info("User {{}} adding task {{}}", userName, taskName);
        task.setAddedBy(authenticationFacade.getAuthentication().getName());
        TaskEntity taskEntity = new TaskEntity();
        ObjectUtil.copyProperties(task, taskEntity);
        erisTaskRepository.save(taskEntity);
        return new Status(HttpStatus.OK.value(), "Successfully saved task.");
    }

    public List<Task> getTasks(){
        String userName = authenticationFacade.getAuthentication().getName();
        logger.info("Getting tasks for user {{}}", userName);
        List<Task> tasks = new ArrayList<>();
        List<TaskEntity> taskEntities = erisTaskRepository.findAllByAddedBy(authenticationFacade.getAuthentication().getName());
        if (!taskEntities.isEmpty()){
            for (TaskEntity entity: taskEntities){
                Task newTask = new Task();
                ObjectUtil.copyProperties(entity, newTask);
                tasks.add(newTask);
            }
            return tasks;
        } else {
            return Collections.emptyList();
        }
    }
}
