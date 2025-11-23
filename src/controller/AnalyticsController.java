

package controller;

import service.AnalyticsService;
import java.util.Map;

public class AnalyticsController {
    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    public Map<String, Object> getCourseAnalytics(String courseId) throws Exception {
        return analyticsService.getCourseAnalytics(courseId);
    }

    public Map<String, Object> getQuizAnalytics(String courseId, String lessonId) throws Exception {
        return analyticsService.getQuizAnalytics(courseId, lessonId);
    }

    public Map<String, Object> getInstructorAnalytics(String instructorId) throws Exception {
        return analyticsService.getInstructorAnalytics(instructorId);
    }

    public Map<String, Object> getSystemAnalytics() {
        return analyticsService.getSystemAnalytics();
    }
}