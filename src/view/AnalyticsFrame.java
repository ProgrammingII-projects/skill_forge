package view;

import controller.AnalyticsController;
import controller.CourseController;
import dao.UserDAO;
import model.Course;
import model.User;
import view.Instructor.InstructorDashboardFrame;
import view.Admin.AdminDashboardFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

public class AnalyticsFrame extends JFrame {
    private User user;
    private AnalyticsController analyticsController;
    private CourseController courseController;
    private UserDAO userDAO;
    private JTextArea analyticsArea;
    private InstructorDashboardFrame instructorDashboardFrame;
    private AdminDashboardFrame adminDashboardFrame;

    public AnalyticsFrame(User user, AnalyticsController analyticsController, CourseController courseController,
                         InstructorDashboardFrame instructorDashboardFrame, AdminDashboardFrame adminDashboardFrame) {
        this.user = user;
        this.analyticsController = analyticsController;
        this.courseController = courseController;
        this.userDAO = new UserDAO("Lab 7/skill_forge/data/users.json");
        this.instructorDashboardFrame = instructorDashboardFrame;
        this.adminDashboardFrame = adminDashboardFrame;
        
        setTitle("Analytics - " + user.getUsername());
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        if ("admin".equalsIgnoreCase(user.getRole())) {
            JButton systemStatsButton = new JButton("System Statistics");
            systemStatsButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showSystemAnalytics();
                }
            });
            buttonPanel.add(systemStatsButton);
            
            JButton courseStatsButton = new JButton("Course Analytics");
            courseStatsButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showAdminCourseAnalytics();
                }
            });
            buttonPanel.add(courseStatsButton);
            
            JButton quizStatsButton = new JButton("Quiz Analytics");
            quizStatsButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showAdminQuizAnalytics();
                }
            });
            buttonPanel.add(quizStatsButton);
            
            JButton instructorStatsButton = new JButton("Instructor Analytics");
            instructorStatsButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showAdminInstructorAnalytics();
                }
            });
            buttonPanel.add(instructorStatsButton);
        } else if ("instructor".equalsIgnoreCase(user.getRole())) {
            JButton instructorStatsButton = new JButton("My Statistics");
            instructorStatsButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showInstructorAnalytics();
                }
            });
            buttonPanel.add(instructorStatsButton);
            
            JButton courseStatsButton = new JButton("Course Analytics");
            courseStatsButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showCourseAnalytics();
                }
            });
            buttonPanel.add(courseStatsButton);
            
            JButton quizStatsButton = new JButton("Quiz Analytics");
            quizStatsButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showQuizAnalytics();
                }
            });
            buttonPanel.add(quizStatsButton);
        }
        
        JButton backButton = new JButton("Back to Dashboard");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goBackToDashboard();
            }
        });
        buttonPanel.add(backButton);
        
        add(buttonPanel, BorderLayout.NORTH);
        
        analyticsArea = new JTextArea();
        analyticsArea.setEditable(false);
        analyticsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(analyticsArea);
        add(scrollPane, BorderLayout.CENTER);
        
        if ("admin".equalsIgnoreCase(user.getRole())) {
            showSystemAnalytics();
        } else if ("instructor".equalsIgnoreCase(user.getRole())) {
            showInstructorAnalytics();
        }
    }
    
    private void goBackToDashboard() {
        if ("admin".equalsIgnoreCase(user.getRole()) && adminDashboardFrame != null) {
            adminDashboardFrame.setVisible(true);
        } else if ("instructor".equalsIgnoreCase(user.getRole()) && instructorDashboardFrame != null) {
            instructorDashboardFrame.refreshCourseList();
            instructorDashboardFrame.setVisible(true);
        }
        dispose();
    }
    
    private void showSystemAnalytics() {
        try {
            Map<String, Object> analytics = analyticsController.getSystemAnalytics();
            
            StringBuilder sb = new StringBuilder();
            sb.append("=== SYSTEM ANALYTICS ===\n\n");
            
            sb.append("=== COURSES ===\n");
            sb.append("Total Courses: ").append(analytics.get("totalCourses")).append("\n");
            sb.append("Approved: ").append(analytics.get("approvedCourses")).append("\n");
            sb.append("Pending: ").append(analytics.get("pendingCourses")).append("\n");
            sb.append("Disapproved: ").append(analytics.get("disapprovedCourses")).append("\n\n");
            
            sb.append("=== USERS ===\n");
            sb.append("Total Students: ").append(analytics.get("totalStudents")).append("\n");
            sb.append("Total Instructors: ").append(analytics.get("totalInstructors")).append("\n");
            sb.append("Total Admins: ").append(analytics.get("totalAdmins")).append("\n\n");
            
            sb.append("=== CONTENT ===\n");
            sb.append("Total Lessons: ").append(analytics.get("totalLessons")).append("\n");
            sb.append("Total Quizzes: ").append(analytics.get("totalQuizzes")).append("\n");
            sb.append("Total Quiz Attempts: ").append(analytics.get("totalQuizAttempts")).append("\n");
            
            analyticsArea.setText(sb.toString());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showInstructorAnalytics() {
        try {
            Map<String, Object> analytics = analyticsController.getInstructorAnalytics(user.getUserId());
            
            StringBuilder sb = new StringBuilder();
            sb.append("=== INSTRUCTOR ANALYTICS ===\n\n");
            sb.append("Total Courses: ").append(analytics.get("totalCourses")).append("\n");
            sb.append("Approved Courses: ").append(analytics.get("approvedCourses")).append("\n");
            sb.append("Pending Courses: ").append(analytics.get("pendingCourses")).append("\n");
            sb.append("Total Students: ").append(analytics.get("totalStudents")).append("\n");
            sb.append("Total Lessons: ").append(analytics.get("totalLessons")).append("\n");
            sb.append("Total Quizzes: ").append(analytics.get("totalQuizzes")).append("\n\n");
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> courses = (List<Map<String, Object>>) analytics.get("courses");
            if (courses != null && !courses.isEmpty()) {
                sb.append("=== COURSE DETAILS ===\n\n");
                for (Map<String, Object> course : courses) {
                    sb.append("Course: ").append(course.get("title")).append("\n");
                    sb.append("  Status: ").append(course.get("status")).append("\n");
                    sb.append("  Students: ").append(course.get("students")).append("\n");
                    sb.append("  Lessons: ").append(course.get("lessons")).append("\n\n");
                }
            }
            
            analyticsArea.setText(sb.toString());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showCourseAnalytics() {
        List<Course> courses = courseController.getCoursesByInstructor(user.getUserId());
        if (courses.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No courses available", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String[] courseTitles = courses.stream()
                .map(Course::getTitle)
                .toArray(String[]::new);
        
        String selected = (String) JOptionPane.showInputDialog(this,
                "Select a course:", "Course Analytics",
                JOptionPane.QUESTION_MESSAGE, null, courseTitles, courseTitles[0]);
        
        if (selected == null) return;
        
        Course selectedCourse = courses.stream()
                .filter(c -> c.getTitle().equals(selected))
                .findFirst()
                .orElse(null);
        
        if (selectedCourse == null) return;
        
        try {
            Map<String, Object> analytics = analyticsController.getCourseAnalytics(selectedCourse.getCourseId());
            
            StringBuilder sb = new StringBuilder();
            sb.append("=== COURSE ANALYTICS ===\n\n");
            sb.append("Course: ").append(analytics.get("courseTitle")).append("\n");
            sb.append("Total Students: ").append(analytics.get("totalStudents")).append("\n");
            sb.append("Total Lessons: ").append(analytics.get("totalLessons")).append("\n");
            sb.append("Total Quizzes: ").append(analytics.get("totalQuizzes")).append("\n");
            sb.append("Total Quiz Attempts: ").append(analytics.get("totalQuizAttempts")).append("\n");
            sb.append("Pass Rate: ").append(String.format("%.1f", analytics.get("passRate"))).append("%\n");
            sb.append("Average Score: ").append(String.format("%.1f", analytics.get("averageScore"))).append("%\n\n");
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> lessonStats = (List<Map<String, Object>>) analytics.get("lessonStats");
            if (lessonStats != null && !lessonStats.isEmpty()) {
                sb.append("=== LESSON STATISTICS ===\n\n");
                for (Map<String, Object> lesson : lessonStats) {
                    sb.append("Lesson: ").append(lesson.get("lessonTitle")).append("\n");
                    sb.append("  Completed: ").append(lesson.get("completedCount")).append("/")
                            .append(analytics.get("totalStudents")).append(" students\n");
                    sb.append("  Completion Rate: ").append(String.format("%.1f", lesson.get("completionRate"))).append("%\n");
                    
                    if (lesson.containsKey("quizTitle")) {
                        sb.append("  Quiz: ").append(lesson.get("quizTitle")).append("\n");
                        sb.append("  Quiz Attempts: ").append(lesson.get("quizAttempts")).append("\n");
                        sb.append("  Quiz Passed: ").append(lesson.get("quizPassed")).append("\n");
                        sb.append("  Average Score: ").append(String.format("%.1f", lesson.get("quizAverageScore"))).append("%\n");
                    }
                    sb.append("\n");
                }
            }
            
            analyticsArea.setText(sb.toString());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showQuizAnalytics() {
        List<Course> courses = courseController.getCoursesByInstructor(user.getUserId());
        if (courses.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No courses available", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String[] courseTitles = courses.stream()
                .map(Course::getTitle)
                .toArray(String[]::new);
        
        String selectedCourse = (String) JOptionPane.showInputDialog(this,
                "Select a course:", "Quiz Analytics",
                JOptionPane.QUESTION_MESSAGE, null, courseTitles, courseTitles[0]);
        
        if (selectedCourse == null) return;
        
        Course course = courses.stream()
                .filter(c -> c.getTitle().equals(selectedCourse))
                .findFirst()
                .orElse(null);
        
        if (course == null) return;
        
        List<String> lessonTitles = new java.util.ArrayList<>();
        List<String> lessonIds = new java.util.ArrayList<>();
        for (model.Lesson lesson : course.getLessons()) {
            if (lesson.getQuizzes() != null && !lesson.getQuizzes().isEmpty()) {
                lessonTitles.add(lesson.getTitle());
                lessonIds.add(lesson.getLessonId());
            }
        }
        
        if (lessonTitles.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No quizzes found in this course", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String selectedLesson = (String) JOptionPane.showInputDialog(this,
                "Select a lesson with quiz:", "Quiz Analytics",
                JOptionPane.QUESTION_MESSAGE, null, lessonTitles.toArray(), lessonTitles.get(0));
        
        if (selectedLesson == null) return;
        
        int index = lessonTitles.indexOf(selectedLesson);
        if (index < 0) return;
        
        try {
            Map<String, Object> analytics = analyticsController.getQuizAnalytics(
                    course.getCourseId(), lessonIds.get(index));
            
            StringBuilder sb = new StringBuilder();
            sb.append("=== QUIZ ANALYTICS ===\n\n");
            sb.append("Quiz: ").append(analytics.get("quizTitle")).append("\n");
            sb.append("Total Attempts: ").append(analytics.get("totalAttempts")).append("\n");
            sb.append("Total Questions: ").append(analytics.get("totalQuestions")).append("\n");
            sb.append("Passing Score: ").append(analytics.get("passingScore")).append("%\n\n");
            
            sb.append("Passed: ").append(analytics.get("passedCount")).append("\n");
            sb.append("Failed: ").append(analytics.get("failedCount")).append("\n");
            sb.append("Pass Rate: ").append(String.format("%.1f", analytics.get("passRate"))).append("%\n\n");
            
            sb.append("Average Score: ").append(String.format("%.1f", analytics.get("averageScore"))).append("%\n");
            sb.append("Min Score: ").append(String.format("%.1f", analytics.get("minScore"))).append("%\n");
            sb.append("Max Score: ").append(String.format("%.1f", analytics.get("maxScore"))).append("%\n\n");
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> studentAttempts = (List<Map<String, Object>>) analytics.get("studentAttempts");
            if (studentAttempts != null && !studentAttempts.isEmpty()) {
                sb.append("=== STUDENT ATTEMPTS ===\n\n");
                for (Map<String, Object> attempt : studentAttempts) {
                    sb.append("Student: ").append(attempt.get("studentName")).append("\n");
                    sb.append("  Attempt #").append(attempt.get("attemptNumber")).append("\n");
                    sb.append("  Score: ").append(String.format("%.1f", attempt.get("score"))).append("%\n");
                    sb.append("  Status: ").append(attempt.get("passed").equals(true) ? "PASSED" : "FAILED").append("\n\n");
                }
            }
            
            analyticsArea.setText(sb.toString());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAdminCourseAnalytics() {
        List<Course> courses = courseController.getAllCourses();
        if (courses.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No courses available", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String[] courseTitles = courses.stream()
                .map(Course::getTitle)
                .toArray(String[]::new);
        
        String selected = (String) JOptionPane.showInputDialog(this,
                "Select a course:", "Course Analytics",
                JOptionPane.QUESTION_MESSAGE, null, courseTitles, courseTitles[0]);
        
        if (selected == null) return;
        
        Course selectedCourse = courses.stream()
                .filter(c -> c.getTitle().equals(selected))
                .findFirst()
                .orElse(null);
        
        if (selectedCourse == null) return;
        
        try {
            Map<String, Object> analytics = analyticsController.getCourseAnalytics(selectedCourse.getCourseId());
            
            StringBuilder sb = new StringBuilder();
            sb.append("=== COURSE ANALYTICS ===\n\n");
            sb.append("Course: ").append(analytics.get("courseTitle")).append("\n");
            sb.append("Total Students: ").append(analytics.get("totalStudents")).append("\n");
            sb.append("Total Lessons: ").append(analytics.get("totalLessons")).append("\n");
            sb.append("Total Quizzes: ").append(analytics.get("totalQuizzes")).append("\n");
            sb.append("Total Quiz Attempts: ").append(analytics.get("totalQuizAttempts")).append("\n");
            sb.append("Pass Rate: ").append(String.format("%.1f", analytics.get("passRate"))).append("%\n");
            sb.append("Average Score: ").append(String.format("%.1f", analytics.get("averageScore"))).append("%\n\n");
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> lessonStats = (List<Map<String, Object>>) analytics.get("lessonStats");
            if (lessonStats != null && !lessonStats.isEmpty()) {
                sb.append("=== LESSON STATISTICS ===\n\n");
                for (Map<String, Object> lesson : lessonStats) {
                    sb.append("Lesson: ").append(lesson.get("lessonTitle")).append("\n");
                    sb.append("  Completed: ").append(lesson.get("completedCount")).append("/")
                            .append(analytics.get("totalStudents")).append(" students\n");
                    sb.append("  Completion Rate: ").append(String.format("%.1f", lesson.get("completionRate"))).append("%\n");
                    
                    if (lesson.containsKey("quizTitle")) {
                        sb.append("  Quiz: ").append(lesson.get("quizTitle")).append("\n");
                        sb.append("  Quiz Attempts: ").append(lesson.get("quizAttempts")).append("\n");
                        sb.append("  Quiz Passed: ").append(lesson.get("quizPassed")).append("\n");
                        sb.append("  Average Score: ").append(String.format("%.1f", lesson.get("quizAverageScore"))).append("%\n");
                    }
                    sb.append("\n");
                }
            }
            
            analyticsArea.setText(sb.toString());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showAdminQuizAnalytics() {
        List<Course> courses = courseController.getAllCourses();
        if (courses.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No courses available", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String[] courseTitles = courses.stream()
                .map(Course::getTitle)
                .toArray(String[]::new);
        
        String selectedCourse = (String) JOptionPane.showInputDialog(this,
                "Select a course:", "Quiz Analytics",
                JOptionPane.QUESTION_MESSAGE, null, courseTitles, courseTitles[0]);
        
        if (selectedCourse == null) return;
        
        Course course = courses.stream()
                .filter(c -> c.getTitle().equals(selectedCourse))
                .findFirst()
                .orElse(null);
        
        if (course == null) return;
        
        List<String> lessonTitles = new java.util.ArrayList<>();
        List<String> lessonIds = new java.util.ArrayList<>();
        for (model.Lesson lesson : course.getLessons()) {
            if (lesson.getQuizzes() != null && !lesson.getQuizzes().isEmpty()) {
                lessonTitles.add(lesson.getTitle());
                lessonIds.add(lesson.getLessonId());
            }
        }
        
        if (lessonTitles.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No quizzes found in this course", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String selectedLesson = (String) JOptionPane.showInputDialog(this,
                "Select a lesson with quiz:", "Quiz Analytics",
                JOptionPane.QUESTION_MESSAGE, null, lessonTitles.toArray(), lessonTitles.get(0));
        
        if (selectedLesson == null) return;
        
        int index = lessonTitles.indexOf(selectedLesson);
        if (index < 0) return;
        
        try {
            Map<String, Object> analytics = analyticsController.getQuizAnalytics(
                    course.getCourseId(), lessonIds.get(index));
            
            StringBuilder sb = new StringBuilder();
            sb.append("=== QUIZ ANALYTICS ===\n\n");
            sb.append("Quiz: ").append(analytics.get("quizTitle")).append("\n");
            sb.append("Total Attempts: ").append(analytics.get("totalAttempts")).append("\n");
            sb.append("Total Questions: ").append(analytics.get("totalQuestions")).append("\n");
            sb.append("Passing Score: ").append(analytics.get("passingScore")).append("%\n\n");
            
            sb.append("Passed: ").append(analytics.get("passedCount")).append("\n");
            sb.append("Failed: ").append(analytics.get("failedCount")).append("\n");
            sb.append("Pass Rate: ").append(String.format("%.1f", analytics.get("passRate"))).append("%\n\n");
            
            sb.append("Average Score: ").append(String.format("%.1f", analytics.get("averageScore"))).append("%\n");
            sb.append("Min Score: ").append(String.format("%.1f", analytics.get("minScore"))).append("%\n");
            sb.append("Max Score: ").append(String.format("%.1f", analytics.get("maxScore"))).append("%\n\n");
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> studentAttempts = (List<Map<String, Object>>) analytics.get("studentAttempts");
            if (studentAttempts != null && !studentAttempts.isEmpty()) {
                sb.append("=== STUDENT ATTEMPTS ===\n\n");
                for (Map<String, Object> attempt : studentAttempts) {
                    sb.append("Student: ").append(attempt.get("studentName")).append("\n");
                    sb.append("  Attempt #").append(attempt.get("attemptNumber")).append("\n");
                    sb.append("  Score: ").append(String.format("%.1f", attempt.get("score"))).append("%\n");
                    sb.append("  Status: ").append(attempt.get("passed").equals(true) ? "PASSED" : "FAILED").append("\n\n");
                }
            }
            
            analyticsArea.setText(sb.toString());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showAdminInstructorAnalytics() {
        List<User> allUsers = new java.util.ArrayList<>();
        try {
            allUsers = userDAO.loadAll().stream()
                    .filter(u -> "instructor".equalsIgnoreCase(u.getRole()))
                    .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading instructors: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (allUsers.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No instructors found", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String[] instructorNames = allUsers.stream()
                .map(u -> u.getUsername() + " (" + u.getEmail() + ")")
                .toArray(String[]::new);
        
        String selected = (String) JOptionPane.showInputDialog(this,
                "Select an instructor:", "Instructor Analytics",
                JOptionPane.QUESTION_MESSAGE, null, instructorNames, instructorNames[0]);
        
        if (selected == null) return;
        
        int index = java.util.Arrays.asList(instructorNames).indexOf(selected);
        if (index < 0) return;
        
        User selectedInstructor = allUsers.get(index);
        
        try {
            Map<String, Object> analytics = analyticsController.getInstructorAnalytics(selectedInstructor.getUserId());
            
            StringBuilder sb = new StringBuilder();
            sb.append("=== INSTRUCTOR ANALYTICS ===\n\n");
            sb.append("Instructor: ").append(selectedInstructor.getUsername()).append("\n");
            sb.append("Email: ").append(selectedInstructor.getEmail()).append("\n\n");
            sb.append("Total Courses: ").append(analytics.get("totalCourses")).append("\n");
            sb.append("Approved Courses: ").append(analytics.get("approvedCourses")).append("\n");
            sb.append("Pending Courses: ").append(analytics.get("pendingCourses")).append("\n");
            sb.append("Total Students: ").append(analytics.get("totalStudents")).append("\n");
            sb.append("Total Lessons: ").append(analytics.get("totalLessons")).append("\n");
            sb.append("Total Quizzes: ").append(analytics.get("totalQuizzes")).append("\n\n");
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> courses = (List<Map<String, Object>>) analytics.get("courses");
            if (courses != null && !courses.isEmpty()) {
                sb.append("=== COURSE DETAILS ===\n\n");
                for (Map<String, Object> course : courses) {
                    sb.append("Course: ").append(course.get("title")).append("\n");
                    sb.append("  Status: ").append(course.get("status")).append("\n");
                    sb.append("  Students: ").append(course.get("students")).append("\n");
                    sb.append("  Lessons: ").append(course.get("lessons")).append("\n\n");
                }
            }
            
            analyticsArea.setText(sb.toString());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
