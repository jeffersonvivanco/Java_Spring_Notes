package app.controllers;

import app.models.Status;
import app.models.Task;
import app.services.ErisTaskService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/secure/tasks")
public class ErisTasksController {

    private final ErisTaskService erisTaskService;

    public ErisTasksController(ErisTaskService erisTaskService) {
        this.erisTaskService = erisTaskService;
    }

    @GetMapping("/getAllTasks")
    List<Task> getTasks(){
        return erisTaskService.getTasks();
    }

    @PostMapping("/saveTask")
    public Status saveTask(@RequestBody Task task){
        return erisTaskService.saveTask(task);
    }
}