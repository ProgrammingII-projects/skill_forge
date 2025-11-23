package service;

import dao.CourseDAO;
import dao.UserDAO;
import model.Course;
import model.Lesson;
import model.Quiz;
import model.QuizAttempt;
import model.User;

import java.util.*;
import java.util.stream.Collectors;

public class AnalyticsService {
    private final CourseDAO courseDAO;
    private final UserDAO userDAO;

    public AnalyticsService(CourseDAO courseDAO, UserDAO userDAO) {
        this.courseDAO = courseDAO;
        this.userDAO = userDAO;
    }

    public Map<String, Object> getCourseAnalytics(String courseId) throws Exception {
        Optional<Course> courseOpt = courseDAO.findById(courseId);
        if (!courseOpt.isPresent()) {
            throw new Exception("Course not found");
        }

        Course course = courseOpt.get();
        Map<String, Object> analytics = new HashMap<>();
        
        int totalStudents = course.getStudents().size();
        int totalLessons = course.getLessons().size();
        int totalQuizzes = 0;
        int totalQuizAttempts = 0;
        int passedAttempts = 0;
        double totalScore = 0.0;
        
        List<Map<String, Object>> lessonStats = new ArrayList<>();
        
        for (Lesson lesson : course.getLessons()) {
            Map<String, Object> lessonStat = new HashMap<>();
            lessonStat.put("lessonId", lesson.getLessonId());
            lessonStat.put("lessonTitle", lesson.getTitle());
            
            int completedCount = 0;
            for (String studentId : course.getStudents()) {
                try {
                    User student = userDAO.findById(studentId).orElse(null);
                    if (student != null) {
                        List<String> completed = student.getProgress().getOrDefault(courseId, new ArrayList<>());
                        if (completed.contains(lesson.getLessonId())) {
                            completedCount++;
                        }
                    }
                } catch (Exception e) {
                }
            }
            
            lessonStat.put("completedCount", completedCount);
            lessonStat.put("completionRate", totalStudents > 0 ? (completedCount * 100.0 / totalStudents) : 0.0);
            
            if (lesson.getQuizzes() != null && !lesson.getQuizzes().isEmpty()) {
                Quiz quiz = lesson.getQuizzes().get(0);
                totalQuizzes++;
                
                List<QuizAttempt> attempts = lesson.getAttempts();
                if (attempts != null) {
                    List<QuizAttempt> quizAttempts = attempts.stream()
                            .filter(a -> a.getQuizId().equals(quiz.getQuizId()))
                            .collect(Collectors.toList());
                    
                    totalQuizAttempts += quizAttempts.size();
                    passedAttempts += quizAttempts.stream().filter(QuizAttempt::isPassed).count();
                    
                    double quizTotalScore = quizAttempts.stream()
                            .mapToDouble(QuizAttempt::getScorePercent)
                            .sum();
                    totalScore += quizTotalScore;
                    
                    lessonStat.put("quizTitle", quiz.getTitle());
                    lessonStat.put("quizAttempts", quizAttempts.size());
                    lessonStat.put("quizPassed", quizAttempts.stream().filter(QuizAttempt::isPassed).count());
                    lessonStat.put("quizAverageScore", quizAttempts.isEmpty() ? 0.0 : quizTotalScore / quizAttempts.size());
                }
            }
            
            lessonStats.add(lessonStat);
        }
        
        analytics.put("courseId", courseId);
        analytics.put("courseTitle", course.getTitle());
        analytics.put("totalStudents", totalStudents);
        analytics.put("totalLessons", totalLessons);
        analytics.put("totalQuizzes", totalQuizzes);
        analytics.put("totalQuizAttempts", totalQuizAttempts);
        analytics.put("passedAttempts", passedAttempts);
        analytics.put("passRate", totalQuizAttempts > 0 ? (passedAttempts * 100.0 / totalQuizAttempts) : 0.0);
        analytics.put("averageScore", totalQuizAttempts > 0 ? (totalScore / totalQuizAttempts) : 0.0);
        analytics.put("lessonStats", lessonStats);
        
        return analytics;
    }

