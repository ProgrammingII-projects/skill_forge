package controller;

import model.User;
import service.StudentService;
import service.QuizService;

/**
 * Controller for Student operations (Presentation Layer)
 * Acts as a bridge between View and Service layers
 */
public class StudentController {
    private final StudentService studentService;
    private final QuizService quizService;

    public StudentController(StudentService studentService, QuizService quizService) {
        this.studentService = studentService;
        this.quizService = quizService;
    }

    public void enrollStudent(String studentId, String courseId) throws Exception {
        studentService.enrollStudent(studentId, courseId);
    }

    public void markLessonCompleted(String studentId, String courseId, String lessonId) throws Exception {
        studentService.markLessonCompleted(studentId, courseId, lessonId);
    }

    public User getUserById(String userId) throws Exception {
        return studentService.getUserById(userId);
    }

    public boolean canEarnCertificate(String courseId, String userId) {
    try {
        boolean lessonsDone = studentService.hasCompletedAllLessons(courseId, userId);
        boolean quizzesPassed = quizService.hasPassedAllQuizzes(courseId, userId);
        return lessonsDone && quizzesPassed;
    } catch (Exception e) {
        return false;
    }
}

    
}
