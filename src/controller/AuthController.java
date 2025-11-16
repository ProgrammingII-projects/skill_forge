package controller;

import model.*;
import model.database_manager.UserModel;
import utils.HashUtil;
import java.util.*;
import java.util.UUID;

public class AuthController {
    private final UserModel userModel;

    public AuthController(UserModel userModel) {
        this.userModel = userModel;
    }

    public User signup(String username, String email, String password, String role) throws Exception {
        if (username == null || username.isEmpty() ||
            email == null || email.isEmpty() ||
            password == null || password.isEmpty() ||
            role == null || role.isEmpty()) {
            throw new Exception("Missing required fields");
        }
        if (!email.contains("@")) throw new Exception("Invalid email");
        if (userModel.findByEmail(email).isPresent()) throw new Exception("Email already registered");

        String id = UUID.randomUUID().toString();
        String hash = HashUtil.sha256(password);
        User u = new User(id, username, email, hash, role.toLowerCase());
        userModel.addUser(u);
        return u;
    }

    public User login(String email, String password) throws Exception {
        Optional<User> opt = userModel.findByEmail(email);
        if (!opt.isPresent()) throw new Exception("Invalid credentials");
        User u = opt.get();
        String hash = HashUtil.sha256(password);
        if (!u.getPasswordHash().equals(hash)) throw new Exception("Invalid credentials");
        return u;
    }
}
