package model;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Lesson {
    private String lessonId;
    private String title;
    private String content;

    private List<Quiz> quizzes;

    private List<QuizAttempt> attempts;

    private List<String> completedStudents;

    public Lesson(String lessonId, String title, String content) {
        this.lessonId = lessonId;
        this.title = title;
        this.content = content;

    }

    public String getLessonId() {
        return lessonId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public List<Quiz> getQuizzes() {
        return quizzes;
    }

    public List<QuizAttempt> getAttempts() {
        return attempts;
    }

    public List<String> getCompletedStudents() {
        return completedStudents;
    }

    public void addQuiz(Quiz q) {
        quizzes.add(q);
    }

    public void addAttempt(QuizAttempt a) {
        attempts.add(a);
    }

    public void markStudentCompleted(String userId) {
        if (!completedStudents.contains(userId))
            completedStudents.add(userId);
    }

    public JSONObject toJson() {
        JSONObject o = new JSONObject();
        o.put("lessonId", lessonId);
        o.put("title", title);
        o.put("content", content);
        JSONArray qarr = new JSONArray();
        for (Quiz q : quizzes)
            qarr.put(q.toJson());
        o.put("quizzes", qarr);

        JSONArray aarr = new JSONArray();
        for (QuizAttempt a : attempts)
            aarr.put(a.toJson());
        o.put("attempts", aarr);

        o.put("completedStudents", new JSONArray(completedStudents));
        return o;
    }

    public static Lesson fromJson(JSONObject o) {
        Lesson l = new Lesson(o.getString("lessonId"), o.optString("title", ""), o.optString("content", ""));
        if (o.has("quizzes")) {
            JSONArray qa = o.getJSONArray("quizzes");
            for (int i = 0; i < qa.length(); i++)
                l.getQuizzes().add(Quiz.fromJson(qa.getJSONObject(i)));
        }
        if (o.has("attempts")) {
            JSONArray aa = o.getJSONArray("attempts");
            for (int i = 0; i < aa.length(); i++)
                l.getAttempts().add(QuizAttempt.fromJson(aa.getJSONObject(i)));
        }
        if (o.has("completedStudents")) {
            JSONArray ca = o.getJSONArray("completedStudents");
            for (int i = 0; i < ca.length(); i++)
                l.getCompletedStudents().add(ca.getString(i));
        }
        return l;
    }
}
