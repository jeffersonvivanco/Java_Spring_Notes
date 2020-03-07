package app.controllers;

import app.models.ErisUser;
import app.services.ErisUserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ErisSignUpController {
    private final ErisUserService erisUserService;

    public ErisSignUpController(ErisUserService erisUserService) {
        this.erisUserService = erisUserService;
    }

    @PostMapping("/sign-up")
    public ErisUser signUp(@RequestBody ErisUser erisUser) {
        return erisUserService.signUp(erisUser);
    }
}
