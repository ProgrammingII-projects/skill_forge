package view;

import javax.swing.*;
import model.*;
import model.database_manager.CourseModel;
import model.database_manager.UserModel;
import controller.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Optional;

public class StudentDashboardFrame extends JFrame {
    private User user;
    private UserModel userModel;
    private CourseModel courseModel;
    private CourseController courseController;
    private StudentController studentController;
    private JList<String> courseList;
    private DefaultListModel<String> listModel;
    private JButton enrollButton;
    private JButton viewButton;
    private List<Course> allCourses;

    public StudentDashboardFrame(User u, UserModel um, CourseModel cm) {
        this.user = u;
        this.userModel = um;
        this.courseModel = cm;
        this.courseController = new CourseController(courseModel);
        this.studentController = new StudentController(userModel, courseModel);
        
        setTitle("Student - " + u.getUsername());
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        
        JLabel titleLabel = new JLabel("Available Courses");
        titleLabel.setBounds(20, 20, 200, 25);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(titleLabel);
        
        listModel = new DefaultListModel<>();
        courseList = new JList<>(listModel);
        courseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(courseList);
        scrollPane.setBounds(20, 55, 450, 280);
        add(scrollPane);
        
        enrollButton = new JButton("Enroll");
        enrollButton.setBounds(490, 55, 180, 35);
        enrollButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enrollInCourse();
            }
        });
        add(enrollButton);
        
        viewButton = new JButton("View Lessons");
        viewButton.setBounds(490, 100, 180, 35);
        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewLessons();
            }
        });
        add(viewButton);
        
        JButton myCoursesButton = new JButton("My Enrolled Courses");
        myCoursesButton.setBounds(490, 145, 180, 35);
        myCoursesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMyCourses();
            }
        });
        add(myCoursesButton);
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBounds(490, 300, 180, 35);
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
        add(logoutButton);
        
        refreshCourseList();
    }
    
    private void refreshCourseList() {
        allCourses = courseController.getAllCourses();
        listModel.clear();
        for (Course c : allCourses) {
            boolean enrolled = user.getEnrolledCourses().contains(c.getCourseId());
            String status = enrolled ? "[ENROLLED] " : "";
            String progressInfo = "";
            if (enrolled) {
                List<String> completed = user.getProgress().getOrDefault(c.getCourseId(), new java.util.ArrayList<>());
                int total = c.getLessons().size();
                int done = completed.size();
                double percentage = total > 0 ? (done * 100.0 / total) : 0;
                progressInfo = " - Progress: " + done + "/" + total + " (" + String.format("%.1f", percentage) + "%)";
            }
            listModel.addElement(status + c.getTitle() + " by " + getInstructorName(c.getInstructorId()) + 
                " (" + c.getLessons().size() + " lessons)" + progressInfo);
        }
    }
    
    private String getInstructorName(String instructorId) {
        Optional<User> opt = userModel.findById(instructorId);
        return opt.isPresent() ? opt.get().getUsername() : "Unknown";
    }
    
    private void enrollInCourse() {
        int index = courseList.getSelectedIndex();
        if (index < 0) {
            JOptionPane.showMessageDialog(this, "Please select a course", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Course course = allCourses.get(index);
        
        if (user.getEnrolledCourses().contains(course.getCourseId())) {
            JOptionPane.showMessageDialog(this, "You are already enrolled in this course", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        try {
            studentController.enrollStudent(user.getUserId(), course.getCourseId());
            user = userModel.findById(user.getUserId()).get();
            refreshCourseList();
            JOptionPane.showMessageDialog(this, "Successfully enrolled in: " + course.getTitle(), "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void viewLessons() {
        int index = courseList.getSelectedIndex();
        if (index < 0) {
            JOptionPane.showMessageDialog(this, "Please select a course", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Course course = allCourses.get(index);
        
        if (!user.getEnrolledCourses().contains(course.getCourseId())) {
            JOptionPane.showMessageDialog(this, "You must enroll in this course first", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        LessonViewerFrame viewer = new LessonViewerFrame(course.getCourseId(), courseModel, studentController, user.getUserId());
        viewer.setVisible(true);
        viewer.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                user = userModel.findById(user.getUserId()).get();
            }
        });
    }
    
    private void showMyCourses() {
        List<String> enrolledIds = user.getEnrolledCourses();
        if (enrolledIds.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You are not enrolled in any courses", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        StringBuilder sb = new StringBuilder("My Enrolled Courses:\n\n");
        for (String courseId : enrolledIds) {
            Optional<Course> opt = courseController.findById(courseId);
            if (opt.isPresent()) {
                Course c = opt.get();
                List<String> completed = user.getProgress().getOrDefault(courseId, new java.util.ArrayList<>());
                int total = c.getLessons().size();
                int done = completed.size();
                double percentage = total > 0 ? (done * 100.0 / total) : 0;
                sb.append("- ").append(c.getTitle())
                  .append(" - ").append(done).append("/").append(total)
                  .append(" lessons completed (").append(String.format("%.1f", percentage)).append("%)\n");
            }
        }
        JOptionPane.showMessageDialog(this, sb.toString(), "My Courses", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void logout() {
        dispose();
        LoginFrame loginFrame = new LoginFrame(userModel, courseModel);
        loginFrame.setVisible(true);
    }
}
