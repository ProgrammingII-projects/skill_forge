package controller;

import model.*;
import model.database_manager.CourseModel;

import java.util.List;
import java.util.Optional;

public class LessonController {
    private final CourseModel courseModel;

    public LessonController(CourseModel courseModel) {
        this.courseModel = courseModel;
    }

    public void addLesson(String courseId, model.Lesson lesson) throws Exception {
        Optional<model.Course> opt = courseModel.findById(courseId);
        if (!opt.isPresent()) throw new Exception("Course not found");
        model.Course c = opt.get();
        c.addLesson(lesson);
        courseModel.updateCourse(c);
    }

    public void editLesson(String courseId, model.Lesson lesson) throws Exception {
        Optional<model.Course> opt = courseModel.findById(courseId);
        if (!opt.isPresent()) throw new Exception("Course not found");
        model.Course c = opt.get();
        c.updateLesson(lesson);
        courseModel.updateCourse(c);
    }

    public void deleteLesson(String courseId, String lessonId) throws Exception {
        Optional<model.Course> opt = courseModel.findById(courseId);
        if (!opt.isPresent()) throw new Exception("Course not found");
        model.Course c = opt.get();
        c.removeLesson(lessonId);
        courseModel.updateCourse(c);
    }

    public List<model.Lesson> getLessons(String courseId) throws Exception {
        Optional<model.Course> opt = courseModel.findById(courseId);
        if (!opt.isPresent()) throw new Exception("Course not found");
        return opt.get().getLessons();
    }
}
