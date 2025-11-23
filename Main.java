
import javax.swing.SwingUtilities;
import view.LoginFrame;


public class Main {
    public static void main(String[] args) {
       
        // Initialize View Layer (Frontend - UI)
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
