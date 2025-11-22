package service;

import model.Question;
import model.Quiz;
import model.Attempt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Attempt;

public class QuizService {

    private Map<String,Attempt> attempts = new HashMap<>();

    public int gradeQuiz(Quiz quiz, List<Integer> userAnswers) {
        int score = 0;
        List<Question> questions = quiz.getQuestions();

        for (int i = 0; i < questions.size(); i++) {
            if (userAnswers.get(i) == questions.get(i).getCorrectIndex()) {
                score++;
            }
        }

        Attempt attempt = attempts.getOrDefault(quiz.getId(), new Attempt(quiz.getId()));
        attempt.addAttempt(score);
        attempts.put(quiz.getId(), attempt);

        return score;
    }

    public boolean hasPassed(Quiz quiz) {
        Attempt attempt = attempts.get(quiz.getId());
        return attempt != null && attempt.getScore() >= quiz.getPassingScore();
    }

    public int getAttempts(String quizId) {
        Attempt attempt = attempts.get(quizId);
        return attempt == null ? 0 : attempt.getAttempts();
    }
}
