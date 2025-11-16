package model.database_manager;

import java.nio.file.*;
import java.util.*;
import java.io.*;
import org.json.JSONArray;
import model.Course;

public class CourseModel {
    private Path file;

    public CourseModel(String filePath) {
        this.file = Paths.get(filePath);
        try {
            if (!Files.exists(file)) {
                Files.createDirectories(file.getParent());
                Files.write(file, "[]".getBytes());
            }
        } catch (IOException e) { throw new RuntimeException(e); }
    }

    public synchronized List<Course> loadAll() {
        try {
            String s = new String(Files.readAllBytes(file));
            JSONArray arr = new JSONArray(s);
            List<Course> list = new ArrayList<>();
            for (int i = 0; i < arr.length(); i++) list.add(Course.fromJson(arr.getJSONObject(i)));
            return list;
        } catch (IOException e) { throw new RuntimeException(e); }
    }

    public synchronized void saveAll(List<Course> courses) {
        JSONArray arr = new JSONArray();
        for (Course c : courses) arr.put(c.toJson());
        try { Files.write(file, arr.toString(2).getBytes()); }
        catch (IOException e) { throw new RuntimeException(e); }
    }

    public synchronized void addCourse(Course c) {
        List<Course> list = loadAll(); list.add(c); saveAll(list);
    }

    public synchronized Optional<Course> findById(String id) {
        return loadAll().stream().filter(c -> c.getCourseId().equals(id)).findFirst();
    }

    public synchronized void updateCourse(Course c) {
        List<Course> list = loadAll();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getCourseId().equals(c.getCourseId())) {
                list.set(i, c);
                saveAll(list);
                return;
            }
        }
        list.add(c);
        saveAll(list);
    }

    public synchronized void deleteCourse(String id) {
        List<Course> list = loadAll();
        list.removeIf(c -> c.getCourseId().equals(id));
        saveAll(list);
    }
}
