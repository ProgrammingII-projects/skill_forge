package src.controller;

import model.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class CourseController {
    private final CourseModel courseModel;

    public CourseController(CourseModel courseModel) {
        this.courseModel = courseModel;
    }

    public model.Course createCourse(String title, String description, String instructorId) throws Exception {
        if (title == null || title.isEmpty()) throw new Exception("Title required");
        String id = UUID.randomUUID().toString();
        model.Course c = new model.Course(id, title, description == null ? "" : description, instructorId);
        courseModel.addCourse(c);
        return c;
    }

    public List<model.Course> getAllCourses() {
        return courseModel.loadAll();
    }

    public List<model.Course> getCoursesByInstructor(String instructorId) {
        return courseModel.loadAll().stream()
                .filter(c -> c.getInstructorId().equals(instructorId))
                .collect(Collectors.toList());
    }

    public Optional<model.Course> findById(String courseId) {
        return courseModel.findById(courseId);
    }

    public void updateCourse(model.Course course) {
        courseModel.updateCourse(course);
    }

    public void deleteCourse(String courseId) {
        courseModel.deleteCourse(courseId);
    }
}
