package view.Student;

import controller.QuizController;
import model.Quiz;
import model.Question;
import model.QuizAttempt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Quiz Viewer for Students (Frontend Layer)
 * Allows students to take quizzes
 */
public class QuizViewerFrame extends JFrame {
    private String courseId;
    private String lessonId;
    private Quiz quiz;
    private QuizController quizController;
    private String studentId;
    
    private List<JRadioButton> answerButtons;
    private ButtonGroup currentGroup;
    private JTextArea questionArea;
    private JPanel answersPanel;
    private JButton submitButton;
    private JButton previousButton;
    private JButton nextButton;
    private JLabel questionNumberLabel;
    private int currentQuestionIndex = 0;
    private List<Integer> selectedAnswers;

    public QuizViewerFrame(String courseId, String lessonId, Quiz quiz, 
                          QuizController quizController, String studentId) {
        this.courseId = courseId;
        this.lessonId = lessonId;
        this.quiz = quiz;
        this.quizController = quizController;
        this.studentId = studentId;
        this.selectedAnswers = new ArrayList<>();
        
        // Initialize with -1 (unanswered) for each question
        for (int i = 0; i < quiz.getQuestions().size(); i++) {
            selectedAnswers.add(-1);
        }
        
        setTitle("Quiz: " + quiz.getTitle());
        setSize(700, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        
        initializeUI();
        showQuestion(0);
    }
    
    private void initializeUI() {
        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Question number label
        questionNumberLabel = new JLabel();
        questionNumberLabel.setFont(new Font("Arial", Font.BOLD, 14));
        questionNumberLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        mainPanel.add(questionNumberLabel, BorderLayout.NORTH);
        
        // Question area
        questionArea = new JTextArea();
        questionArea.setEditable(false);
        questionArea.setLineWrap(true);
        questionArea.setWrapStyleWord(true);
        questionArea.setFont(new Font("Arial", Font.PLAIN, 14));
        questionArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane questionScroll = new JScrollPane(questionArea);
        questionScroll.setPreferredSize(new Dimension(0, 80));
        mainPanel.add(questionScroll, BorderLayout.CENTER);
        
        // Answers panel
        answersPanel = new JPanel();
        answersPanel.setLayout(new BoxLayout(answersPanel, BoxLayout.Y_AXIS));
        answersPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10), "Select an answer:"));
        JScrollPane answersScroll = new JScrollPane(answersPanel);
        answersScroll.setPreferredSize(new Dimension(0, 250));
        mainPanel.add(answersScroll, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Navigation buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        previousButton = new JButton("Previous");
        previousButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentQuestionIndex > 0) {
                    showQuestion(currentQuestionIndex - 1);
                }
            }
        });
        buttonPanel.add(previousButton);
        
        nextButton = new JButton("Next");
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentQuestionIndex < quiz.getQuestions().size() - 1) {
                    showQuestion(currentQuestionIndex + 1);
                }
            }
        });
        buttonPanel.add(nextButton);
        
        submitButton = new JButton("Submit Quiz");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitQuiz();
            }
        });
        buttonPanel.add(submitButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void showQuestion(int index) {
        if (index < 0 || index >= quiz.getQuestions().size()) {
            return;
        }
        
        currentQuestionIndex = index;
        Question question = quiz.getQuestions().get(index);
        
        // Update question number
        questionNumberLabel.setText("Question " + (index + 1) + " of " + quiz.getQuestions().size());
        
        // Update question text
        questionArea.setText(question.getText());
        
        // Clear and rebuild answer buttons
        answersPanel.removeAll();
        answerButtons = new ArrayList<>();
        currentGroup = new ButtonGroup();
        
        // Create radio buttons for each option
        for (int i = 0; i < question.getOptions().size(); i++) {
            JRadioButton radioButton = new JRadioButton(question.getOptions().get(i));
            radioButton.setActionCommand(String.valueOf(i));
            radioButton.setFont(new Font("Arial", Font.PLAIN, 13));
            radioButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            radioButton.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            currentGroup.add(radioButton);
            answerButtons.add(radioButton);
            answersPanel.add(radioButton);
            
            // Select if previously answered
            if (selectedAnswers.get(index) == i) {
                radioButton.setSelected(true);
            }
        }
        
        // Add listener to save selection
        for (JRadioButton btn : answerButtons) {
            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int selectedIndex = Integer.parseInt(e.getActionCommand());
                    selectedAnswers.set(currentQuestionIndex, selectedIndex);
                }
            });
        }
        
        // Update navigation buttons
        previousButton.setEnabled(index > 0);
        nextButton.setEnabled(index < quiz.getQuestions().size() - 1);
        
        // Force layout update
        answersPanel.revalidate();
        answersPanel.repaint();
        revalidate();
        repaint();
    }
    
    private void submitQuiz() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to submit the quiz? You cannot change your answers after submitting.", 
            "Confirm Submit", JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        try {
            QuizAttempt attempt = quizController.submitQuizAttempt(
                courseId, lessonId, studentId, selectedAnswers);
            
            // Show results
            String message = String.format(
                "Quiz Submitted!\n\n" +
                "Score: %.1f%%\n" +
                "Passing Score: %.1f%%\n" +
                "Result: %s",
                attempt.getScorePercent(),
                quiz.getPassScorePercent(),
                attempt.isPassed() ? "PASSED ✓" : "FAILED ✗"
            );
            
            JOptionPane.showMessageDialog(this, message, "Quiz Results", 
                attempt.isPassed() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
            
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
