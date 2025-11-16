package view;

import javax.swing.*;
import model.*;
import model.database_manager.CourseModel;
import model.database_manager.UserModel;
import controller.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Optional;

public class InstructorDashboardFrame extends JFrame {
    private User user;
    private UserModel userModel;
    private CourseModel courseModel;
    private CourseController courseController;
    private JList<String> courseList;
    private DefaultListModel<String> listModel;
    private JButton createButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton manageLessonsButton;
    private List<Course> courses;

    public InstructorDashboardFrame(User u, UserModel um, CourseModel cm) {
        this.user = u;
        this.userModel = um;
        this.courseModel = cm;
        this.courseController = new CourseController(courseModel);
        
        setTitle("Instructor - " + u.getUsername());
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        
        JLabel titleLabel = new JLabel("My Courses");
        titleLabel.setBounds(20, 20, 200, 25);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(titleLabel);
        
        listModel = new DefaultListModel<>();
        courseList = new JList<>(listModel);
        courseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(courseList);
        scrollPane.setBounds(20, 55, 450, 280);
        add(scrollPane);
        
        createButton = new JButton("Create Course");
        createButton.setBounds(490, 55, 180, 35);
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createCourse();
            }
        });
        add(createButton);
        
        editButton = new JButton("Edit Course");
        editButton.setBounds(490, 100, 180, 35);
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editCourse();
            }
        });
        add(editButton);
        
        manageLessonsButton = new JButton("Manage Lessons");
        manageLessonsButton.setBounds(490, 145, 180, 35);
        manageLessonsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                manageLessons();
            }
        });
        add(manageLessonsButton);
        
        deleteButton = new JButton("Delete Course");
        deleteButton.setBounds(490, 190, 180, 35);
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteCourse();
            }
        });
        add(deleteButton);
        
        JButton viewStudentsButton = new JButton("View Enrolled Students");
        viewStudentsButton.setBounds(490, 235, 180, 35);
        viewStudentsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewEnrolledStudents();
            }
        });
        add(viewStudentsButton);
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBounds(490, 300, 180, 35);
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
        add(logoutButton);
        
        refreshCourseList();
    }
    
    private void refreshCourseList() {
        courses = courseController.getCoursesByInstructor(user.getUserId());
        listModel.clear();
        for (Course c : courses) {
            int studentCount = c.getStudents().size();
            listModel.addElement(c.getTitle() + " (" + c.getLessons().size() + " lessons, " + studentCount + " students)");
        }
    }
    
    private void createCourse() {
        CourseEditorFrame editor = new CourseEditorFrame(user.getUserId(), courseModel, true, null);
        editor.setVisible(true);
        editor.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                refreshCourseList();
            }
        });
    }
    
    private void editCourse() {
        int index = courseList.getSelectedIndex();
        if (index < 0) {
            JOptionPane.showMessageDialog(this, "Please select a course", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Course course = courses.get(index);
        CourseEditorFrame editor = new CourseEditorFrame(user.getUserId(), courseModel, false, course);
        editor.setVisible(true);
        editor.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                refreshCourseList();
            }
        });
    }
    
    private void manageLessons() {
        int index = courseList.getSelectedIndex();
        if (index < 0) {
            JOptionPane.showMessageDialog(this, "Please select a course", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Course course = courses.get(index);
        LessonEditorFrame editor = new LessonEditorFrame(course, courseModel);
        editor.setVisible(true);
        editor.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                refreshCourseList();
            }
        });
    }
    
    private void deleteCourse() {
        int index = courseList.getSelectedIndex();
        if (index < 0) {
            JOptionPane.showMessageDialog(this, "Please select a course", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Course course = courses.get(index);
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete: " + course.getTitle() + "?", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            courseController.deleteCourse(course.getCourseId());
            refreshCourseList();
        }
    }
    
    private void viewEnrolledStudents() {
        int index = courseList.getSelectedIndex();
        if (index < 0) {
            JOptionPane.showMessageDialog(this, "Please select a course", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Course course = courses.get(index);
        List<String> studentIds = course.getStudents();
        
        if (studentIds.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No students enrolled in: " + course.getTitle(), "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        StringBuilder sb = new StringBuilder("Enrolled Students in: " + course.getTitle() + "\n\n");
        sb.append("Total: ").append(studentIds.size()).append(" student(s)\n\n");
        
        for (String studentId : studentIds) {
            Optional<User> opt = userModel.findById(studentId);
            if (opt.isPresent()) {
                User student = opt.get();
                List<String> completed = student.getProgress().getOrDefault(course.getCourseId(), new java.util.ArrayList<>());
                int total = course.getLessons().size();
                int done = completed.size();
                double percentage = total > 0 ? (done * 100.0 / total) : 0;
                sb.append("- ").append(student.getUsername())
                  .append(" (").append(student.getEmail()).append(")")
                  .append(" - Progress: ").append(done).append("/").append(total)
                  .append(" (").append(String.format("%.1f", percentage)).append("%)\n");
            } else {
                sb.append("- Unknown student (ID: ").append(studentId).append(")\n");
            }
        }
        
        JOptionPane.showMessageDialog(this, sb.toString(), "Enrolled Students", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void logout() {
        dispose();
        LoginFrame loginFrame = new LoginFrame(userModel, courseModel);
        loginFrame.setVisible(true);
    }
}
