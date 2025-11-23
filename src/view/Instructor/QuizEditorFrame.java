package view.Instructor;

import controller.CourseController;
import controller.QuizController;
import model.Quiz;
import model.Question;
import view.QuestionDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Quiz Editor View (Frontend Layer)
 * Only interacts with Controllers, not DAOs or Services directly
 */
public class QuizEditorFrame extends JFrame {
    private String courseId;
    private String lessonId;
    private CourseController courseController;
    private QuizController quizController;
    private Quiz currentQuiz;
    
    private JTextField titleField;
    private JSpinner passScoreSpinner;
    private JSpinner maxRetriesSpinner;
    private JList<String> questionList;
    private DefaultListModel<String> questionListModel;
    private JButton saveQuizButton;
    private JButton deleteQuizButton;
    private JButton addQuestionButton;
    private JButton editQuestionButton;
    private JButton deleteQuestionButton;

    public QuizEditorFrame(String courseId, String lessonId, 
                          CourseController courseController, QuizController quizController) {
        this.courseId = courseId;
        this.lessonId = lessonId;
        this.courseController = courseController;
        this.quizController = quizController;
        
        setTitle("Quiz Editor");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        
        initializeUI();
        loadQuiz();
    }
    
    private void initializeUI() {
        // Quiz Title
        JLabel titleLabel = new JLabel("Quiz Title:");
        titleLabel.setBounds(20, 20, 100, 25);
        add(titleLabel);
        
        titleField = new JTextField();
        titleField.setBounds(130, 20, 300, 25);
        add(titleField);
        
        // Passing Score
        JLabel passScoreLabel = new JLabel("Passing Score (%):");
        passScoreLabel.setBounds(20, 55, 120, 25);
        add(passScoreLabel);
        
        SpinnerNumberModel passScoreModel = new SpinnerNumberModel(60.0, 0.0, 100.0, 1.0);
        passScoreSpinner = new JSpinner(passScoreModel);
        passScoreSpinner.setBounds(150, 55, 100, 25);
        add(passScoreSpinner);
        
        // Max Retries
        JLabel maxRetriesLabel = new JLabel("Max Retries (-1 = unlimited):");
        maxRetriesLabel.setBounds(20, 90, 180, 25);
        add(maxRetriesLabel);
        
        SpinnerNumberModel retriesModel = new SpinnerNumberModel(-1, -1, 100, 1);
        maxRetriesSpinner = new JSpinner(retriesModel);
        maxRetriesSpinner.setBounds(210, 90, 100, 25);
        add(maxRetriesSpinner);
        
        // Questions List
        JLabel questionsLabel = new JLabel("Questions:");
        questionsLabel.setBounds(20, 130, 200, 25);
        questionsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(questionsLabel);
        
        questionListModel = new DefaultListModel<>();
        questionList = new JList<>(questionListModel);
        questionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane questionScroll = new JScrollPane(questionList);
        questionScroll.setBounds(20, 160, 550, 300);
        add(questionScroll);
        
        // Buttons
        saveQuizButton = new JButton("Save Quiz");
        saveQuizButton.setBounds(590, 20, 180, 35);
        saveQuizButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveQuiz();
            }
        });
        add(saveQuizButton);
        
        deleteQuizButton = new JButton("Delete Quiz");
        deleteQuizButton.setBounds(590, 65, 180, 35);
        deleteQuizButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteQuiz();
            }
        });
        add(deleteQuizButton);
        
        addQuestionButton = new JButton("Add Question");
        addQuestionButton.setBounds(590, 160, 180, 35);
        addQuestionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addQuestion();
            }
        });
        add(addQuestionButton);
        
        editQuestionButton = new JButton("Edit Question");
        editQuestionButton.setBounds(590, 205, 180, 35);
        editQuestionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editQuestion();
            }
        });
        add(editQuestionButton);
        
        deleteQuestionButton = new JButton("Delete Question");
        deleteQuestionButton.setBounds(590, 250, 180, 35);
        deleteQuestionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteQuestion();
            }
        });
        add(deleteQuestionButton);
    }
    
    private void loadQuiz() {
        try {
            currentQuiz = quizController.getQuizForLesson(courseId, lessonId);
            if (currentQuiz != null) {
                titleField.setText(currentQuiz.getTitle());
                passScoreSpinner.setValue(currentQuiz.getPassScorePercent());
                maxRetriesSpinner.setValue(currentQuiz.getMaxRetries());
                refreshQuestionList();
            } else {
                titleField.setText("");
                passScoreSpinner.setValue(60.0);
                maxRetriesSpinner.setValue(-1);
                questionListModel.clear();
                deleteQuizButton.setEnabled(false);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void refreshQuestionList() {
        questionListModel.clear();
        if (currentQuiz != null) {
            for (Question q : currentQuiz.getQuestions()) {
                questionListModel.addElement(q.getText());
            }
        }
    }
    
    private void saveQuiz() {
        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Quiz title cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            double passScore = ((Number) passScoreSpinner.getValue()).doubleValue();
            int maxRetries = ((Number) maxRetriesSpinner.getValue()).intValue();
            
            if (currentQuiz == null) {
                // Create new quiz
                Quiz newQuiz = quizController.createQuiz(title, passScore, maxRetries);
                quizController.setQuizForLesson(courseId, lessonId, newQuiz);
                currentQuiz = newQuiz;
                JOptionPane.showMessageDialog(this, "Quiz created successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Update existing quiz
                currentQuiz.setTitle(title);
                currentQuiz.setPassScorePercent(passScore);
                currentQuiz.setMaxRetries(maxRetries);
                quizController.updateQuiz(currentQuiz);
                quizController.setQuizForLesson(courseId, lessonId, currentQuiz);
                JOptionPane.showMessageDialog(this, "Quiz updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
            deleteQuizButton.setEnabled(true);
            refreshQuestionList();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteQuiz() {
        if (currentQuiz == null) {
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this quiz?", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                quizController.removeQuizFromLesson(courseId, lessonId);
                quizController.deleteQuiz(currentQuiz.getQuizId());
                currentQuiz = null;
                titleField.setText("");
                passScoreSpinner.setValue(60.0);
                maxRetriesSpinner.setValue(-1);
                questionListModel.clear();
                deleteQuizButton.setEnabled(false);
                JOptionPane.showMessageDialog(this, "Quiz deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void addQuestion() {
        if (currentQuiz == null) {
            JOptionPane.showMessageDialog(this, "Please save the quiz first", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        QuestionDialog dialog = new QuestionDialog(this, null);
        dialog.setVisible(true);
        
        if (dialog.isSaved()) {
            try {
                Question question = dialog.getQuestion();
                
                // Add question directly to the current quiz
                currentQuiz.addQuestion(question);
                
                // Save the updated quiz back to the lesson
                quizController.setQuizForLesson(courseId, lessonId, currentQuiz);
                
                // Refresh the quiz and question list
                loadQuiz();
                
                JOptionPane.showMessageDialog(this, "Question added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void editQuestion() {
        if (currentQuiz == null) {
            JOptionPane.showMessageDialog(this, "No quiz loaded", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int index = questionList.getSelectedIndex();
        if (index < 0) {
            JOptionPane.showMessageDialog(this, "Please select a question", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Question questionToEdit = currentQuiz.getQuestions().get(index);
        QuestionDialog dialog = new QuestionDialog(this, questionToEdit);
        dialog.setVisible(true);
        
        if (dialog.isSaved()) {
            try {
                // Remove old question and add new one directly to the current quiz
                currentQuiz.removeQuestion(questionToEdit.getQuestionId());
                Question updatedQuestion = dialog.getQuestion();
                currentQuiz.addQuestion(updatedQuestion);
                
                // Save the updated quiz back to the lesson
                quizController.setQuizForLesson(courseId, lessonId, currentQuiz);
                
                // Refresh the quiz and question list
                loadQuiz();
                
                JOptionPane.showMessageDialog(this, "Question updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void deleteQuestion() {
        if (currentQuiz == null) {
            JOptionPane.showMessageDialog(this, "No quiz loaded", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int index = questionList.getSelectedIndex();
        if (index < 0) {
            JOptionPane.showMessageDialog(this, "Please select a question", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Question question = currentQuiz.getQuestions().get(index);
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this question?", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Remove question directly from the current quiz
                currentQuiz.removeQuestion(question.getQuestionId());
                
                // Save the updated quiz back to the lesson
                quizController.setQuizForLesson(courseId, lessonId, currentQuiz);
                
                // Refresh the quiz and question list
                loadQuiz();
                
                JOptionPane.showMessageDialog(this, "Question deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
