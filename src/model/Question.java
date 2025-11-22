package model;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class Question {
    private String questionId;
    private String text;
    private List<String> options;
    private String correctAnswer; // stored as the correct option string (matches JSON)

    public Question(String questionId, String text, List<String> options, String correctAnswer) {
        this.questionId = questionId;
        this.text = text;
        this.options = new ArrayList<>(options);
        this.correctAnswer = correctAnswer;
    }

    public String getQuestionId() { return questionId; }
    public String getText() { return text; }
    public List<String> getOptions() { return options; }
    public String getCorrectAnswer() { return correctAnswer; }

    public JSONObject toJson() {
        JSONObject o = new JSONObject();
        o.put("questionId", questionId);
        o.put("text", text);
        o.put("options", new JSONArray(options));
        o.put("correctAnswer", correctAnswer);
        return o;
    }

    public static Question fromJson(JSONObject o) {
        List<String> opts = new ArrayList<>();
        if (o.has("options")) {
            JSONArray a = o.getJSONArray("options");
            for (int i = 0; i < a.length(); i++) opts.add(a.getString(i));
        }
        String correct = o.optString("correctAnswer", opts.isEmpty() ? "" : opts.get(0));
        return new Question(o.getString("questionId"), o.optString("text", ""), opts, correct);
    }
}
