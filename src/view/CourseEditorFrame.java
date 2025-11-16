package src.view;

import controller.*;
import model.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CourseEditorFrame extends JFrame {
    private String instructorId;
    private CourseModel courseModel;
    private CourseController courseController;
    private model.Course existingCourse;
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JButton saveButton;
    private JButton cancelButton;

    public CourseEditorFrame(String instructorId, CourseModel cm, boolean isNew, model.Course existing) {
        this.instructorId = instructorId;
        this.courseModel = cm;
        this.courseController = new CourseController(courseModel);
        this.existingCourse = existing;
        
        setTitle(isNew ? "Create Course" : "Edit Course");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        
        JLabel titleLabel = new JLabel("Title:");
        titleLabel.setBounds(20, 20, 100, 25);
        add(titleLabel);
        
        titleField = new JTextField();
        titleField.setBounds(130, 20, 340, 25);
        if (existing != null) titleField.setText(existing.getTitle());
        add(titleField);
        
        JLabel descLabel = new JLabel("Description:");
        descLabel.setBounds(20, 55, 100, 25);
        add(descLabel);
        
        descriptionArea = new JTextArea();
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        if (existing != null) descriptionArea.setText(existing.getDescription());
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setBounds(130, 55, 340, 250);
        add(scrollPane);
        
        saveButton = new JButton("Save");
        saveButton.setBounds(130, 320, 100, 30);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveCourse();
            }
        });
        add(saveButton);
        
        cancelButton = new JButton("Cancel");
        cancelButton.setBounds(240, 320, 100, 30);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        add(cancelButton);
    }
    
    private void saveCourse() {
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title is required", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            if (existingCourse == null) {
                courseController.createCourse(title, description, instructorId);
                JOptionPane.showMessageDialog(this, "Course created successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                existingCourse.setTitle(title);
                existingCourse.setDescription(description);
                courseController.updateCourse(existingCourse);
                JOptionPane.showMessageDialog(this, "Course updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
