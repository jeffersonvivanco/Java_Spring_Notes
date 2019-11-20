package app.controllers;

import app.exceptions.ErisAppException;
import app.models.ErisUser;
import app.models.Status;
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
    public Status signUp(@RequestBody ErisUser erisUser) throws ErisAppException {
        return erisUserService.signUp(erisUser);
    }


    @GetMapping("/getUserInfo")
    public ErisUser getUserInfo() throws ErisAppException {
        return erisUserService.getUserInfo();
    }

    @PostMapping("/updateUserInfo")
    public Status updateUserInfo(@RequestBody ErisUser erisUser) throws ErisAppException {
        return erisUserService.updateUserDetails(erisUser);
    }
}
