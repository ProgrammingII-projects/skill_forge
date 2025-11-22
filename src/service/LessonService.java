package service;

import dao.CourseDAO;
import model.Course;
import model.Lesson;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for Lesson business logic (Backend Layer)
 * Handles lesson management within courses
 */
public class LessonService {
    private final CourseDAO courseDAO;

    public LessonService(CourseDAO courseDAO) {
        this.courseDAO = courseDAO;
    }

    public void addLesson(String courseId, Lesson lesson) throws Exception {
        Optional<Course> opt = courseDAO.findById(courseId);
        if (!opt.isPresent()) throw new Exception("Course not found");
        Course c = opt.get();
        
        if (lesson == null) throw new Exception("Lesson cannot be null");
        
        c.addLesson(lesson);
        courseDAO.updateCourse(c);
    }

    public void editLesson(String courseId, Lesson lesson) throws Exception {
        Optional<Course> opt = courseDAO.findById(courseId);
        if (!opt.isPresent()) throw new Exception("Course not found");
        Course c = opt.get();
        
        if (lesson == null) throw new Exception("Lesson cannot be null");
        
        c.updateLesson(lesson);
        courseDAO.updateCourse(c);
    }

    public void deleteLesson(String courseId, String lessonId) throws Exception {
        Optional<Course> opt = courseDAO.findById(courseId);
        if (!opt.isPresent()) throw new Exception("Course not found");
        Course c = opt.get();
        
        boolean lessonExists = c.getLessons().stream()
                .anyMatch(l -> l.getLessonId().equals(lessonId));
        if (!lessonExists) throw new Exception("Lesson not found");
        
        c.removeLesson(lessonId);
        courseDAO.updateCourse(c);
    }

    public List<Lesson> getLessons(String courseId) throws Exception {
        Optional<Course> opt = courseDAO.findById(courseId);
        if (!opt.isPresent()) throw new Exception("Course not found");
        return opt.get().getLessons();
    }
}

