package src.view;

import controller.*;
import model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.UUID;

public class LessonEditorFrame extends JFrame {
    private model.Course course;
    private CourseModel courseModel;
    private LessonController lessonController;
    private JList<String> lessonList;
    private DefaultListModel<String> listModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;

    public LessonEditorFrame(model.Course course, CourseModel cm) {
        this.course = course;
        this.courseModel = cm;
        this.lessonController = new LessonController(courseModel);
        
        setTitle("Lessons - " + course.getTitle());
        setSize(600, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        
        JLabel titleLabel = new JLabel("Lessons");
        titleLabel.setBounds(20, 20, 200, 25);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(titleLabel);
        
        listModel = new DefaultListModel<>();
        lessonList = new JList<>(listModel);
        lessonList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(lessonList);
        scrollPane.setBounds(20, 55, 350, 300);
        add(scrollPane);
        
        addButton = new JButton("Add Lesson");
        addButton.setBounds(390, 55, 180, 35);
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addLesson();
            }
        });
        add(addButton);
        
        editButton = new JButton("Edit Lesson");
        editButton.setBounds(390, 100, 180, 35);
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editLesson();
            }
        });
        add(editButton);
        
        deleteButton = new JButton("Delete Lesson");
        deleteButton.setBounds(390, 145, 180, 35);
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteLesson();
            }
        });
        add(deleteButton);
        
        refreshLessonList();
    }
    
    private void refreshLessonList() {
        course = courseModel.findById(course.getCourseId()).get();
        listModel.clear();
        for (model.Lesson l : course.getLessons()) {
            listModel.addElement(l.getTitle());
        }
    }
    
    private void addLesson() {
        String title = JOptionPane.showInputDialog(this, "Enter lesson title:", "Add Lesson", JOptionPane.PLAIN_MESSAGE);
        if (title == null || title.trim().isEmpty()) return;
        
        String content = JOptionPane.showInputDialog(this, "Enter lesson content:", "Add Lesson", JOptionPane.PLAIN_MESSAGE);
        if (content == null) return;
        
        try {
            String lessonId = UUID.randomUUID().toString();
            model.Lesson lesson = new model.Lesson(lessonId, title.trim(), content.trim());
            lessonController.addLesson(course.getCourseId(), lesson);
            refreshLessonList();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void editLesson() {
        int index = lessonList.getSelectedIndex();
        if (index < 0) {
            JOptionPane.showMessageDialog(this, "Please select a lesson", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        model.Lesson lesson = course.getLessons().get(index);
        
        String title = JOptionPane.showInputDialog(this, "Enter lesson title:", lesson.getTitle(), JOptionPane.PLAIN_MESSAGE);
        if (title == null || title.trim().isEmpty()) return;
        
        String content = JOptionPane.showInputDialog(this, "Enter lesson content:", lesson.getContent(), JOptionPane.PLAIN_MESSAGE);
        if (content == null) return;
        
        try {
            model.Lesson updated = new model.Lesson(lesson.getLessonId(), title.trim(), content.trim());
            lessonController.editLesson(course.getCourseId(), updated);
            refreshLessonList();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteLesson() {
        int index = lessonList.getSelectedIndex();
        if (index < 0) {
            JOptionPane.showMessageDialog(this, "Please select a lesson", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        model.Lesson lesson = course.getLessons().get(index);
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete: " + lesson.getTitle() + "?", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                lessonController.deleteLesson(course.getCourseId(), lesson.getLessonId());
                refreshLessonList();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
