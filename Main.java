import javax.swing.SwingUtilities;
import view.LoginFrame;
import model.UserModel;
import model.CourseModel;

public class Main {
    public static void main(String[] args) {
        UserModel userModel = new UserModel("Lab 7/skill_forge/data/users.json");
        CourseModel courseModel = new CourseModel("Lab 7/skill_forge/data/courses.json");

        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame(userModel, courseModel);
            loginFrame.setVisible(true);
        });
    }
}
