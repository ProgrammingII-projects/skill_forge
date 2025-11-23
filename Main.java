
import javax.swing.SwingUtilities;
import view.LoginFrame;


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
