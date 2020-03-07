package app.controllers;

import app.models.Task;
import app.services.ErisTaskService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/admin/task")
public class ErisTasksAdminController {
    private final ErisTaskService erisTaskService;

    public ErisTasksAdminController(ErisTaskService erisTaskService) {
        this.erisTaskService = erisTaskService;
    }

    @PostMapping("/saveTask")
    public Task saveTask(@RequestBody Task task){
        return erisTaskService.saveTask(task);
    }
}
