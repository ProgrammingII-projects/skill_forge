package model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class QuizAttempt {
    private String attemptId;
    private String quizId;
    private String userId;
    private List<Integer> selectedIndices; // -1 for unanswered
    private double scorePercent;
    private boolean passed;
    private long timestamp;
    private int attemptNumber; // 1-based per-user attempt count for that quiz

    public QuizAttempt(String attemptId, String quizId, String userId, List<Integer> selectedIndices,
                       double scorePercent, boolean passed, long timestamp, int attemptNumber) {
        this.attemptId = attemptId;
        this.quizId = quizId;
        this.userId = userId;
        this.selectedIndices = new ArrayList<>(selectedIndices);
        this.scorePercent = scorePercent;
        this.passed = passed;
        this.timestamp = timestamp;
        this.attemptNumber = attemptNumber;
    }

    public String getAttemptId() { return attemptId; }
    public String getQuizId() { return quizId; }
    public String getUserId() { return userId; }
    public List<Integer> getSelectedIndices() { return selectedIndices; }
    public double getScorePercent() { return scorePercent; }
    public boolean isPassed() { return passed; }
    public long getTimestamp() { return timestamp; }
    public int getAttemptNumber() { return attemptNumber; }

    public JSONObject toJson() {
        JSONObject o = new JSONObject();
        o.put("attemptId", attemptId);
        o.put("quizId", quizId);
        o.put("userId", userId);
        o.put("selectedIndices", new JSONArray(selectedIndices));
        o.put("scorePercent", scorePercent);
        o.put("passed", passed);
        o.put("timestamp", timestamp);
        o.put("attemptNumber", attemptNumber);
        return o;
    }

    public static QuizAttempt fromJson(JSONObject o) {
        List<Integer> sel = new ArrayList<>();
        if (o.has("selectedIndices")) {
            JSONArray sa = o.getJSONArray("selectedIndices");
            for (int i = 0; i < sa.length(); i++) sel.add(sa.getInt(i));
        }
        return new QuizAttempt(
            o.getString("attemptId"),
            o.getString("quizId"),
            o.getString("userId"),
            sel,
            o.optDouble("scorePercent", 0.0),
            o.optBoolean("passed", false),
            o.optLong("timestamp", System.currentTimeMillis()),
            o.optInt("attemptNumber", 1)
        );
    }
}
