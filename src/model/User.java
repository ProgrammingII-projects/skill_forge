package model;

import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;
public class User {
    private String userId;
    private String username;
    private String email;
    private String passwordHash;
    private String role;
    private List<String> enrolledCourses;
    private Map<String, List<String>> progress;

    public User(String userId, String username, String email, String passwordHash, String role) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.enrolledCourses = new ArrayList<>();
        this.progress = new HashMap<>();
    }

    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getRole() { return role; }
    public List<String> getEnrolledCourses() { return enrolledCourses; }
    public Map<String, List<String>> getProgress() { return progress; }

    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }

    public void enrollCourse(String courseId) {
        if (!enrolledCourses.contains(courseId)) {
            enrolledCourses.add(courseId);
            progress.putIfAbsent(courseId, new ArrayList<>());
        }
    }

    public void markLessonCompleted(String courseId, String lessonId) {
        progress.putIfAbsent(courseId, new ArrayList<>());
        List<String> list = progress.get(courseId);
        if (!list.contains(lessonId)) list.add(lessonId);
    }

    public JSONObject toJson() {
        JSONObject o = new JSONObject();
        o.put("userId", userId);
        o.put("username", username);
        o.put("email", email);
        o.put("passwordHash", passwordHash);
        o.put("role", role);
        o.put("enrolledCourses", enrolledCourses);
        JSONObject prog = new JSONObject();
        for (Map.Entry<String, List<String>> e : progress.entrySet()) {
            prog.put(e.getKey(), new JSONArray(e.getValue()));
        }
        o.put("progress", prog);
        return o;
    }

    public static User fromJson(JSONObject o) {
        String userId = o.getString("userId");
        String username = o.getString("username");
        String email = o.getString("email");
        String passwordHash = o.getString("passwordHash");
        String role = o.getString("role");
        User u = new User(userId, username, email, passwordHash, role);
        if (o.has("enrolledCourses")) {
            JSONArray arr = o.getJSONArray("enrolledCourses");
            for (int i = 0; i < arr.length(); i++) u.getEnrolledCourses().add(arr.getString(i));
        }
        if (o.has("progress")) {
            JSONObject prog = o.getJSONObject("progress");
            for (String key : prog.keySet()) {
                JSONArray arr = prog.getJSONArray(key);
                List<String> li = new ArrayList<>();
                for (int i = 0; i < arr.length(); i++) li.add(arr.getString(i));
                u.getProgress().put(key, li);
            }
        }
        return u;
    }
}
