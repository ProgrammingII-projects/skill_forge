
import javax.swing.SwingUtilities;
import view.LoginFrame;
import dao.UserDAO;
import dao.AdminDAO;
import dao.CourseDAO;
import dao.QuizDAO;
import service.AdminService;
import service.AuthService;
import service.CourseService;
import service.StudentService;
import service.LessonService;
import service.QuizService;
import controller.AdminController;
import controller.AuthController;
import controller.CourseController;
import controller.StudentController;
import controller.LessonController;
import controller.QuizController;

/**
 * Main Application Entry Point
 * Wires all layers together:
 * - DAO Layer (Data Access) - Backend
 * - Service Layer (Business Logic) - Backend
 * - Controller Layer (Presentation Logic) - Bridge between Frontend and Backend
 * - View Layer (UI) - Frontend
 */
public class Main {
    public static void main(String[] args) {
        // Initialize DAO Layer (Backend - Data Access)
        UserDAO userDAO = new UserDAO("Lab 7/skill_forge/data/users.json");
        CourseDAO courseDAO = new CourseDAO("Lab 7/skill_forge/data/courses.json");
        AdminDAO adminDAO = new AdminDAO("Lab 7/skill_forge/data/users.json");
        // QuizDAO now uses CourseDAO to work with courses.json without overwriting
        QuizDAO quizDAO = new QuizDAO("Lab 7/skill_forge/data/courses.json", courseDAO);
        
        // Initialize Service Layer (Backend - Business Logic)
        AuthService authService = new AuthService(userDAO);
        CourseService courseService = new CourseService(courseDAO, userDAO);
        StudentService studentService = new StudentService(userDAO, courseDAO);
        LessonService lessonService = new LessonService(courseDAO);
        AdminService adminService = new AdminService(courseDAO, adminDAO);
        QuizService quizService = new QuizService(quizDAO, courseDAO);

        // Initialize Controller Layer (Presentation Logic - Bridge between Frontend and Backend)
        AuthController authController = new AuthController(authService);
        CourseController courseController = new CourseController(courseService);
        StudentController studentController = new StudentController(studentService);
        LessonController lessonController = new LessonController(lessonService);
        AdminController adminController = new AdminController(adminService);
        QuizController quizController = new QuizController(quizService);

        // Initialize View Layer (Frontend - UI)
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame(authController, courseController, studentController, lessonController, adminController, quizController);
            loginFrame.setVisible(true);
        });
    }
}
