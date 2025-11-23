package view;

import model.Question;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Reusable dialog for adding/editing quiz questions
 * Can be used from any view that needs to create or edit questions
 */
public class QuestionDialog extends JDialog {
    private JTextField questionTextField;
    private JTextArea optionsArea;
    private JComboBox<String> correctAnswerCombo;
    private boolean saved = false;
    private Question question;
    private Question existingQuestion;

    public QuestionDialog(JFrame parent, Question existingQuestion) {
        super(parent, existingQuestion == null ? "Add Question" : "Edit Question", true);
        this.existingQuestion = existingQuestion;
        setSize(500, 450);
        setLocationRelativeTo(parent);
        setLayout(null);
        
        initializeUI();
        populateFields();
    }
    
    private void initializeUI() {
        // Question Text
        JLabel questionLabel = new JLabel("Question Text:");
        questionLabel.setBounds(20, 20, 120, 25);
        add(questionLabel);
        
        questionTextField = new JTextField();
        questionTextField.setBounds(20, 50, 450, 25);
        add(questionTextField);
        
        // Options
        JLabel optionsLabel = new JLabel("Options (one per line):");
        optionsLabel.setBounds(20, 90, 200, 25);
        add(optionsLabel);
        
        optionsArea = new JTextArea();
        optionsArea.setLineWrap(true);
        optionsArea.setWrapStyleWord(true);
        JScrollPane optionsScroll = new JScrollPane(optionsArea);
        optionsScroll.setBounds(20, 120, 450, 150);
        add(optionsScroll);
        
        // Correct Answer
        JLabel correctLabel = new JLabel("Correct Answer:");
        correctLabel.setBounds(20, 280, 120, 25);
        add(correctLabel);
        
        correctAnswerCombo = new JComboBox<>();
        correctAnswerCombo.setBounds(150, 280, 200, 25);
        add(correctAnswerCombo);
        
        // Buttons
        JButton saveButton = new JButton("Save");
        saveButton.setBounds(200, 320, 100, 30);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveQuestion();
            }
        });
        add(saveButton);
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBounds(310, 320, 100, 30);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        add(cancelButton);
        
        // Add listener to update combo box when options change
        optionsArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateCorrectAnswerOptions();
            }
            
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateCorrectAnswerOptions();
            }
            
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateCorrectAnswerOptions();
            }
        });
    }
    
    private void populateFields() {
        if (existingQuestion != null) {
            questionTextField.setText(existingQuestion.getText());
            
            StringBuilder sb = new StringBuilder();
            for (String option : existingQuestion.getOptions()) {
                sb.append(option).append("\n");
            }
            optionsArea.setText(sb.toString());
            
            updateCorrectAnswerOptions();
            correctAnswerCombo.setSelectedItem(existingQuestion.getCorrectAnswer());
        }
    }
    
    private void updateCorrectAnswerOptions() {
        String[] optionLines = optionsArea.getText().split("\n");
        List<String> options = new ArrayList<>();
        for (String line : optionLines) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                options.add(trimmed);
            }
        }
        
        String currentSelection = (String) correctAnswerCombo.getSelectedItem();
        correctAnswerCombo.removeAllItems();
        
        for (String option : options) {
            correctAnswerCombo.addItem(option);
        }
        
        // Restore selection if it still exists
        if (currentSelection != null && options.contains(currentSelection)) {
            correctAnswerCombo.setSelectedItem(currentSelection);
        } else if (options.size() > 0) {
            correctAnswerCombo.setSelectedIndex(0);
        }
    }
    
    private void saveQuestion() {
        String questionText = questionTextField.getText().trim();
        if (questionText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Question text cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String[] optionLines = optionsArea.getText().split("\n");
        List<String> options = new ArrayList<>();
        for (String line : optionLines) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                options.add(trimmed);
            }
        }
        
        if (options.size() < 2) {
            JOptionPane.showMessageDialog(this, "At least 2 options are required", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Ensure combo box is updated
        updateCorrectAnswerOptions();
        
        if (correctAnswerCombo.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "Please enter at least one option", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String correctAnswer = (String) correctAnswerCombo.getSelectedItem();
        if (correctAnswer == null) {
            JOptionPane.showMessageDialog(this, "Please select a correct answer", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String questionId = existingQuestion != null ? existingQuestion.getQuestionId() : UUID.randomUUID().toString();
        question = new Question(questionId, questionText, options, correctAnswer);
        saved = true;
        dispose();
    }
    
    /**
     * Returns true if the dialog was saved (not cancelled)
     */
    public boolean isSaved() {
        return saved;
    }
    
    /**
     * Returns the question created/edited in the dialog
     * Only valid if isSaved() returns true
     */
    public Question getQuestion() {
        return question;
    }
}