    public Map<String, Object> getQuizAnalytics(String courseId, String lessonId) throws Exception {
        Optional<Course> courseOpt = courseDAO.findById(courseId);
        if (!courseOpt.isPresent()) {
            throw new Exception("Course not found");
        }

        Course course = courseOpt.get();
        Optional<Lesson> lessonOpt = course.getLessons().stream()
                .filter(l -> l.getLessonId().equals(lessonId))
                .findFirst();

        if (!lessonOpt.isPresent()) {
            throw new Exception("Lesson not found");
        }

        Lesson lesson = lessonOpt.get();
        Map<String, Object> analytics = new HashMap<>();
        
        if (lesson.getQuizzes() == null || lesson.getQuizzes().isEmpty()) {
            throw new Exception("No quiz found for this lesson");
        }

        Quiz quiz = lesson.getQuizzes().get(0);
        List<QuizAttempt> attempts = lesson.getAttempts() != null ? 
                lesson.getAttempts().stream()
                        .filter(a -> a.getQuizId().equals(quiz.getQuizId()))
                        .collect(Collectors.toList()) : new ArrayList<>();
        
        analytics.put("quizId", quiz.getQuizId());
        analytics.put("quizTitle", quiz.getTitle());
        analytics.put("totalAttempts", attempts.size());
        analytics.put("totalQuestions", quiz.getQuestions().size());
        analytics.put("passingScore", quiz.getPassScorePercent());
        
        long passedCount = attempts.stream().filter(QuizAttempt::isPassed).count();
        analytics.put("passedCount", passedCount);
        analytics.put("failedCount", attempts.size() - passedCount);
        analytics.put("passRate", attempts.isEmpty() ? 0.0 : (passedCount * 100.0 / attempts.size()));
        
        double avgScore = attempts.isEmpty() ? 0.0 : 
                attempts.stream().mapToDouble(QuizAttempt::getScorePercent).average().orElse(0.0);
        analytics.put("averageScore", avgScore);
        
        double minScore = attempts.isEmpty() ? 0.0 : 
                attempts.stream().mapToDouble(QuizAttempt::getScorePercent).min().orElse(0.0);
        analytics.put("minScore", minScore);
        
        double maxScore = attempts.isEmpty() ? 0.0 : 
                attempts.stream().mapToDouble(QuizAttempt::getScorePercent).max().orElse(0.0);
        analytics.put("maxScore", maxScore);
        
        Map<String, Integer> questionStats = new HashMap<>();
        for (int i = 0; i < quiz.getQuestions().size(); i++) {
            int correctCount = 0;
            for (QuizAttempt attempt : attempts) {
                if (i < attempt.getSelectedIndices().size()) {
                    int selectedIndex = attempt.getSelectedIndices().get(i);
                    String selectedAnswer = selectedIndex >= 0 && selectedIndex < quiz.getQuestions().get(i).getOptions().size() ?
                            quiz.getQuestions().get(i).getOptions().get(selectedIndex) : "";
                    if (selectedAnswer.equals(quiz.getQuestions().get(i).getCorrectAnswer())) {
                        correctCount++;
                    }
                }
            }
            questionStats.put("question_" + i, correctCount);
        }
        analytics.put("questionStats", questionStats);
        
        List<Map<String, Object>> studentAttempts = new ArrayList<>();
        for (QuizAttempt attempt : attempts) {
            Map<String, Object> attemptData = new HashMap<>();
            try {
                User student = userDAO.findById(attempt.getUserId()).orElse(null);
                attemptData.put("studentName", student != null ? student.getUsername() : "Unknown");
                attemptData.put("studentId", attempt.getUserId());
                attemptData.put("score", attempt.getScorePercent());
                attemptData.put("passed", attempt.isPassed());
                attemptData.put("attemptNumber", attempt.getAttemptNumber());
                studentAttempts.add(attemptData);
            } catch (Exception e) {
            }
        }
        analytics.put("studentAttempts", studentAttempts);
        
        return analytics;
    }

