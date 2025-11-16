package model;

import org.json.JSONObject;
public class Lesson {
    private String lessonId;
    private String title;
    private String content;

    public Lesson(String lessonId, String title, String content) {
        this.lessonId = lessonId;
        this.title = title;
        this.content = content;
    }

    public String getLessonId() { return lessonId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }

    public JSONObject toJson() {
        JSONObject o = new JSONObject();
        o.put("lessonId", lessonId);
        o.put("title", title);
        o.put("content", content);
        return o;
    }

    public static Lesson fromJson(JSONObject o) {
        return new Lesson(o.getString("lessonId"), o.getString("title"), o.getString("content"));
    }
}
