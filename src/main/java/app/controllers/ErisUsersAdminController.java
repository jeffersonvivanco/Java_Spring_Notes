package app.controllers;

import app.models.ErisUser;
import app.services.ErisUserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/admin/user")
public class ErisUsersAdminController {

    private final ErisUserService erisUserService;

    public ErisUsersAdminController(ErisUserService erisUserService) {
        this.erisUserService = erisUserService;
    }
}
