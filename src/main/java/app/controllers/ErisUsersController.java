package app.controllers;

import app.models.ErisUser;
import app.services.ErisUserService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth/user")
public class ErisUsersController {

    private final ErisUserService erisUserService;

    public ErisUsersController(ErisUserService erisUserService) {
        this.erisUserService = erisUserService;
    }

    @GetMapping("/getUserInfo")
    public ErisUser getUserInfo() {
        return erisUserService.getUserInfo();
    }

    @PostMapping("/updateUserInfo")
    public ErisUser updateUserInfo(@RequestBody ErisUser erisUser) {
        return erisUserService.updateUserDetails(erisUser);
    }
}
