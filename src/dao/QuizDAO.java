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

    private void saveAll(List<Quiz> quizzes) {
    }

    public void saveQuiz(Quiz quiz) {
        List<Course> courses = courseDAO.loadAll();
        for (Course course : courses) {
            for (Lesson lesson : course.getLessons()) {
                for (int i = 0; i < lesson.getQuizzes().size(); i++) {
                    if (lesson.getQuizzes().get(i).getQuizId().equals(quiz.getQuizId())) {
                        lesson.getQuizzes().set(i, quiz);
                        courseDAO.updateCourse(course);
                        return;
                    }
                }
            }
        }
    }

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
