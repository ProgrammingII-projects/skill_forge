package view;

import model.User;
import service.AdminService;
import service.AuthService;
import service.CourseService;
import service.LessonService;
import service.QuizService;
import service.StudentService;
import view.Admin.AdminDashboardFrame;
import view.Instructor.InstructorDashboardFrame;
import view.Student.StudentDashboardFrame;
import controller.AdminController;
import controller.AuthController;
import controller.CourseController;
import controller.StudentController;
import dao.AdminDAO;
import dao.CourseDAO;
import dao.QuizDAO;
import dao.UserDAO;
import controller.LessonController;
import controller.QuizController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Login View (Frontend Layer)
 * Only interacts with Controllers, not DAOs or Services directly
 */
public class LoginFrame extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signupButton;

    private UserDAO userDAO = new UserDAO("Lab 7/skill_forge/data/users.json");
    private CourseDAO courseDAO = new CourseDAO("Lab 7/skill_forge/data/courses.json");
    private AdminDAO adminDAO = new AdminDAO("Lab 7/skill_forge/data/users.json");
    // QuizDAO now uses CourseDAO to work with courses.json without overwriting
    private QuizDAO quizDAO = new QuizDAO("Lab 7/skill_forge/data/courses.json", courseDAO);
    
    // Initialize Service Layer (Backend - Business Logic)
    private AuthService authService = new AuthService(userDAO);
    private CourseService courseService = new CourseService(courseDAO, userDAO);
    private StudentService studentService = new StudentService(userDAO, courseDAO);
    private LessonService lessonService = new LessonService(courseDAO);
    private AdminService adminService = new AdminService(courseDAO, adminDAO);
    private QuizService quizService = new QuizService(quizDAO, courseDAO);

    // Initialize Controller Layer (Presentation Logic - Bridge between Frontend and Backend)
    private AuthController authController = new AuthController(authService);
    private CourseController courseController = new CourseController(courseService);
    private StudentController studentController = new StudentController(studentService);
    private LessonController lessonController = new LessonController(lessonService);
    private AdminController adminController = new AdminController(adminService);
    private QuizController quizController = new QuizController(quizService);

    public LoginFrame() {
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
                new InstructorDashboardFrame(user, authController, courseController, 
                                     studentController, lessonController, quizController).setVisible(true);
            } 
            else if("student".equalsIgnoreCase(user.getRole())) {
                new StudentDashboardFrame(user, authController, courseController, studentController, lessonController, quizController).setVisible(true);
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
