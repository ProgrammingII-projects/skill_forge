package service;

import dao.CourseDAO;
import dao.UserDAO;
import model.Course;
import model.User;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service layer for Course business logic (Backend Layer)
 * Handles all course-related operations
 */
public class CourseService {
    private final CourseDAO courseDAO;
    private final UserDAO userDAO;

    public CourseService(CourseDAO courseDAO, UserDAO userDAO) {
        this.courseDAO = courseDAO;
        this.userDAO = userDAO;
    }

    public Course createCourse(String title, String description, String instructorId) throws Exception {
        if (title == null || title.isEmpty()) throw new Exception("Title required");
        if (instructorId == null || instructorId.isEmpty()) throw new Exception("Instructor ID required");
        if (!userDAO.findById(instructorId).isPresent()) throw new Exception("Instructor not found");
        
        String id = UUID.randomUUID().toString();
        Course c = new Course(id, title, description == null ? "" : description, instructorId,"pending");
        courseDAO.addCourse(c);
        return c;
    }

    public List<Course> getAllCourses() {
        return courseDAO.loadAll();
    }

    public List<Course> getCoursesByInstructor(String instructorId) {
        return courseDAO.loadAll().stream()
                .filter(c -> c.getInstructorId().equals(instructorId))
                .collect(Collectors.toList());
    }

    public Optional<Course> findById(String courseId) {
        return courseDAO.findById(courseId);
    }

    public void updateCourse(Course course) throws Exception {
        if (course == null) throw new Exception("Course cannot be null");
        Optional<Course> existing = courseDAO.findById(course.getCourseId());
        if (!existing.isPresent()) {
            throw new Exception("Course not found");
        }
        courseDAO.updateCourse(course);
    }
    
    public String getInstructorName(String instructorId) {
        Optional<User> opt = userDAO.findById(instructorId);
        return opt.isPresent() ? opt.get().getUsername() : "Unknown";
    }
    
    public List<User> getStudentsByCourse(String courseId) throws Exception {
        Optional<Course> opt = courseDAO.findById(courseId);
        if (!opt.isPresent()) {
            throw new Exception("Course not found");
        }
        Course course = opt.get();
        List<User> students = new java.util.ArrayList<>();
        for (String studentId : course.getStudents()) {
            Optional<User> userOpt = userDAO.findById(studentId);
            if (userOpt.isPresent()) {
                students.add(userOpt.get());
            }
        }
        return students;
    }

    public void deleteCourse(String courseId) throws Exception {
        if (!courseDAO.findById(courseId).isPresent()) {
            throw new Exception("Course not found");
        }
        courseDAO.deleteCourse(courseId);
    }
}

