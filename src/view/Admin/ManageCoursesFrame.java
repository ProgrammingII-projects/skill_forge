package view.Admin;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import model.User;
import view.LoginFrame;
import view.Instructor.CourseEditorFrame;
import view.Instructor.LessonEditorFrame;
import model.Course;
import controller.CourseController;
import controller.StudentController;
import controller.LessonController;
import controller.AdminController;
import controller.AnalyticsController;
import controller.AuthController;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

/**
 * Admin Manage Courses View (Frontend Layer)
 * Only interacts with Controllers, not DAOs or Services directly
 */
public class ManageCoursesFrame extends JFrame {
    private User user;
    private CourseController courseController;
    private StudentController studentController;
    private LessonController lessonController;
    private AuthController authController;
    private AdminController adminController;
    private AnalyticsController analyticsController;
    private JList<String> pendingCourseList;
    private JList<String> approvedCourseList;
    private JList<String> disapprovedCourseList;
    private DefaultListModel<String> pendingListModel;
    private DefaultListModel<String> approvedListModel;
    private DefaultListModel<String> disapprovedListModel;
    private JButton approveButton;
    private JButton disapproveButton;
    private JButton deleteButton;
    private JButton viewStudentsButton;
    private List<Course> pendingCourses = new ArrayList<>();
    private List<Course> approvedCourses = new ArrayList<>();
    private List<Course> disapprovedCourses = new ArrayList<>();
    private boolean isUpdatingSelection = false; // Flag to prevent recursive updates

