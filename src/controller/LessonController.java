package controller;

import model.Lesson;
import service.LessonService;
import java.util.List;

/**
 * Controller for Lesson operations (Presentation Layer)
 * Acts as a bridge between View and Service layers
 */
public class LessonController {
    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    public void addLesson(String courseId, Lesson lesson) throws Exception {
        lessonService.addLesson(courseId, lesson);
    }

    public void editLesson(String courseId, Lesson lesson) throws Exception {
        lessonService.editLesson(courseId, lesson);
    }

    public void deleteLesson(String courseId, String lessonId) throws Exception {
        lessonService.deleteLesson(courseId, lessonId);
    }

    public List<Lesson> getLessons(String courseId) throws Exception {
        return lessonService.getLessons(courseId);
    }
    
}
