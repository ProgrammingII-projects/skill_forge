package view;

import model.*;
import model.database_manager.CourseModel;
import model.database_manager.UserModel;
import controller.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private UserModel userModel;
    private CourseModel courseModel;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signupButton;

    public LoginFrame(UserModel userModel, CourseModel courseModel) {
        this.userModel = userModel;
        this.courseModel = courseModel;
        
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
            AuthController authController = new AuthController(userModel);
            User user = authController.login(email, password);
            
            dispose();
            
            if ("instructor".equalsIgnoreCase(user.getRole())) {
                new InstructorDashboardFrame(user, userModel, courseModel).setVisible(true);
            } else {
                new StudentDashboardFrame(user, userModel, courseModel).setVisible(true);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void openSignup() {
        SignupFrame signupFrame = new SignupFrame(userModel, courseModel);
        signupFrame.setVisible(true);
    }
}