    public ManageCoursesFrame(User u, AuthController authController, CourseController courseController,
                             StudentController studentController, LessonController lessonController, AdminController adminController, AnalyticsController analyticsController) {
        this.user = u;
        this.authController = authController;
        this.courseController = courseController;
        this.studentController = studentController;
        this.lessonController = lessonController;
        this.adminController = adminController;
        this.analyticsController = analyticsController;
        setTitle("Admin - " + u.getUsername());
        setSize(1200, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        // Tiles setup
        int tileWidth = 330;
        int tileHeight = 340;
        int tileSpacing = 30;
        int labelHeight = 25;
        int startY = 20;
        int startX = 30;

        // Pending courses tile
        JLabel pendingTitleLabel = new JLabel("Pending Courses");
        pendingTitleLabel.setBounds(startX, startY, tileWidth, labelHeight);
        pendingTitleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(pendingTitleLabel);

        pendingListModel = new DefaultListModel<>();
        pendingCourseList = new JList<>(pendingListModel);
        pendingCourseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Synchronize selection - clear other lists when this one is selected
        pendingCourseList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && !isUpdatingSelection) {
                    isUpdatingSelection = true;
                    approvedCourseList.clearSelection();
                    disapprovedCourseList.clearSelection();
                    isUpdatingSelection = false;
                }
            }
        });
        JScrollPane pendingScrollPane = new JScrollPane(pendingCourseList);
        pendingScrollPane.setBounds(startX, startY + labelHeight + 10, tileWidth, tileHeight);
        add(pendingScrollPane);

        // Approved courses tile
        int centerX = startX + tileWidth + tileSpacing;
        JLabel approvedTitleLabel = new JLabel("Approved Courses");
        approvedTitleLabel.setBounds(centerX, startY, tileWidth, labelHeight);
        approvedTitleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(approvedTitleLabel);

        approvedListModel = new DefaultListModel<>();
        approvedCourseList = new JList<>(approvedListModel);
        approvedCourseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Synchronize selection - clear other lists when this one is selected
        approvedCourseList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && !isUpdatingSelection) {
                    isUpdatingSelection = true;
                    pendingCourseList.clearSelection();
                    disapprovedCourseList.clearSelection();
                    isUpdatingSelection = false;
                }
            }
        });
        JScrollPane approvedScrollPane = new JScrollPane(approvedCourseList);
        approvedScrollPane.setBounds(centerX, startY + labelHeight + 10, tileWidth, tileHeight);
        add(approvedScrollPane);

        // Disapproved courses tile
        int rightX = startX + 2 * (tileWidth + tileSpacing);
        JLabel disapprovedTitleLabel = new JLabel("Disapproved Courses");
        disapprovedTitleLabel.setBounds(rightX, startY, tileWidth, labelHeight);
        disapprovedTitleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(disapprovedTitleLabel);

        disapprovedListModel = new DefaultListModel<>();
        disapprovedCourseList = new JList<>(disapprovedListModel);
        disapprovedCourseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Synchronize selection - clear other lists when this one is selected
        disapprovedCourseList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && !isUpdatingSelection) {
                    isUpdatingSelection = true;
                    pendingCourseList.clearSelection();
                    approvedCourseList.clearSelection();
                    isUpdatingSelection = false;
                }
            }
        });
        JScrollPane disapprovedScrollPane = new JScrollPane(disapprovedCourseList);
        disapprovedScrollPane.setBounds(rightX, startY + labelHeight + 10, tileWidth, tileHeight);
        add(disapprovedScrollPane);

        // Approve & Disapprove buttons for pending
        approveButton = new JButton("Approve");
        approveButton.setBounds(startX, startY + tileHeight + labelHeight + 35, 120, 35);
        approveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                approveSelectedPendingCourse();
            }
        });
        add(approveButton);

        disapproveButton = new JButton("Disapprove");
        disapproveButton.setBounds(startX + 140, startY + tileHeight + labelHeight + 35, 120, 35);
        disapproveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                disapproveSelectedPendingCourse();
            }
        });
        add(disapproveButton);

        // Delete button for all (works on any selected)
        deleteButton = new JButton("Delete Course");
        deleteButton.setBounds(centerX+120, startY + tileHeight + labelHeight + 35, 180, 35);
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedCourse();
            }
        });
        add(deleteButton);

        // View Enrolled Students for approved
        viewStudentsButton = new JButton("View Enrolled Students");
        viewStudentsButton.setBounds(rightX, startY + tileHeight + labelHeight + 35, 180, 35);
        viewStudentsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewEnrolledStudentsForApproved();
            }
        });
        add(viewStudentsButton);

        // Review Course Details button (works on any selected course)
        JButton reviewButton = new JButton("Review Course Details");
        reviewButton.setBounds(startX + 270, startY + tileHeight + labelHeight + 35, 180, 35);
        reviewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reviewCourseDetails();
            }
        });
        add(reviewButton);

        JButton backButton = new JButton("Back");

        int backButtonWidth = 100;
        int backButtonHeight = 35;
        int backButtonY = startY + tileHeight + labelHeight + 35 + 50;
        backButton.setBounds(startX, backButtonY, backButtonWidth, backButtonHeight);
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                AdminDashboardFrame adminDashboardFrame = new AdminDashboardFrame(user, authController, courseController, studentController, lessonController, adminController, null);
                adminDashboardFrame.setVisible(true);
            }
        });
        add(backButton);

        refreshCourseLists();
    }

    private void refreshCourseLists() {
        List<Course> allCourses = courseController.getAllCourses();
        pendingCourses.clear();
        approvedCourses.clear();
        disapprovedCourses.clear();
        pendingListModel.clear();
        approvedListModel.clear();
        disapprovedListModel.clear();

        // Fixed: Use "getApproveStatus" if available, fallback to "pending"
        for (Course c : allCourses) {
            String status = c.getApproveStatus();

            String label = c.getTitle() + " (" + (c.getLessons() != null ? c.getLessons().size() : 0) + " lessons, " + (c.getStudents() != null ? c.getStudents().size() : 0) + " students)";

            if ("pending".equalsIgnoreCase(status)) {
                pendingCourses.add(c);
                pendingListModel.addElement(label);
            } 
            
            else if ("approved".equalsIgnoreCase(status)) {
                approvedCourses.add(c);
                approvedListModel.addElement(label);
            } 
            
            else if ("disapproved".equalsIgnoreCase(status) || "rejected".equalsIgnoreCase(status)) {
                disapprovedCourses.add(c);
                disapprovedListModel.addElement(label);
            }
        }
    }

    private void approveSelectedPendingCourse() {
        int idx = pendingCourseList.getSelectedIndex();
        if (idx < 0) {
            JOptionPane.showMessageDialog(this, "Please select a pending course", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Course course = pendingCourses.get(idx);
        // Assume adminController.reviewCourse(courseId, approve);
        try {
            adminController.approveCourse(course.getCourseId());
            JOptionPane.showMessageDialog(this, "Course approved!");
            refreshCourseLists();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void disapproveSelectedPendingCourse() {
        int idx = pendingCourseList.getSelectedIndex();
        if (idx < 0) {
            JOptionPane.showMessageDialog(this, "Please select a pending course", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Course course = pendingCourses.get(idx);
        try {
            adminController.disapproveCourse(course.getCourseId());
            JOptionPane.showMessageDialog(this, "Course disapproved!");
            refreshCourseLists();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedCourse() {
        int idx = approvedCourseList.getSelectedIndex();
        if (idx >= 0) {
            Course course = approvedCourses.get(idx);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete: " + course.getTitle() + "?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    courseController.deleteCourse(course.getCourseId());
                    refreshCourseLists();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            return;
        }
        idx = pendingCourseList.getSelectedIndex();
        if (idx >= 0) {
            Course course = pendingCourses.get(idx);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete: " + course.getTitle() + "?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    courseController.deleteCourse(course.getCourseId());
                    refreshCourseLists();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            return;
        }
        idx = disapprovedCourseList.getSelectedIndex();
        if (idx >= 0) {
            Course course = disapprovedCourses.get(idx);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete: " + course.getTitle() + "?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    courseController.deleteCourse(course.getCourseId());
                    refreshCourseLists();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            return;
        }
        JOptionPane.showMessageDialog(this, "Please select a course to delete", "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void viewEnrolledStudentsForApproved() {
        int idx = approvedCourseList.getSelectedIndex();
        if (idx < 0) {
            JOptionPane.showMessageDialog(this, "Please select an approved course", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Course course = approvedCourses.get(idx);
        List<String> studentIds = course.getStudents();

        if (studentIds.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No students enrolled in: " + course.getTitle(), "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder("Enrolled Students in: " + course.getTitle() + "\n\n");
        sb.append("Total: ").append(studentIds.size()).append(" student(s)\n\n");

        for (String studentId : studentIds) {
            try {
                User student = studentController.getUserById(studentId);
                List<String> completed = student.getProgress().getOrDefault(course.getCourseId(), new ArrayList<>());
                int total = course.getLessons().size();
                int done = completed.size();
                double percentage = total > 0 ? (done * 100.0 / total) : 0;
                sb.append("- ").append(student.getUsername())
                  .append(" (").append(student.getEmail()).append(")")
                  .append(" - Progress: ").append(done).append("/").append(total)
                  .append(" (").append(String.format("%.1f", percentage)).append("%)\n");
            } catch (Exception ex) {
                sb.append("- Unknown student (ID: ").append(studentId).append(") - ").append(ex.getMessage()).append("\n");
            }
        }

        JOptionPane.showMessageDialog(this, sb.toString(), "Enrolled Students", JOptionPane.INFORMATION_MESSAGE);
    }

    private void reviewCourseDetails() {
        Course selectedCourse = null;
        int idx = -1;
        
        // Check which list has a selection
        idx = pendingCourseList.getSelectedIndex();
        if (idx >= 0) {
            selectedCourse = pendingCourses.get(idx);
        } else {
            idx = approvedCourseList.getSelectedIndex();
            if (idx >= 0) {
                selectedCourse = approvedCourses.get(idx);
            } else {
                idx = disapprovedCourseList.getSelectedIndex();
                if (idx >= 0) {
                    selectedCourse = disapprovedCourses.get(idx);
                }
            }
        }
        
        if (selectedCourse == null) {
            JOptionPane.showMessageDialog(this, "Please select a course to review", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Get instructor name
        String instructorName = "Unknown";
        try {
            User instructor = studentController.getUserById(selectedCourse.getInstructorId());
            instructorName = instructor.getUsername() + " (" + instructor.getEmail() + ")";
        } catch (Exception ex) {
            // Keep "Unknown"
        }
        
        // Build course details string
        StringBuilder details = new StringBuilder();
        details.append("=== COURSE DETAILS ===\n\n");
        details.append("Title: ").append(selectedCourse.getTitle()).append("\n");
        details.append("Description: ").append(selectedCourse.getDescription()).append("\n");
        details.append("Status: ").append(selectedCourse.getApproveStatus().toUpperCase()).append("\n");
        details.append("Instructor: ").append(instructorName).append("\n");
        details.append("Course ID: ").append(selectedCourse.getCourseId()).append("\n\n");
        
        details.append("=== STATISTICS ===\n");
        details.append("Total Lessons: ").append(selectedCourse.getLessons() != null ? selectedCourse.getLessons().size() : 0).append("\n");
        details.append("Enrolled Students: ").append(selectedCourse.getStudents() != null ? selectedCourse.getStudents().size() : 0).append("\n\n");
        
        // List lessons
        if (selectedCourse.getLessons() != null && !selectedCourse.getLessons().isEmpty()) {
            details.append("=== LESSONS ===\n");
            int lessonNum = 1;
            for (model.Lesson lesson : selectedCourse.getLessons()) {
                details.append(lessonNum).append(". ").append(lesson.getTitle()).append("\n");
                details.append("   Lesson ID: ").append(lesson.getLessonId()).append("\n");
                
                // Check if lesson has quiz
                if (lesson.getQuizzes() != null && !lesson.getQuizzes().isEmpty()) {
                    model.Quiz quiz = lesson.getQuizzes().get(0);
                    details.append("   Quiz: ").append(quiz.getTitle()).append("\n");
                    details.append("   Quiz Questions: ").append(quiz.getQuestions().size()).append("\n");
                    details.append("   Passing Score: ").append(String.format("%.1f", quiz.getPassScorePercent())).append("%\n");
                    details.append("   Max Retries: ").append(quiz.getMaxRetries()).append("\n");
                    
                    // Quiz attempts count
                    if (lesson.getAttempts() != null) {
                        long quizAttempts = lesson.getAttempts().stream()
                                .filter(a -> a.getQuizId().equals(quiz.getQuizId()))
                                .count();
                        details.append("   Total Quiz Attempts: ").append(quizAttempts).append("\n");
                    }
                } else {
                    details.append("   Quiz: None\n");
                }
                details.append("\n");
                lessonNum++;
            }
        } else {
            details.append("=== LESSONS ===\n");
            details.append("No lessons added yet.\n\n");
        }
        
        // List enrolled students
        if (selectedCourse.getStudents() != null && !selectedCourse.getStudents().isEmpty()) {
            details.append("=== ENROLLED STUDENTS ===\n");
            for (String studentId : selectedCourse.getStudents()) {
                try {
                    User student = studentController.getUserById(studentId);
                    List<String> completed = student.getProgress().getOrDefault(selectedCourse.getCourseId(), new ArrayList<>());
                    int total = selectedCourse.getLessons().size();
                    int done = completed.size();
                    double percentage = total > 0 ? (done * 100.0 / total) : 0;
                    details.append("- ").append(student.getUsername())
                          .append(" (").append(student.getEmail()).append(")")
                          .append(" - Progress: ").append(done).append("/").append(total)
                          .append(" (").append(String.format("%.1f", percentage)).append("%)\n");
                } catch (Exception ex) {
                    details.append("- Unknown student (ID: ").append(studentId).append(")\n");
                }
            }
        } else {
            details.append("=== ENROLLED STUDENTS ===\n");
            details.append("No students enrolled yet.\n");
        }
        
        // Display in a scrollable dialog
        JTextArea textArea = new JTextArea(details.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 500));
        
        JOptionPane.showMessageDialog(this, scrollPane, 
                "Course Details: " + selectedCourse.getTitle(), 
                JOptionPane.INFORMATION_MESSAGE);
    }
}

