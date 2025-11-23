package service;

import dao.QuizDAO;
import dao.CourseDAO;
import model.Quiz;
import model.Question;
import model.QuizAttempt;
import model.Lesson;
import model.Course;

import java.util.*;
import java.util.stream.Collectors;

public class QuizService {

    private final QuizDAO quizDAO;
    private final CourseDAO courseDAO;

    public QuizService(QuizDAO quizDAO, CourseDAO courseDAO) {
        this.quizDAO = quizDAO;
        this.courseDAO = courseDAO;
    }

    public Quiz createQuiz(String title, double passingPercent, int maxRetries) {
        String id = UUID.randomUUID().toString();
        Quiz quiz = new Quiz(id, title, passingPercent, maxRetries);
        quizDAO.saveQuiz(quiz);
        return quiz;
    }

    public boolean addQuestion(String quizId, Question q) {
        Quiz quiz = quizDAO.getQuiz(quizId);
        if (quiz == null)
            return false;

        quiz.getQuestions().add(q);
        quizDAO.saveQuiz(quiz);
        return true;
    }

    public boolean removeQuestion(String quizId, String questionId) {
        Quiz quiz = quizDAO.getQuiz(quizId);
        if (quiz == null)
            return false;

        quiz.removeQuestion(questionId);
        quizDAO.saveQuiz(quiz);
        return true;
    }

    public Quiz getQuiz(String quizId) {
        return quizDAO.getQuiz(quizId);
    }

    public boolean updateQuiz(Quiz updated) {
        Quiz existing = quizDAO.getQuiz(updated.getQuizId());
        if (existing == null)
            return false;

        quizDAO.saveQuiz(updated);
        return true;
    }

    /**
     * Add or set the quiz for a lesson (only one quiz per lesson)
     */
    public void setQuizForLesson(String courseId, String lessonId, Quiz quiz) throws Exception {
        Optional<Course> courseOpt = courseDAO.findById(courseId);
        if (!courseOpt.isPresent()) {
            throw new Exception("Course not found");
        }

        Course course = courseOpt.get();
        Optional<Lesson> lessonOpt = course.getLessons().stream()
                .filter(l -> l.getLessonId().equals(lessonId))
                .findFirst();

        if (!lessonOpt.isPresent()) {
            throw new Exception("Lesson not found");
        }

        Lesson lesson = lessonOpt.get();
        // Clear existing quizzes and add the new one (only one quiz per lesson)
        lesson.getQuizzes().clear();
        lesson.addQuiz(quiz);
        courseDAO.updateCourse(course);
    }

    /**
     * Get the quiz for a lesson (only one quiz per lesson)
     */
    public Quiz getQuizForLesson(String courseId, String lessonId) throws Exception {
        Optional<Course> courseOpt = courseDAO.findById(courseId);
        if (!courseOpt.isPresent()) {
            throw new Exception("Course not found");
        }

        Course course = courseOpt.get();
        Optional<Lesson> lessonOpt = course.getLessons().stream()
                .filter(l -> l.getLessonId().equals(lessonId))
                .findFirst();

        if (!lessonOpt.isPresent()) {
            throw new Exception("Lesson not found");
        }

        Lesson lesson = lessonOpt.get();
        List<Quiz> quizzes = lesson.getQuizzes();
        if (quizzes.isEmpty()) {
            return null; // No quiz for this lesson
        }
        return quizzes.get(0); // Return the first (and only) quiz
    }

    /**
     * Remove quiz from a lesson
     */
    public void removeQuizFromLesson(String courseId, String lessonId) throws Exception {
        Optional<Course> courseOpt = courseDAO.findById(courseId);
        if (!courseOpt.isPresent()) {
            throw new Exception("Course not found");
        }

        Course course = courseOpt.get();
        Optional<Lesson> lessonOpt = course.getLessons().stream()
                .filter(l -> l.getLessonId().equals(lessonId))
                .findFirst();

        if (!lessonOpt.isPresent()) {
            throw new Exception("Lesson not found");
        }

        Lesson lesson = lessonOpt.get();
        lesson.getQuizzes().clear();
        courseDAO.updateCourse(course);
    }

    /**
     * Calculate score for a quiz attempt
     */
    private double calculateScore(Quiz quiz, List<Integer> selectedIndices) {
        if (quiz == null || quiz.getQuestions().isEmpty()) {
            return 0.0;
        }

        int correct = 0;
        List<Question> questions = quiz.getQuestions();

        for (int i = 0; i < questions.size(); i++) {
            if (i >= selectedIndices.size()) {
                continue; // Unanswered question
            }

            int selectedIndex = selectedIndices.get(i);
            if (selectedIndex < 0 || selectedIndex >= questions.get(i).getOptions().size()) {
                continue; // Invalid selection
            }

            String selectedAnswer = questions.get(i).getOptions().get(selectedIndex);
            if (selectedAnswer.equals(questions.get(i).getCorrectAnswer())) {
                correct++;
            }
        }

        return (double) correct / questions.size() * 100.0;
    }

