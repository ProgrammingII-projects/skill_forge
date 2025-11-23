package controller;

import model.User;
import service.AuthService;

/**
 * Controller for Authentication (Presentation Layer)
 * Acts as a bridge between View and Service layers
 */
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public User signup(String username, String email, String password, String role) throws Exception {
        return authService.signup(username, email, password, role);
    }

    public User login(String email, String password) throws Exception {
        return authService.login(email, password);
    }
}
