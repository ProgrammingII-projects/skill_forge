package view.Student;

import javax.swing.*;
import model.Course;
import model.Lesson;
import model.User;
import controller.AdminController;
import controller.CourseController;
import controller.StudentController;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Optional;

/**
 * Lesson Viewer View (Frontend Layer)
 * Only interacts with Controllers, not DAOs or Services directly
 */
public class ManageUsersFrame extends JFrame {
    private String courseId;
    private CourseController courseController;
    private StudentController studentController;
    private AdminController adminController;
    private String studentId;
    private Course course;
    private User user;
    private JList<String> lessonList;
    private DefaultListModel<String> listModel;
    private JTextArea contentArea;
    private JLabel progressLabel;
    private JButton addUserButton;
    private int currentLessonIndex = -1;

    public ManageUsersFrame(String courseId, CourseController courseController, StudentController studentController, String studentId, AdminController adminController) {
        this.courseId = courseId;
        this.courseController = courseController;
        this.studentController = studentController;
        this.adminController = adminController;
        this.studentId = studentId;
        
        try {
            Optional<Course> opt = courseController.findById(courseId);
            if (!opt.isPresent()) {
                JOptionPane.showMessageDialog(null, "Course not found", "Error", JOptionPane.ERROR_MESSAGE);
                dispose();
                return;
            }
            this.course = opt.get();
            this.user = studentController.getUserById(studentId);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }
        
        setTitle("Users Viewer - ");
        setSize(600, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        
        JLabel titleLabel = new JLabel("Users");
        titleLabel.setBounds(20, 20, 150, 25);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(titleLabel);
        
        listModel = new DefaultListModel<>();
        lessonList = new JList<>(listModel);
        lessonList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lessonList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showLesson();
            }
        });
        JScrollPane listScroll = new JScrollPane(lessonList);
        listScroll.setBounds(20, 50, 250, 200);
        add(listScroll);
        
        progressLabel = new JLabel();
        progressLabel.setBounds(20, 260, 250, 25);
        add(progressLabel);
        
        addUserButton = new JButton("Add User");
        addUserButton.setBounds(20, 295, 250, 30);
        addUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                markComplete();
            }
        });
        add(addUserButton);
        
        JLabel contentLabel = new JLabel("Lesson Content");
        contentLabel.setBounds(290, 20, 280, 25);
        contentLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(contentLabel);
        
        contentArea = new JTextArea();
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        JScrollPane contentScroll = new JScrollPane(contentArea);
        contentScroll.setBounds(290, 50, 280, 275);
        add(contentScroll);
        
        refreshLessonList();
        updateProgress();
    }
    
    private void refreshLessonList() {
        try {
            Optional<Course> opt = .findById(courseId);
            if (opt.isPresent()) {
                course = opt.get();
            }
            user = studentController.getUserById(studentId);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        listModel.clear();
        List<String> completed = user.getProgress().getOrDefault(courseId, new java.util.ArrayList<>());
        
        for (Lesson l : course.getLessons()) {
            String status = completed.contains(l.getLessonId()) ? "✓ " : "";
            listModel.addElement(status + l.getTitle());
        }
    }
    
    private void showLesson() {
        int index = lessonList.getSelectedIndex();
        if (index < 0 || index >= course.getLessons().size()) {
            contentArea.setText("");
            return;
        }
        currentLessonIndex = index;
        Lesson lesson = course.getLessons().get(index);
        contentArea.setText(lesson.getContent());
        updateProgress();
    }
    
    private void updateProgress() {
        try {
            user = studentController.getUserById(studentId);
            List<String> completed = user.getProgress().getOrDefault(courseId, new java.util.ArrayList<>());
            int total = course.getLessons().size();
            int done = completed.size();
            progressLabel.setText("Progress: " + done + "/" + total + " lessons completed");
        } catch (Exception ex) {
            progressLabel.setText("Progress: Unknown");
        }
    }
    
    private void markComplete() {
        if (currentLessonIndex < 0 || currentLessonIndex >= course.getLessons().size()) {
            JOptionPane.showMessageDialog(this, "Please select a lesson", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Lesson lesson = course.getLessons().get(currentLessonIndex);
        try {
            studentController.markLessonCompleted(studentId, courseId, lesson.getLessonId());
            refreshLessonList();
            updateProgress();
            JOptionPane.showMessageDialog(this, "Lesson marked as complete!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
