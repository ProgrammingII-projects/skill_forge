package view.Admin;

import javax.swing.*;
import model.User;
import view.LoginFrame;
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

/**
 * Instructor Dashboard View (Frontend Layer)
 * Only interacts with Controllers, not DAOs or Services directly
 */
public class AdminDashboardFrame extends JFrame {
    private User user;
    private CourseController courseController;
    private StudentController studentController;
    private LessonController lessonController;
    private AuthController authController;
    private AdminController adminController;
    private JList<String> courseList;
    private DefaultListModel<String> listModel;
    private JButton UmangaeButton;
    private JButton CmanageButton;
    private JButton deleteButton;
    private JButton manageLessonsButton;
    private List<Course> courses;

    public AdminDashboardFrame(User u, AuthController authController, CourseController courseController, StudentController studentController, LessonController lessonController, AdminController adminController) {
        this.user = u;
        this.authController = authController;
        this.courseController = courseController;
        this.studentController = studentController;
        this.lessonController = lessonController;
        this.adminController = adminController;
        
        setTitle("Admin - " + u.getUsername()
        );
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        
        JLabel welcomeLabel = new JLabel("Welcome Admin " + (user != null ? user.getUsername() : ""));
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 26));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setBounds(50, 10, 600, 40);
        add(welcomeLabel);
        
        UmangaeButton = new JButton("Manage Users");
        UmangaeButton.setBounds(50, 55, 600, 35);
        UmangaeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                manageUsers();
            }
        });
        add(UmangaeButton);
        
        CmanageButton = new JButton("Manage Courses");
        CmanageButton.setBounds(50, 100, 600, 35);
        CmanageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                manageCourses();
            }
        });
        add(CmanageButton);

        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBounds(50, 300, 600, 35);
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
        add(logoutButton);
        
    }

    

    private void manageUsers() {
        dispose();
        AdminAddUserFrame AdminAddUserFrame = new AdminAddUserFrame( user,  authController,  courseController,  studentController,  lessonController,  adminController);
        AdminAddUserFrame.setVisible(true);
    
    }

    private void manageCourses() {
        dispose();
        ManageCoursesFrame manageUsersFrame = new ManageCoursesFrame( user,  authController,  courseController,  studentController,  lessonController,  adminController);
         manageUsersFrame.setVisible(true);
    
    }


    private void logout() {
        dispose();
        LoginFrame loginFrame = new LoginFrame(authController, courseController, studentController, lessonController, adminController);
        loginFrame.setVisible(true);
    }
}
