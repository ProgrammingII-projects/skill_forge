package view;

import controller.CourseController;
import controller.LessonController;
import model.Course;
import model.Lesson;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.UUID;
import java.util.Optional;

/**
 * Lesson Editor View (Frontend Layer)
 * Only interacts with Controllers, not DAOs or Services directly
 */
public class LessonEditorFrame extends JFrame {
    private Course course;
    private CourseController courseController;
    private LessonController lessonController;
    private JList<String> lessonList;
    private DefaultListModel<String> listModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;

    public LessonEditorFrame(Course course, CourseController courseController, LessonController lessonController) {
        this.course = course;
        this.courseController = courseController;
        this.lessonController = lessonController;
        
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
        Optional<Course> opt = courseController.findById(course.getCourseId());
        if (opt.isPresent()) {
            course = opt.get();
        }
        listModel.clear();
        for (Lesson l : course.getLessons()) {
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
            Lesson lesson = new Lesson(lessonId, title.trim(), content.trim());
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
        
        Lesson lesson = course.getLessons().get(index);
        
        String title = JOptionPane.showInputDialog(this, "Enter lesson title:", lesson.getTitle(), JOptionPane.PLAIN_MESSAGE);
        if (title == null || title.trim().isEmpty()) return;
        
        String content = JOptionPane.showInputDialog(this, "Enter lesson content:", lesson.getContent(), JOptionPane.PLAIN_MESSAGE);
        if (content == null) return;
        
        try {
            Lesson updated = new Lesson(lesson.getLessonId(), title.trim(), content.trim());
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
        
        Lesson lesson = course.getLessons().get(index);
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
