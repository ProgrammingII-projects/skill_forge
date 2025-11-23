package view.Student;

import javax.swing.*;
import model.Course;
import model.Lesson;
import model.Quiz;
import model.User;
import utils.PdfGenerator;
import controller.CourseController;
import controller.StudentController;
import controller.QuizController;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Optional;

public class LessonViewerFrame extends JFrame {
    private String courseId;
    private CourseController courseController;
    private StudentController studentController;
    private QuizController quizController;
    private String studentId;
    private Course course;
    private User user;
    private JList<String> lessonList;
    private DefaultListModel<String> listModel;
    private JTextArea contentArea;
    private JLabel progressLabel;
    private JButton markCompleteButton;
    private JButton takeQuizButton;
    private JButton earnCertificateButton;
    private int currentLessonIndex = -1;

    public LessonViewerFrame(String courseId, CourseController courseController,
            StudentController studentController, QuizController quizController,
            String studentId) {
        this.courseId = courseId;
        this.courseController = courseController;
        this.studentController = studentController;
        this.quizController = quizController;
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

        setTitle("Lesson Viewer - " + course.getTitle());
        setSize(700, 500);
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
        listScroll.setBounds(20, 50, 250, 250);
        add(listScroll);

        progressLabel = new JLabel();
        progressLabel.setBounds(20, 310, 250, 25);
        add(progressLabel);

        takeQuizButton = new JButton("Take Quiz");
        takeQuizButton.setBounds(20, 345, 120, 30);
        takeQuizButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                takeQuiz();
            }
        });
        add(takeQuizButton);

        earnCertificateButton = new JButton("Earn Certificate");
        earnCertificateButton.setBounds(150, 380, 150, 30);
        earnCertificateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                earnCertificate();
            }
        });
        add(earnCertificateButton);

        markCompleteButton = new JButton("Mark as Complete");
        markCompleteButton.setBounds(150, 345, 120, 30);
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
        contentScroll.setBounds(290, 50, 380, 325);
        add(contentScroll);
        JButton backButton = new JButton("Back");
        backButton.setBounds(20, 420, 120, 30);
        backButton.addActionListener(e -> dispose());
        add(backButton);

        refreshLessonList();
        updateProgress();
        showLesson();
        updateCertificateButtonState();
    }

    private void refreshLessonList() {
        try {
            Optional<Course> opt = courseController.findById(courseId);
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
        updateCertificateButtonState();

    }

    private void showLesson() {
        int index = lessonList.getSelectedIndex();
        if (index < 0 || index >= course.getLessons().size()) {
            contentArea.setText("");
            takeQuizButton.setEnabled(false);
            markCompleteButton.setEnabled(false);
            return;
        }
        currentLessonIndex = index;
        Lesson lesson = course.getLessons().get(index);
        contentArea.setText(lesson.getContent());

        try {
            Quiz quiz = quizController.getQuizForLesson(courseId, lesson.getLessonId());
            if (quiz != null) {
                takeQuizButton.setEnabled(true);
                takeQuizButton.setText("Take Quiz");

                List<model.QuizAttempt> attempts = quizController.getUserQuizAttempts(
                        courseId, lesson.getLessonId(), studentId);
                boolean hasPassed = attempts.stream().anyMatch(a -> a.isPassed());

                markCompleteButton.setEnabled(hasPassed);
                if (hasPassed) {
                    markCompleteButton.setText("Mark as Complete");
                } else if (!attempts.isEmpty()) {
                    markCompleteButton.setText("Complete Quiz First");
                } else {
                    markCompleteButton.setText("Take Quiz First");
                }
            } else {
                takeQuizButton.setEnabled(false);
                takeQuizButton.setText("No Quiz");
                markCompleteButton.setEnabled(true);
                markCompleteButton.setText("Mark as Complete");
            }
        } catch (Exception ex) {
            takeQuizButton.setEnabled(false);
            markCompleteButton.setEnabled(true);
        }

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
        updateCertificateButtonState();

    }

    private void takeQuiz() {
        if (currentLessonIndex < 0 || currentLessonIndex >= course.getLessons().size()) {
            JOptionPane.showMessageDialog(this, "Please select a lesson", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Lesson lesson = course.getLessons().get(currentLessonIndex);
        try {
            Quiz quiz = quizController.getQuizForLesson(courseId, lesson.getLessonId());
            if (quiz == null) {
                JOptionPane.showMessageDialog(this, "No quiz available for this lesson", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean canRetake = quizController.canRetakeQuiz(courseId, lesson.getLessonId(), studentId);
            if (!canRetake) {
                JOptionPane.showMessageDialog(this, "You have reached the maximum number of attempts for this quiz",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            QuizViewerFrame quizViewer = new QuizViewerFrame(courseId, lesson.getLessonId(),
                    quiz, quizController, studentId);
            quizViewer.setVisible(true);
            quizViewer.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                    showLesson();
                    updateCertificateButtonState();

                }
            });
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void markComplete() {
        if (currentLessonIndex < 0 || currentLessonIndex >= course.getLessons().size()) {
            JOptionPane.showMessageDialog(this, "Please select a lesson", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Lesson lesson = course.getLessons().get(currentLessonIndex);

        try {
            Quiz quiz = quizController.getQuizForLesson(courseId, lesson.getLessonId());
            if (quiz != null) {
                List<model.QuizAttempt> attempts = quizController.getUserQuizAttempts(
                        courseId, lesson.getLessonId(), studentId);
                boolean hasPassed = attempts.stream().anyMatch(a -> a.isPassed());

                if (!hasPassed) {
                    JOptionPane.showMessageDialog(this,
                            "You must pass the quiz before marking this lesson as complete",
                            "Quiz Required", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
        } catch (Exception ex) {
        }

        try {
            studentController.markLessonCompleted(studentId, courseId, lesson.getLessonId());
            refreshLessonList();
            updateProgress();
            showLesson();
            JOptionPane.showMessageDialog(this, "Lesson marked as complete!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        updateCertificateButtonState();

    }

    public void earnCertificate() {
        try {
            List<String> completed = user.getProgress().getOrDefault(courseId, new java.util.ArrayList<>());
            if (completed.size() == course.getLessons().size()) {
                String filePath = PdfGenerator.generateCertificate(user.getUsername(), course.getTitle());
                JOptionPane.showMessageDialog(this,
                        "Congratulations! You have earned a certificate.\nSaved at: " + filePath, "Certificate Earned",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "You must complete all lessons to earn a certificate.",
                        "Incomplete Course", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCertificateButtonState() {
        boolean canEarn = studentController.canEarnCertificate(courseId, studentId);
        earnCertificateButton.setEnabled(canEarn);
    }

}
