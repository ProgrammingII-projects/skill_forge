package model;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class Quiz {
    private String quizId;
    private String title;
    private List<Question> questions;
    private double passScorePercent; // default 60.0
    private int maxRetries; // -1 => unlimited

    public Quiz(String quizId, String title, List<Question> questions, double passScorePercent, int maxRetries) {
        this.quizId = quizId;
        this.title = title;
        this.questions = new ArrayList<>(questions);
        this.passScorePercent = passScorePercent;
        this.maxRetries = maxRetries;
    }

    public String getQuizId() { return quizId; }
    public String getTitle() { return title; }
    public List<Question> getQuestions() { return questions; }
    public double getPassScorePercent() { return passScorePercent; }
    public int getMaxRetries() { return maxRetries; }

    public JSONObject toJson() {
        JSONObject o = new JSONObject();
        o.put("quizId", quizId);
        o.put("title", title);
        JSONArray qa = new JSONArray();
        for (Question q : questions) qa.put(q.toJson());
        o.put("questions", qa);
        o.put("passScorePercent", passScorePercent);
        o.put("maxRetries", maxRetries);
        return o;
    }

    public static Quiz fromJson(JSONObject o) {
        List<Question> qs = new ArrayList<>();
        if (o.has("questions")) {
            JSONArray qa = o.getJSONArray("questions");
            for (int i = 0; i < qa.length(); i++) qs.add(Question.fromJson(qa.getJSONObject(i)));
        }
        double pass = o.optDouble("passScorePercent", 60.0);
        int retries = o.optInt("maxRetries", -1);
        return new Quiz(o.getString("quizId"), o.optString("title", ""), qs, pass, retries);
    }
}
