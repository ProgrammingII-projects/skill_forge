package service;

import dao.QuizDAO;
import model.Quiz;
import model.Question;

import java.util.List;
import java.util.UUID;

public class QuizService {

    private final QuizDAO dao;

    public QuizService(QuizDAO dao) {
        this.dao = dao;
    }


    public Quiz createQuiz(String title, double passingPercent, int maxRetries) {
        String id = UUID.randomUUID().toString();
        Quiz quiz = new Quiz(id, title, passingPercent, maxRetries);
        dao.saveQuiz(quiz);
        return quiz;
    }

    public boolean addQuestion(String quizId, Question q) {
        Quiz quiz = dao.getQuiz(quizId);
        if (quiz == null) return false;

        quiz.getQuestions().add(q);
        dao.saveQuiz(quiz);
        return true;
    }

    public Quiz getQuiz(String quizId) {
        return dao.getQuiz(quizId);
    }

    public List<Quiz> getCourseQuizzes(String courseId) {
        return dao.getQuizzesByCourse(courseId);
    }

    public boolean deleteQuiz(String quizId) {
        return dao.deleteQuiz(quizId);
    }

    public boolean updateQuiz(Quiz updated) {
        Quiz existing = dao.getQuiz(updated.getQuizId());
        if (existing == null) return false;

        dao.saveQuiz(updated);
        return true;
    }
}
