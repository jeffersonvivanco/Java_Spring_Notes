package app.controllers;

import app.model.ErisUser;
import app.services.ErisUserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class ErisUsersController {

    private final ErisUserService erisUserService;

    public ErisUsersController(ErisUserService erisUserService) {
        this.erisUserService = erisUserService;
    }

    @PostMapping("/sign-up")
    public void signUp(@RequestBody ErisUser erisUser){
        erisUserService.signUp(erisUser);
    }
}
