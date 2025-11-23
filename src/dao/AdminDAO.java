package dao;

import java.util.List;

import model.User;

public  class AdminDAO extends UserDAO {
    public AdminDAO(String filePath) {
        super(filePath);
    }

    public synchronized void deleteUser(String userId) {
        List<User> users = loadAll();
        users.removeIf(u -> u.getUserId().equals(userId));
        saveAll(users);
    }
    
}