    /**
     * Get the next attempt number for a user and quiz
     */
    private int getNextAttemptNumber(String courseId, String lessonId, String userId) throws Exception {
        Optional<Course> courseOpt = courseDAO.findById(courseId);
        if (!courseOpt.isPresent()) {
            throw new Exception("Course not found");
        }

        Course course = courseOpt.get();
        Optional<Lesson> lessonOpt = course.getLessons().stream()
                .filter(l -> l.getLessonId().equals(lessonId))
                .findFirst();

        if (!lessonOpt.isPresent()) {
            throw new Exception("Lesson not found");
        }

        Lesson lesson = lessonOpt.get();
        Quiz quiz = getQuizForLesson(courseId, lessonId);
        if (quiz == null) {
            return 1; // First attempt if no quiz exists yet
        }

        List<QuizAttempt> userAttempts = lesson.getAttempts().stream()
                .filter(a -> a.getQuizId().equals(quiz.getQuizId()) && a.getUserId().equals(userId))
                .collect(Collectors.toList());

        return userAttempts.size() + 1;
    }

    /**
     * Submit a quiz attempt
     */
    public QuizAttempt submitQuizAttempt(String courseId, String lessonId, String userId,
            List<Integer> selectedIndices) throws Exception {
        // Get the quiz for the lesson
        Quiz quiz = getQuizForLesson(courseId, lessonId);
        if (quiz == null) {
            throw new Exception("No quiz found for this lesson");
        }

        // Check retry limit
        int currentAttemptNumber = getNextAttemptNumber(courseId, lessonId, userId);
        if (quiz.getMaxRetries() > 0 && currentAttemptNumber > quiz.getMaxRetries()) {
            throw new Exception("Maximum retry limit reached for this quiz");
        }

        // Calculate score
        double scorePercent = calculateScore(quiz, selectedIndices);
        boolean passed = scorePercent >= quiz.getPassScorePercent();

        // Create attempt
        String attemptId = UUID.randomUUID().toString();
        QuizAttempt attempt = new QuizAttempt(
                attemptId,
                quiz.getQuizId(),
                userId,
                selectedIndices,
                scorePercent,
                passed,
                currentAttemptNumber);

        // Save attempt to lesson
        Optional<Course> courseOpt = courseDAO.findById(courseId);
        if (!courseOpt.isPresent()) {
            throw new Exception("Course not found");
        }

        Course course = courseOpt.get();
        Optional<Lesson> lessonOpt = course.getLessons().stream()
                .filter(l -> l.getLessonId().equals(lessonId))
                .findFirst();

        if (!lessonOpt.isPresent()) {
            throw new Exception("Lesson not found");
        }

        Lesson lesson = lessonOpt.get();
        lesson.addAttempt(attempt);
        courseDAO.updateCourse(course);

        return attempt;
    }

    /**
     * Get all attempts for a quiz by a user
     */
    public List<QuizAttempt> getUserQuizAttempts(String courseId, String lessonId, String userId) throws Exception {
        Optional<Course> courseOpt = courseDAO.findById(courseId);
        if (!courseOpt.isPresent()) {
            throw new Exception("Course not found");
        }

        Course course = courseOpt.get();
        Optional<Lesson> lessonOpt = course.getLessons().stream()
                .filter(l -> l.getLessonId().equals(lessonId))
                .findFirst();

        if (!lessonOpt.isPresent()) {
            throw new Exception("Lesson not found");
        }

        Lesson lesson = lessonOpt.get();
        Quiz quiz = getQuizForLesson(courseId, lessonId);
        if (quiz == null) {
            return new ArrayList<>();
        }

        return lesson.getAttempts().stream()
                .filter(a -> a.getQuizId().equals(quiz.getQuizId()) && a.getUserId().equals(userId))
                .sorted(Comparator.comparing(QuizAttempt::getAttemptNumber).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Get all attempts for a quiz (for instructors)
     */
    public List<QuizAttempt> getAllQuizAttempts(String courseId, String lessonId) throws Exception {
        Optional<Course> courseOpt = courseDAO.findById(courseId);
        if (!courseOpt.isPresent()) {
            throw new Exception("Course not found");
        }

        Course course = courseOpt.get();
        Optional<Lesson> lessonOpt = course.getLessons().stream()
                .filter(l -> l.getLessonId().equals(lessonId))
                .findFirst();

        if (!lessonOpt.isPresent()) {
            throw new Exception("Lesson not found");
        }

        Lesson lesson = lessonOpt.get();
        Quiz quiz = getQuizForLesson(courseId, lessonId);
        if (quiz == null) {
            return new ArrayList<>();
        }

        return lesson.getAttempts().stream()
                .filter(a -> a.getQuizId().equals(quiz.getQuizId()))
                .sorted(Comparator.comparing(QuizAttempt::getAttemptNumber).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Check if user can retake quiz
     */
    public boolean canRetakeQuiz(String courseId, String lessonId, String userId) throws Exception {
        Quiz quiz = getQuizForLesson(courseId, lessonId);
        if (quiz == null) {
            return false;
        }

        // Unlimited retries
        if (quiz.getMaxRetries() < 0) {
            return true;
        }

        // Check current attempt count
        int currentAttemptNumber = getNextAttemptNumber(courseId, lessonId, userId);
        return currentAttemptNumber <= quiz.getMaxRetries();
    }

    public boolean hasPassedAllQuizzes(String courseId, String userId) throws Exception {
        Optional<Course> courseOpt = courseDAO.findById(courseId);
        if (!courseOpt.isPresent())
            return false;

        Course course = courseOpt.get();

        for (Lesson lesson : course.getLessons()) {
            Quiz quiz = getQuizForLesson(courseId, lesson.getLessonId());
            if (quiz != null) {
                List<QuizAttempt> attempts = getUserQuizAttempts(courseId, lesson.getLessonId(), userId);
                boolean passed = attempts.stream().anyMatch(QuizAttempt::isPassed);
                if (!passed)
                    return false;
            }
        }
        return true;
    }

}
