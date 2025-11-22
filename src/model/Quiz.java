package model;

import java.util.List;

public class Quiz {
    private String id;
    private List<model.Question> questions;
    private int passingScore;

    public Quiz(String id, List<model.Question> questions, int passingScore) {
        this.id = id;
        this.questions = questions;
        this.passingScore = passingScore;
    }

    public String getId() { return id; }
    public List<model.Question> getQuestions() { return questions; }
    public int getPassingScore() { return passingScore; }
}

