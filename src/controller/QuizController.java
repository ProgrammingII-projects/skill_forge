package controller;

import model.Quiz;
import model.Question;
import model.QuizAttempt;
import service.QuizService;
import java.util.List;

/**
 * Controller for Quiz operations (Presentation Layer)
 * Acts as a bridge between View and Service layers
 */
public class QuizController {
    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    public Quiz createQuiz(String title, double passingPercent, int maxRetries) {
        return quizService.createQuiz(title, passingPercent, maxRetries);
    }

    public boolean addQuestion(String quizId, Question question) {
        return quizService.addQuestion(quizId, question);
    }

    public boolean removeQuestion(String quizId, String questionId) {
        return quizService.removeQuestion(quizId, questionId);
    }

    public Quiz getQuiz(String quizId) {
        return quizService.getQuiz(quizId);
    }

    public boolean updateQuiz(Quiz quiz) {
        return quizService.updateQuiz(quiz);
    }

    public void setQuizForLesson(String courseId, String lessonId, Quiz quiz) throws Exception {
        quizService.setQuizForLesson(courseId, lessonId, quiz);
    }

    public Quiz getQuizForLesson(String courseId, String lessonId) throws Exception {
        return quizService.getQuizForLesson(courseId, lessonId);
    }

    public void removeQuizFromLesson(String courseId, String lessonId) throws Exception {
        quizService.removeQuizFromLesson(courseId, lessonId);
    }

    public QuizAttempt submitQuizAttempt(String courseId, String lessonId, String userId, 
                                         List<Integer> selectedIndices) throws Exception {
        return quizService.submitQuizAttempt(courseId, lessonId, userId, selectedIndices);
    }

    public List<QuizAttempt> getUserQuizAttempts(String courseId, String lessonId, String userId) throws Exception {
        return quizService.getUserQuizAttempts(courseId, lessonId, userId);
    }

    public List<QuizAttempt> getAllQuizAttempts(String courseId, String lessonId) throws Exception {
        return quizService.getAllQuizAttempts(courseId, lessonId);
    }

    public boolean canRetakeQuiz(String courseId, String lessonId, String userId) throws Exception {
        return quizService.canRetakeQuiz(courseId, lessonId, userId);
    }
}