    public Map<String, Object> getInstructorAnalytics(String instructorId) throws Exception {
        List<Course> courses = courseDAO.loadAll().stream()
                .filter(c -> c.getInstructorId().equals(instructorId))
                .collect(Collectors.toList());
        
        Map<String, Object> analytics = new HashMap<>();
        
        int totalCourses = courses.size();
        int approvedCourses = (int) courses.stream().filter(c -> c.getApproveStatus().equals("approved")).count();
        int totalStudents = 0;
        int totalLessons = 0;
        int totalQuizzes = 0;
        
        for (Course course : courses) {
            totalStudents += course.getStudents().size();
            totalLessons += course.getLessons().size();
            for (Lesson lesson : course.getLessons()) {
                if (lesson.getQuizzes() != null && !lesson.getQuizzes().isEmpty()) {
                    totalQuizzes++;
                }
            }
        }
        
        analytics.put("instructorId", instructorId);
        analytics.put("totalCourses", totalCourses);
        analytics.put("approvedCourses", approvedCourses);
        analytics.put("pendingCourses", totalCourses - approvedCourses);
        analytics.put("totalStudents", totalStudents);
        analytics.put("totalLessons", totalLessons);
        analytics.put("totalQuizzes", totalQuizzes);
        
        List<Map<String, Object>> courseList = new ArrayList<>();
        for (Course course : courses) {
            Map<String, Object> courseData = new HashMap<>();
            courseData.put("courseId", course.getCourseId());
            courseData.put("title", course.getTitle());
            courseData.put("status", course.getApproveStatus());
            courseData.put("students", course.getStudents().size());
            courseData.put("lessons", course.getLessons().size());
            courseList.add(courseData);
        }
        analytics.put("courses", courseList);
        
        return analytics;
    }

    public Map<String, Object> getSystemAnalytics() {
        List<Course> allCourses = courseDAO.loadAll();
        List<User> allUsers = userDAO.loadAll();
        
        Map<String, Object> analytics = new HashMap<>();
        
        int totalCourses = allCourses.size();
        int approvedCourses = (int) allCourses.stream().filter(c -> c.getApproveStatus().equals("approved")).count();
        int pendingCourses = (int) allCourses.stream().filter(c -> c.getApproveStatus().equals("pending")).count();
        int disapprovedCourses = (int) allCourses.stream().filter(c -> c.getApproveStatus().equals("disapproved")).count();
        
        int totalStudents = (int) allUsers.stream().filter(u -> u.getRole().equals("student")).count();
        int totalInstructors = (int) allUsers.stream().filter(u -> u.getRole().equals("instructor")).count();
        int totalAdmins = (int) allUsers.stream().filter(u -> u.getRole().equals("admin")).count();
        
        int totalLessons = 0;
        int totalQuizzes = 0;
        int totalQuizAttempts = 0;
        
        for (Course course : allCourses) {
            totalLessons += course.getLessons().size();
            for (Lesson lesson : course.getLessons()) {
                if (lesson.getQuizzes() != null && !lesson.getQuizzes().isEmpty()) {
                    totalQuizzes++;
                }
                if (lesson.getAttempts() != null) {
                    totalQuizAttempts += lesson.getAttempts().size();
                }
            }
        }
        
        analytics.put("totalCourses", totalCourses);
        analytics.put("approvedCourses", approvedCourses);
        analytics.put("pendingCourses", pendingCourses);
        analytics.put("disapprovedCourses", disapprovedCourses);
        analytics.put("totalStudents", totalStudents);
        analytics.put("totalInstructors", totalInstructors);
        analytics.put("totalAdmins", totalAdmins);
        analytics.put("totalLessons", totalLessons);
        analytics.put("totalQuizzes", totalQuizzes);
        analytics.put("totalQuizAttempts", totalQuizAttempts);
        
        return analytics;
    }
}
