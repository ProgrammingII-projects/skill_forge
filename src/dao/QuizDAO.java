package dao;

import model.Quiz;
import model.Lesson;
import model.Course;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class QuizDAO {

    private final String filePath;
    private final CourseDAO courseDAO;

    public QuizDAO(String filePath, CourseDAO courseDAO) {
        this.filePath = filePath;
        this.courseDAO = courseDAO;
    }

    // Load all quizzes from all courses
    public List<Quiz> loadAll() {
        List<Quiz> allQuizzes = new ArrayList<>();
        List<Course> courses = courseDAO.loadAll();
        for (Course course : courses) {
            for (Lesson lesson : course.getLessons()) {
                allQuizzes.addAll(lesson.getQuizzes());
            }
        }
        return allQuizzes;
    }

    // Save all quizzes (not used directly, but kept for compatibility)
    private void saveAll(List<Quiz> quizzes) {
        // This method is not used since quizzes are saved through CourseDAO
        // Kept for compatibility but does nothing
    }

    // Add or update quiz - finds it in courses and updates it
    public void saveQuiz(Quiz quiz) {
        List<Course> courses = courseDAO.loadAll();
        for (Course course : courses) {
            for (Lesson lesson : course.getLessons()) {
                // Check if this quiz exists in this lesson
                for (int i = 0; i < lesson.getQuizzes().size(); i++) {
                    if (lesson.getQuizzes().get(i).getQuizId().equals(quiz.getQuizId())) {
                        // Update existing quiz
                        lesson.getQuizzes().set(i, quiz);
                        courseDAO.updateCourse(course);
                        return;
                    }
                }
            }
        }
        // Quiz not found in any lesson - it will be added when setQuizForLesson is called
        // For now, we don't add it automatically since we don't know which lesson it belongs to
    }

    // Find quiz by ID - searches through all courses
    public Quiz getQuiz(String id) {
        List<Course> courses = courseDAO.loadAll();
        for (Course course : courses) {
            for (Lesson lesson : course.getLessons()) {
                for (Quiz quiz : lesson.getQuizzes()) {
                    if (quiz.getQuizId().equals(id)) {
                        return quiz;
                    }
                }
            }
        }
        return null;
    }

    // Delete quiz - finds it in courses and removes it
    public boolean deleteQuiz(String id) {
        List<Course> courses = courseDAO.loadAll();
        for (Course course : courses) {
            for (Lesson lesson : course.getLessons()) {
                boolean removed = lesson.getQuizzes().removeIf(q -> q.getQuizId().equals(id));
                if (removed) {
                    courseDAO.updateCourse(course);
                    return true;
                }
            }
        }
        return false;
    }

    // List all quizzes for a course (by course ID, not quiz ID)
    public List<Quiz> getQuizzesByCourse(String courseId) {
        Optional<Course> courseOpt = courseDAO.findById(courseId);
        if (!courseOpt.isPresent()) {
            return new ArrayList<>();
        }
        
        List<Quiz> result = new ArrayList<>();
        Course course = courseOpt.get();
        for (Lesson lesson : course.getLessons()) {
            result.addAll(lesson.getQuizzes());
        }
        return result;
    }
}
