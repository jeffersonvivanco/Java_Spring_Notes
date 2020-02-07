package app.controllers;

import app.models.ErisUser;
import app.services.ErisUserService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
public class ErisUsersController {

    private final ErisUserService erisUserService;

    public ErisUsersController(ErisUserService erisUserService) {
        this.erisUserService = erisUserService;
    }

    @PostMapping("/sign-up")
    public ErisUser signUp(@RequestBody ErisUser erisUser) {
        return erisUserService.signUp(erisUser);
    }
}
