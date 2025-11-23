package view.Admin;

import controller.AdminController;
import controller.AuthController;
import controller.CourseController;
import controller.StudentController;
import model.User;
import controller.LessonController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Edit User View (Admin Frontend Layer)
 * Only interacts with Controllers, not DAOs or Services directly
 */
public class EditUserFrame extends JFrame {
    private AuthController authController;
    private CourseController courseController;
    private StudentController studentController;
    private LessonController lessonController;
    private AdminController adminController;
    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JComboBox<String> roleCombo;
    private JButton saveButton;
    private JButton cancelButton;
    private User user;
    private User selectedUser; // The user to edit
    private Runnable onSaveCallback; // Callback to refresh parent frame after save

    public EditUserFrame(User selectedUser, User user, AuthController authController, CourseController courseController, StudentController studentController, LessonController lessonController, AdminController adminController) {
        this(selectedUser, user, authController, courseController, studentController, lessonController, adminController, null);
    }

    public EditUserFrame(User selectedUser, User user, AuthController authController, CourseController courseController, StudentController studentController, LessonController lessonController, AdminController adminController, Runnable onSaveCallback) {
        this.user = user;
        this.authController = authController;
        this.courseController = courseController;
        this.studentController = studentController;
        this.lessonController = lessonController;
        this.adminController = adminController;
        this.selectedUser = selectedUser;
        this.onSaveCallback = onSaveCallback;
        
        setTitle("Edit User");
        setSize(420, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        
        JLabel titleLabel = new JLabel("Edit User");
        titleLabel.setBounds(30, 20, 330, 25);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel);
        
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(30, 60, 100, 25);
        add(usernameLabel);
        
        usernameField = new JTextField(selectedUser.getUsername());
        usernameField.setBounds(140, 60, 240, 25);
        add(usernameField);
        
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(30, 95, 100, 25);
        add(emailLabel);
        
        emailField = new JTextField(selectedUser.getEmail());
        emailField.setBounds(140, 95, 240, 25);
        add(emailField);
        
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(30, 130, 100, 25);
        add(passwordLabel);
        
        passwordField = new JPasswordField();
        passwordField.setBounds(140, 130, 240, 25);
        add(passwordField);
        
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setBounds(30, 165, 100, 25);
        add(roleLabel);
        
        roleCombo = new JComboBox<>(new String[]{"student", "instructor", "admin"});
        roleCombo.setBounds(140, 165, 240, 25);
        roleCombo.setSelectedItem(selectedUser.getRole());
        add(roleCombo);
        
        saveButton = new JButton("Save");
        saveButton.setBounds(140, 210, 100, 30);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleEditUser();
            }
        });
        add(saveButton);
        
        cancelButton = new JButton("Cancel");
        cancelButton.setBounds(250, 210, 100, 30);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        add(cancelButton);
    }
    
    private void handleEditUser() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String role = (String) roleCombo.getSelectedItem();
        
        if (username.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and email cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
       
        try {
            String passwordToUse = password.isEmpty() ? null : password;
            
            adminController.updateUser(selectedUser.getUserId(), username, email, passwordToUse, role);

            JOptionPane.showMessageDialog(this, "User updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            
            if (onSaveCallback != null) {
                onSaveCallback.run();
            }
            
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to update user: " + ex.getMessage(), "Edit User Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
