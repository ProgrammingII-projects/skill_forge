package controller;

import model.*;
import model.database_manager.CourseModel;
import model.database_manager.UserModel;

import java.util.Optional;

public class StudentController {
    private final UserModel userModel;
    private final CourseModel courseModel;

    public StudentController(UserModel userModel, CourseModel courseModel) {
        this.userModel = userModel;
        this.courseModel = courseModel;
    }

    public void enrollStudent(String studentId, String courseId) throws Exception {
        Optional<User> uopt = userModel.findById(studentId);
        Optional<Course> copt = courseModel.findById(courseId);
        if (!uopt.isPresent()) throw new Exception("Student not found");
        if (!copt.isPresent()) throw new Exception("Course not found");
        User u = uopt.get();
        Course c = copt.get();
        u.enrollCourse(courseId);
        c.enrollStudent(studentId);
        userModel.updateUser(u);
        courseModel.updateCourse(c);
    }

    public void markLessonCompleted(String studentId, String courseId, String lessonId) throws Exception {
        Optional<User> uopt = userModel.findById(studentId);
        if (!uopt.isPresent()) throw new Exception("Student not found");
        User u = uopt.get();
        u.markLessonCompleted(courseId, lessonId);
        userModel.updateUser(u);
    }
}
