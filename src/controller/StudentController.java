package controller;

import model.User;
import service.StudentService;

/**
 * Controller for Student operations (Presentation Layer)
 * Acts as a bridge between View and Service layers
 */
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
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
}
