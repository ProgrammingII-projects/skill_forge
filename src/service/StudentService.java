package service;

import dao.CourseDAO;
import dao.UserDAO;
import model.Course;
import model.User;
import model.Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for Student business logic (Backend Layer)
 * Handles student enrollment and progress tracking
 */
public class StudentService {
    private final UserDAO userDAO;
    private final CourseDAO courseDAO;

    public StudentService(UserDAO userDAO, CourseDAO courseDAO) {
        this.userDAO = userDAO;
        this.courseDAO = courseDAO;
    }

    public void enrollStudent(String studentId, String courseId) throws Exception {
        Optional<User> uopt = userDAO.findById(studentId);
        Optional<Course> copt = courseDAO.findById(courseId);

        if (!uopt.isPresent())
            throw new Exception("Student not found");
        if (!copt.isPresent())
            throw new Exception("Course not found");

        User u = uopt.get();
        Course c = copt.get();

        if (u.getEnrolledCourses().contains(courseId)) {
            throw new Exception("Student is already enrolled in this course");
        }

        u.enrollCourse(courseId);
        c.enrollStudent(studentId);

        userDAO.updateUser(u);
        courseDAO.updateCourse(c);
    }

    public void markLessonCompleted(String studentId, String courseId, String lessonId) throws Exception {
        Optional<User> uopt = userDAO.findById(studentId);
        if (!uopt.isPresent())
            throw new Exception("Student not found");

        Optional<Course> copt = courseDAO.findById(courseId);
        if (!copt.isPresent())
            throw new Exception("Course not found");

        Course course = copt.get();
        boolean lessonExists = course.getLessons().stream()
                .anyMatch(l -> l.getLessonId().equals(lessonId));
        if (!lessonExists)
            throw new Exception("Lesson not found in course");

        User u = uopt.get();
        u.markLessonCompleted(courseId, lessonId);
        userDAO.updateUser(u);
    }

    public User getUserById(String userId) throws Exception {
        Optional<User> opt = userDAO.findById(userId);
        if (!opt.isPresent())
            throw new Exception("User not found");
        return opt.get();
    }

    public List<Certificate> getCertificates(String studentId) throws Exception {

        User u = getUserById(studentId);
        return new ArrayList<>(u.getCertificates());
    }

    public Certificate getCertificate(String studentId, String courseId) throws Exception {

        User u = getUserById(studentId);
        for (Certificate c : u.getCertificates()) {
            if (c != null && courseId.equals(c.getCourseId())) {
                return c;
            }
        }
        return null;
    }

    public void earnCertificate(String studentId, String courseId) throws Exception {
        Optional<User> uopt = userDAO.findById(studentId);
        if (!uopt.isPresent())
            throw new Exception("Student not found");

        Optional<Course> copt = courseDAO.findById(courseId);
        if (!copt.isPresent())
            throw new Exception("Course not found");

        User u = uopt.get();
        Course c = copt.get();
        u.earnCertificate(new Certificate(studentId, courseId));
    }

    public List<Course> getApprovedCourses() {
        return courseDAO.loadAll().stream()
                .filter(c -> c.getApproveStatus().equals("approved"))
                .collect(Collectors.toList());
    }

    public boolean hasCompletedAllLessons(String courseId, String userId) throws Exception {
        User user = userDAO.getUser(userId);
        Course course = courseDAO.findById(courseId).orElse(null);

        if (user == null || course == null)
            return false;

        List<String> completed = user.getProgress().getOrDefault(courseId, new ArrayList<>());
        return completed.size() == course.getLessons().size();
    }

}
