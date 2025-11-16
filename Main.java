import javax.swing.SwingUtilities;
import view.LoginFrame;
import model.database_manager.UserModel;
import model.database_manager.CourseModel;

public class Main {
    public static void main(String[] args) {
        UserModel userModel = new UserModel("data/users.json");
        CourseModel courseModel = new CourseModel("data/courses.json");
        
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame(userModel, courseModel);
            loginFrame.setVisible(true);
        });
    }
}
