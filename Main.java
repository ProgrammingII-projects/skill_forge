
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
       
        // Initialize View Layer (Frontend - UI)
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
