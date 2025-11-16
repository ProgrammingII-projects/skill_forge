import javax.swing.SwingUtilities;
import model.database_manager.UserModel;
import model.database_manager.CourseModel;

public class Main {
    public static void main(String[] args) {
        UserModel userModel = new UserModel("data\\courses.json");
        CourseModel courseModel = new CourseModel("data\\users.json");
    }
}