package view.Admin;

import javax.swing.*;
import model.User;
import controller.AdminController;
import controller.AuthController;
import controller.CourseController;
import controller.LessonController;
import controller.StudentController;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;  

import controller.AnalyticsController;

/**
 * Manage Users View (Admin Frontend Layer)
 */
public class ManageUsersFrame extends JFrame {
    private AuthController authController;
    private CourseController courseController;
    private StudentController studentController;
    private LessonController lessonController;
    private AdminController adminController;
    private AnalyticsController analyticsController;
    private User user;
    private JList<String> userList;
    private DefaultListModel<String> listModel;
    private JTextArea contentArea;
    private JLabel infoLabel;
    private JButton addUserButton;
    private JButton deleteUserButton;
    private JButton editUserButton;
    private List<User> users;

    public ManageUsersFrame(User user, AuthController authController, CourseController courseController, StudentController studentController, LessonController lessonController, AdminController adminController, AnalyticsController analyticsController) {
        this.user = user;
        this.authController = authController;
        this.courseController = courseController;
        this.studentController = studentController;
        this.lessonController = lessonController;
        this.adminController = adminController; 
        this.analyticsController = analyticsController;
        setTitle("Manage Users");
        setSize(600, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel titleLabel = new JLabel("Users List");
        titleLabel.setBounds(20, 20, 150, 25);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(titleLabel);

        listModel = new DefaultListModel<>();
        userList = new JList<>(listModel);
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showSelectedUser();
            }
        });

        JScrollPane listScroll = new JScrollPane(userList);
        listScroll.setBounds(20, 50, 250, 200);
        add(listScroll);

        infoLabel = new JLabel();
        infoLabel.setBounds(20, 260, 250, 25);
        add(infoLabel);

        addUserButton = new JButton("Add User");
        addUserButton.setBounds(20, 295, 120, 30);
        addUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addUsers();
            }
        });
        add(addUserButton);

        editUserButton = new JButton("Edit User");
        editUserButton.setBounds(150, 295, 120, 30);
        editUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editSelectedUser();
            }
        });
        add(editUserButton);

        deleteUserButton = new JButton("Delete User");
        deleteUserButton.setBounds(20, 335, 250, 30);
        deleteUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedUser();
            }
        });
        add(deleteUserButton);

        contentArea = new JTextArea();
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        JScrollPane contentScroll = new JScrollPane(contentArea);
        contentScroll.setBounds(290, 50, 280, 275);
        add(contentScroll);

        refreshUserList();
    }

    private void refreshUserList() {
        try {
            users = adminController.getAllUsers();
            listModel.clear();
            for (User u : users) {
                listModel.addElement(u.getUsername() + " (" + u.getRole() + ")");
            }
            contentArea.setText("");
            infoLabel.setText("Total users: " + (users != null ? users.size() : 0));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showSelectedUser() {
        int idx = userList.getSelectedIndex();
        if (idx >= 0 && users != null && idx < users.size()) {
            User u = users.get(idx);
            StringBuilder sb = new StringBuilder();
            sb.append("Username: ").append(u.getUsername()).append("\n");
            sb.append("Email: ").append(u.getEmail()).append("\n");
            sb.append("Role: ").append(u.getRole()).append("\n");
            // add more fields if needed
            contentArea.setText(sb.toString());
        } else {
            contentArea.setText("");
        }
    }

    private void addUsers() {
        dispose();
        AdminAddUserFrame addUserFrame = new AdminAddUserFrame(user, authController, courseController, studentController, lessonController, adminController);
        addUserFrame.setVisible(true);
    }

    private void editSelectedUser() {
        int idx = userList.getSelectedIndex();
        if (idx < 0 || users == null || idx >= users.size()) {
            JOptionPane.showMessageDialog(this, "Please select a user to edit.", "Edit User", JOptionPane.WARNING_MESSAGE);
            return;
        }
        User selectedUser = users.get(idx);
        //JOptionPane.showMessageDialog(this, "Edit user functionality not implemented.", "Edit User", JOptionPane.INFORMATION_MESSAGE);
        EditUserFrame EditUserFrame = new EditUserFrame( selectedUser, user,  authController,  courseController,  studentController,  lessonController, adminController, this::refreshUserList);
        EditUserFrame.setVisible(true);
    }

    private void deleteSelectedUser() {
        int idx = userList.getSelectedIndex();
        if (idx < 0 || users == null || idx >= users.size()) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.", "Delete User", JOptionPane.WARNING_MESSAGE);
            return;
        }
        User selectedUser = users.get(idx);

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete user: " + selectedUser.getUsername() + "?", "Delete User", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                adminController.removeUsers(selectedUser.getUserId());
                JOptionPane.showMessageDialog(this, "User deleted successfully.");
                refreshUserList();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Failed to Delete", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
