package view;

import javax.swing.*;
import model.*;
import controller.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Optional;

public class LessonViewerFrame extends JFrame {
    private String courseId;
    private CourseModel courseModel;
    private StudentController studentController;
    private UserModel userModel;
    private String studentId;
    private Course course;
    private JList<String> lessonList;
    private DefaultListModel<String> listModel;
    private JTextArea contentArea;
    private JLabel progressLabel;
    private JButton markCompleteButton;
    private int currentLessonIndex = -1;

    public LessonViewerFrame(String courseId, CourseModel cm, StudentController sc, String studentId) {
        this.courseId = courseId;
        this.courseModel = cm;
        this.studentController = sc;
        this.studentId = studentId;
        
        // Get UserModel from StudentController
        try {
            java.lang.reflect.Field field = StudentController.class.getDeclaredField("userModel");
            field.setAccessible(true);
            this.userModel = (UserModel) field.get(sc);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access UserModel", e);
        }
        
        Optional<Course> opt = courseModel.findById(courseId);
        if (!opt.isPresent()) {
            JOptionPane.showMessageDialog(null, "Course not found", "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }
        this.course = opt.get();
        
        setTitle("Lesson Viewer - " + course.getTitle());
        setSize(600, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        
        JLabel titleLabel = new JLabel("Lessons");
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
        
        markCompleteButton = new JButton("Mark as Complete");
        markCompleteButton.setBounds(20, 295, 250, 30);
        markCompleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                markComplete();
            }
        });
        add(markCompleteButton);
        
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
        course = courseModel.findById(courseId).get();
        listModel.clear();
        Optional<User> userOpt = userModel.findById(studentId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            List<String> completed = user.getProgress().getOrDefault(courseId, new java.util.ArrayList<>());
            
            for (Lesson l : course.getLessons()) {
                String status = completed.contains(l.getLessonId()) ? "✓ " : "";
                listModel.addElement(status + l.getTitle());
            }
        } else {
            for (Lesson l : course.getLessons()) {
                listModel.addElement(l.getTitle());
            }
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
        Optional<User> userOpt = userModel.findById(studentId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            List<String> completed = user.getProgress().getOrDefault(courseId, new java.util.ArrayList<>());
            int total = course.getLessons().size();
            int done = completed.size();
            progressLabel.setText("Progress: " + done + "/" + total + " lessons completed");
        } else {
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
