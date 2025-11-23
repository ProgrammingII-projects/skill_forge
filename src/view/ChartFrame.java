package view;

import controller.AnalyticsController;
import controller.CourseController;
import model.Course;
import model.User;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import view.Instructor.InstructorDashboardFrame;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class ChartFrame extends JFrame {
    private User user;
    private AnalyticsController analyticsController;
    private CourseController courseController;
    private InstructorDashboardFrame dashboardFrame;
    private JComboBox<String> courseComboBox;
    private JPanel chartPanel;
    private List<Course> courses;

    public ChartFrame(User user, AnalyticsController analyticsController, CourseController courseController, 
                     InstructorDashboardFrame dashboardFrame) {
        this.user = user;
        this.analyticsController = analyticsController;
        this.courseController = courseController;
        this.dashboardFrame = dashboardFrame;
        
        setTitle("Insights - " + user.getUsername());
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Select Course:"));
        
        courses = courseController.getCoursesByInstructor(user.getUserId());
        String[] courseTitles = courses.stream()
                .map(Course::getTitle)
                .toArray(String[]::new);
        
        courseComboBox = new JComboBox<>(courseTitles);
        courseComboBox.addActionListener(e -> updateCharts());
        topPanel.add(courseComboBox);
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> updateCharts());
        topPanel.add(refreshButton);
        
        JButton backButton = new JButton("Back to Dashboard");
        backButton.addActionListener(e -> goBackToDashboard());
        topPanel.add(backButton);
        
        add(topPanel, BorderLayout.NORTH);
        
        chartPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        chartPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(new JScrollPane(chartPanel), BorderLayout.CENTER);
        
        if (courses.size() > 0) {
            updateCharts();
        } else {
            JOptionPane.showMessageDialog(this, "No courses available", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void goBackToDashboard() {
        if (dashboardFrame != null) {
            dashboardFrame.refreshCourseList();
            dashboardFrame.setVisible(true);
        }
        dispose();
    }
    
    private void updateCharts() {
        int selectedIndex = courseComboBox.getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= courses.size()) {
            return;
        }
        
        Course selectedCourse = courses.get(selectedIndex);
        
        try {
            Map<String, Object> analytics = analyticsController.getCourseAnalytics(selectedCourse.getCourseId());
            
            chartPanel.removeAll();
            
            ChartPanel studentPerformanceChart = createStudentPerformanceChart(analytics);
            ChartPanel quizAveragesChart = createQuizAveragesChart(analytics);
            ChartPanel completionChart = createCompletionChart(analytics);
            ChartPanel passRateChart = createPassRateChart(analytics);
            
            chartPanel.add(studentPerformanceChart);
            chartPanel.add(quizAveragesChart);
            chartPanel.add(completionChart);
            chartPanel.add(passRateChart);
            
            chartPanel.revalidate();
            chartPanel.repaint();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading analytics: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private ChartPanel createStudentPerformanceChart(Map<String, Object> analytics) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> lessonStats = (List<Map<String, Object>>) analytics.get("lessonStats");
        
        if (lessonStats != null) {
            for (Map<String, Object> lesson : lessonStats) {
                if (lesson.containsKey("quizAverageScore")) {
                    String lessonTitle = (String) lesson.get("lessonTitle");
                    double avgScore = ((Number) lesson.get("quizAverageScore")).doubleValue();
                    dataset.addValue(avgScore, "Average Score", lessonTitle);
                }
            }
        }
        
        JFreeChart chart = ChartFactory.createBarChart(
                "Quiz Averages by Lesson",
                "Lesson",
                "Average Score (%)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(550, 350));
        return chartPanel;
    }
    
    private ChartPanel createQuizAveragesChart(Map<String, Object> analytics) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> lessonStats = (List<Map<String, Object>>) analytics.get("lessonStats");
        
        if (lessonStats != null) {
            for (Map<String, Object> lesson : lessonStats) {
                if (lesson.containsKey("quizAverageScore")) {
                    String lessonTitle = (String) lesson.get("lessonTitle");
                    double avgScore = ((Number) lesson.get("quizAverageScore")).doubleValue();
                    dataset.addValue(avgScore, "Score", lessonTitle);
                }
            }
        }
        
        JFreeChart chart = ChartFactory.createLineChart(
                "Quiz Averages Trend",
                "Lesson",
                "Average Score (%)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(550, 350));
        return chartPanel;
    }
    
    private ChartPanel createCompletionChart(Map<String, Object> analytics) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> lessonStats = (List<Map<String, Object>>) analytics.get("lessonStats");
        int totalStudents = ((Number) analytics.get("totalStudents")).intValue();
        
        if (lessonStats != null) {
            for (Map<String, Object> lesson : lessonStats) {
                String lessonTitle = (String) lesson.get("lessonTitle");
                int completedCount = ((Number) lesson.get("completedCount")).intValue();
                double completionRate = totalStudents > 0 ? (completedCount * 100.0 / totalStudents) : 0.0;
                dataset.addValue(completionRate, "Completion %", lessonTitle);
            }
        }
        
        JFreeChart chart = ChartFactory.createBarChart(
                "Lesson Completion Percentages",
                "Lesson",
                "Completion Rate (%)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(550, 350));
        return chartPanel;
    }
    
    private ChartPanel createPassRateChart(Map<String, Object> analytics) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> lessonStats = (List<Map<String, Object>>) analytics.get("lessonStats");
        
        int totalPassed = 0;
        int totalFailed = 0;
        
        if (lessonStats != null) {
            for (Map<String, Object> lesson : lessonStats) {
                if (lesson.containsKey("quizPassed") && lesson.containsKey("quizAttempts")) {
                    int passed = ((Number) lesson.get("quizPassed")).intValue();
                    int attempts = ((Number) lesson.get("quizAttempts")).intValue();
                    totalPassed += passed;
                    totalFailed += (attempts - passed);
                }
            }
        }
        
        if (totalPassed + totalFailed > 0) {
            dataset.setValue("Passed", totalPassed);
            dataset.setValue("Failed", totalFailed);
        } else {
            dataset.setValue("No Data", 1);
        }
        
        JFreeChart chart = ChartFactory.createPieChart(
                "Overall Quiz Pass/Fail Rate",
                dataset,
                true,
                true,
                false
        );
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(550, 350));
        return chartPanel;
    }
}
