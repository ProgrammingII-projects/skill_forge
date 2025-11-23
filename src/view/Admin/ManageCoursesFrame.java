package view.Admin;

import javax.swing.*;
import model.User;
import view.LoginFrame;
import view.Instructor.CourseEditorFrame;
import view.Instructor.LessonEditorFrame;
import model.Course;
import controller.CourseController;
import controller.StudentController;
import controller.LessonController;
import controller.AdminController;
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

    public ManageCoursesFrame(User u, AuthController authController, CourseController courseController,
                             StudentController studentController, LessonController lessonController, AdminController adminController) {
        this.user = u;
        this.authController = authController;
        this.courseController = courseController;
        this.studentController = studentController;
        this.lessonController = lessonController;
        this.adminController = adminController;

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
        deleteButton.setBounds(centerX, startY + tileHeight + labelHeight + 35, 180, 35);
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
                new AdminDashboardFrame(user, authController, courseController, studentController, lessonController, adminController).setVisible(true);
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
}

