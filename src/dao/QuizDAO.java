package dao;

import model.Quiz;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class QuizDAO {

    private final String filePath;

    public QuizDAO(String filePath) {
        this.filePath = filePath;
    }

    // Load all quizzes
    public List<Quiz> loadAll() {
        try {
            if (!Files.exists(Paths.get(filePath))) {
                return new ArrayList<>();
            }

            String data = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONArray arr = new JSONArray(data);

            List<Quiz> quizzes = new ArrayList<>();
            for (int i = 0; i < arr.length(); i++) {
                quizzes.add(Quiz.fromJson(arr.getJSONObject(i)));
            }
            return quizzes;

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Save all quizzes
    private void saveAll(List<Quiz> quizzes) {
        JSONArray arr = new JSONArray();
        for (Quiz q : quizzes) arr.put(q.toJson());

        try (FileWriter fw = new FileWriter(filePath)) {
            fw.write(arr.toString(4)); // pretty JSON
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Add or update quiz
    public void saveQuiz(Quiz quiz) {
        List<Quiz> all = loadAll();
        all.removeIf(q -> q.getQuizId().equals(quiz.getQuizId()));
        all.add(quiz);
        saveAll(all);
    }

    // Find quiz by ID
    public Quiz getQuiz(String id) {
        return loadAll()
                .stream()
                .filter(q -> q.getQuizId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // Delete quiz
    public boolean deleteQuiz(String id) {
        List<Quiz> quizzes = loadAll();
        boolean removed = quizzes.removeIf(q -> q.getQuizId().equals(id));
        if (removed) saveAll(quizzes);
        return removed;
    }

    // List all quizzes for a course
    public List<Quiz> getQuizzesByCourse(String quizID) {
        List<Quiz> result = new ArrayList<>();
        for (Quiz q : loadAll()) {
            if (q.getQuizId().equals(quizID)) result.add(q);
        }
        return result;
    }
}
