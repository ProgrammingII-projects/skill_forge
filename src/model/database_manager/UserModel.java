package model.database_manager;

import java.nio.file.*;
import java.util.*;

import model.User;

import java.io.*;
import org.json.JSONArray;

public class UserModel {
    private Path file;

    public UserModel(String filePath) {
        this.file = Paths.get(filePath);
        try {
            if (!Files.exists(file)) {
                Files.createDirectories(file.getParent());
                Files.write(file, "[]".getBytes());
            }
        } catch (IOException e) { throw new RuntimeException(e); }
    }

    public synchronized List<User> loadAll() {
        try {
            String s = new String(Files.readAllBytes(file));
            JSONArray arr = new JSONArray(s);
            List<User> list = new ArrayList<>();
            for (int i = 0; i < arr.length(); i++) list.add(User.fromJson(arr.getJSONObject(i)));
            return list;
        } catch (IOException e) { throw new RuntimeException(e); }
    }

    public synchronized void saveAll(List<User> users) {
        JSONArray arr = new JSONArray();
        for (User u : users) arr.put(u.toJson());
        try { Files.write(file, arr.toString(2).getBytes()); }
        catch (IOException e) { throw new RuntimeException(e); }
    }

    public synchronized void addUser(User u) {
        List<User> list = loadAll(); list.add(u); saveAll(list);
    }

    public synchronized Optional<User> findByEmail(String email) {
        return loadAll().stream().filter(u -> u.getEmail().equalsIgnoreCase(email)).findFirst();
    }

    public synchronized Optional<User> findById(String id) {
        return loadAll().stream().filter(u -> u.getUserId().equals(id)).findFirst();
    }

    public synchronized void updateUser(User u) {
        List<User> list = loadAll();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getUserId().equals(u.getUserId())) {
                list.set(i, u);
                saveAll(list);
                return;
            }
        }
        list.add(u);
        saveAll(list);
    }
}
