package view.Admin;

import controller.AdminController;
import controller.AuthController;
import controller.CourseController;
import controller.StudentController;
import model.User;
import controller.LessonController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Signup View (Frontend Layer)
 * Only interacts with Controllers, not DAOs or Services directly
 */
public class AdminAddUserFrame extends JFrame {

    private User user;
    private AuthController authController;
    private CourseController courseController;
    private StudentController studentController;
    private LessonController lessonController;
    private AdminController adminController;
    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JComboBox<String> roleCombo;
    private JButton signupButton;
    private JButton cancelButton;

    public AdminAddUserFrame(User user, AuthController authController, CourseController courseController, StudentController studentController, LessonController lessonController, AdminController adminController) {
        this.user = user;
        this.authController = authController;
        this.courseController = courseController;
        this.studentController = studentController;
        this.lessonController = lessonController;
        this.adminController = adminController;
        
        setTitle("Add User");
        setSize(420, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        
        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setBounds(30, 20, 330, 25);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel);
        
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(30, 60, 100, 25);
        add(usernameLabel);
        
        usernameField = new JTextField();
        usernameField.setBounds(140, 60, 240, 25);
        add(usernameField);
        
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(30, 95, 100, 25);
        add(emailLabel);
        
        emailField = new JTextField();
        emailField.setBounds(140, 95, 240, 25);
        add(emailField);
        
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(30, 130, 100, 25);
        add(passwordLabel);
        
        passwordField = new JPasswordField();
        passwordField.setBounds(140, 130, 240, 25);
        add(passwordField);
        
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setBounds(30, 165, 100, 25);
        add(roleLabel);
        
        roleCombo = new JComboBox<>(new String[]{"student", "instructor" , "admin"});
        roleCombo.setBounds(140, 165, 240, 25);
        add(roleCombo);
        
        signupButton = new JButton("Signup");
        signupButton.setBounds(140, 210, 100, 30);
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSignup();
            }
        });
        add(signupButton);
        
        cancelButton = new JButton("Cancel");
        cancelButton.setBounds(250, 210, 100, 30);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        add(cancelButton);
    }
    
    private void handleSignup() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String role = (String) roleCombo.getSelectedItem();
        
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            authController.signup(username, email, password, role);
            
            JOptionPane.showMessageDialog(this, "User Added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            
            dispose();
            AdminDashboardFrame AdminDashboardFrame = new AdminDashboardFrame(user,authController, courseController, studentController, lessonController, adminController);
            AdminDashboardFrame.setVisible(true);
        
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "user creating Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
