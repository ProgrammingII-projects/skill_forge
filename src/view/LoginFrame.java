package view;

import model.User;
import view.Admin.AdminDashboardFrame;
import view.Instructor.InstructorDashboardFrame;
import view.Student.StudentDashboardFrame;
import controller.AdminController;
import controller.AuthController;
import controller.CourseController;
import controller.StudentController;
import controller.LessonController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Login View (Frontend Layer)
 * Only interacts with Controllers, not DAOs or Services directly
 */
public class LoginFrame extends JFrame {
    private AuthController authController;
    private CourseController courseController;
    private StudentController studentController;
    private LessonController lessonController;
    private AdminController adminController;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signupButton;

    public LoginFrame(AuthController authController, CourseController courseController, StudentController studentController, LessonController lessonController) {
        this.authController = authController;
        this.courseController = courseController;
        this.studentController = studentController;
        this.lessonController = lessonController;
        veiw();
    }

    public LoginFrame(AuthController authController, CourseController courseController, StudentController studentController, LessonController lessonController,  AdminController adminController ) {
        this.authController = authController;
        this.courseController = courseController;
        this.studentController = studentController;
        this.lessonController = lessonController;
        this.adminController = adminController;
        veiw();
    }



        private void veiw(){
        
        setTitle("Login");
        setSize(400, 220);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        
        JLabel titleLabel = new JLabel("Login");
        titleLabel.setBounds(30, 20, 330, 25);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel);
        
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(30, 60, 100, 25);
        add(emailLabel);
        
        emailField = new JTextField();
        emailField.setBounds(130, 60, 230, 25);
        add(emailField);
        
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(30, 95, 100, 25);
        add(passwordLabel);
        
        passwordField = new JPasswordField();
        passwordField.setBounds(130, 95, 230, 25);
        add(passwordField);
        
        loginButton = new JButton("Login");
        loginButton.setBounds(130, 135, 100, 30);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
        add(loginButton);
        
        signupButton = new JButton("Signup");
        signupButton.setBounds(240, 135, 100, 30);
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openSignup();
            }
        });
        add(signupButton);
    }
    
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            User user = authController.login(email, password);
            
            dispose();
            
            if ("instructor".equalsIgnoreCase(user.getRole())) {
                new InstructorDashboardFrame(user, authController, courseController, studentController, lessonController).setVisible(true);
            } 
            else if("student".equalsIgnoreCase(user.getRole())) {
                new StudentDashboardFrame(user, authController, courseController, studentController, lessonController).setVisible(true);
            }
            else {
                new AdminDashboardFrame(user, authController, courseController, studentController, lessonController, adminController).setVisible(true);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void openSignup() {
        SignupFrame signupFrame = new SignupFrame(authController, courseController, studentController, lessonController);
        signupFrame.setVisible(true);
    }
}
