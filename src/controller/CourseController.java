package controller;

import model.Course;
import service.CourseService;
import java.util.List;
import java.util.Optional;

/**
 * Controller for Course operations (Presentation Layer)
 * Acts as a bridge between View and Service layers
 */
public class CourseController {
    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    public Course createCourse(String title, String description, String instructorId) throws Exception {
        return courseService.createCourse(title, description, instructorId);
    }

    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    public List<Course> getCoursesByInstructor(String instructorId) {
        return courseService.getCoursesByInstructor(instructorId);
    }

    public Optional<Course> findById(String courseId) {
        return courseService.findById(courseId);
    }

    public void updateCourse(Course course) throws Exception {
        courseService.updateCourse(course);
    }

    public void deleteCourse(String courseId) throws Exception {
        courseService.deleteCourse(courseId);
    }
}
