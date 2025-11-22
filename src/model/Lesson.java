package model;

import org.json.JSONObject;
public class Lesson {
    private String lessonId;
    private String title;
    private String content;
   private model.Quiz quiz;

    public Lesson(String lessonId, String title, String content, model.Quiz quiz) {
        this.lessonId = lessonId;
        this.title = title;
        this.content = content;
        this.quiz = quiz;
    }

    public String getLessonId() { return lessonId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }

    public JSONObject toJson() {
        JSONObject o = new JSONObject();
        o.put("lessonId", lessonId);
        o.put("title", title);
        o.put("content", content);
        o.put("quiz",quiz);
        return o;
    }

    public static Lesson fromJson(JSONObject o) {
        return new Lesson(o.getString("lessonId"), o.getString("title"), o.getString("content"),o.getString("quiz"));
    }
}
