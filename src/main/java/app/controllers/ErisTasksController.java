package app.controllers;

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
    public List<Task> getTasks(){
        return erisTaskService.getTasks();
    }

    @PostMapping("/saveTask")
    public Task saveTask(@RequestBody Task task){
        return erisTaskService.saveTask(task);
    }
}