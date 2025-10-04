package com.dominAItionBackend.controllers;
import com.dominAItionBackend.models.LoginRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class LoginController {

    // Example: normally you'd inject a UserService to check credentials
    // @Autowired
    // private UserService userService;

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        System.out.println(email + " " + password);
    }
}
