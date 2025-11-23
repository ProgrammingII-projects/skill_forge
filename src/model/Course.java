
package model;

import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class Course {
    private String courseId;
    private String title;
    private String description;
    private String instructorId;
    private String approveStatus;
    private List<Lesson> lessons;
    private List<String> students;

    public Course(String courseId, String title, String description, String instructorId, String approveStatus) {
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.instructorId = instructorId;
        this.approveStatus = approveStatus;
        this.lessons = new ArrayList<>();
        this.students = new ArrayList<>();
    }

    public String getCourseId() {
        return courseId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getInstructorId() {
        return instructorId;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public List<String> getStudents() {
        return students;
    }

    public void setTitle(String t) {
        title = t;
    }

    public void setDescription(String d) {
        description = d;
    }
    public String getApproveStatus() {
        return approveStatus;
    }

    public void setApproveStatus(String s) {
        approveStatus = s;
    }

    public void addLesson(Lesson l) {
        lessons.add(l);
    }

    public void updateLesson(Lesson l) {
        for (int i = 0; i < lessons.size(); i++) {
            if (lessons.get(i).getLessonId().equals(l.getLessonId())) {
                lessons.set(i, l);
                return;
            }
        }
    }

    public void removeLesson(String lessonId) {
        lessons.removeIf(x -> x.getLessonId().equals(lessonId));
    }

    public void enrollStudent(String userId) {
        if (!students.contains(userId))
            students.add(userId);
    }

    public void unenrollStudent(String userId) {
        students.removeIf(x -> x.equals(userId));
    }

    public JSONObject toJson() {
        JSONObject o = new JSONObject();
        o.put("courseId", courseId);
        o.put("title", title);
        o.put("description", description);
        o.put("instructorId", instructorId);
        o.put("approveStatus", approveStatus);
        JSONArray la = new JSONArray();
        for (Lesson l : lessons)
            la.put(l.toJson());
        o.put("lessons", la);
        o.put("students", students);
        return o;
    }

    public static Course fromJson(JSONObject o) {
        Course c = new Course(o.getString("courseId"), o.getString("title"), o.getString("description"),
                o.getString("instructorId"), o.optString("approveStatus", "pending"));
        if (o.has("lessons")) {
            JSONArray la = o.getJSONArray("lessons");
            for (int i = 0; i < la.length(); i++)
                c.getLessons().add(Lesson.fromJson(la.getJSONObject(i)));
        }
        if (o.has("students")) {
            JSONArray sa = o.getJSONArray("students");
            for (int i = 0; i < sa.length(); i++)
                c.getStudents().add(sa.getString(i));
        }
        return c;
    }
}