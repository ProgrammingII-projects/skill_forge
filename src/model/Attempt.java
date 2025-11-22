package model;

public class Attempt {
    private String quizId;
    private int score;
    private int attempts;

    public Attempt(String quizId) {
        this.quizId = quizId;
        this.score = 0;
        this.attempts = 0;
    }

    public void addAttempt(int score) {
        this.attempts++;
        this.score = score;
    }

    public int getAttempts() { return attempts; }
    public int getScore() { return score; }
}

