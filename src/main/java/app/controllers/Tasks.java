package app.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Tasks {

    @GetMapping("/tasks")
    String getTasks(){
        return "Hello World!";
    }
}
