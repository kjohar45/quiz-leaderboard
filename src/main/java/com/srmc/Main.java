package com.srmc;
 
import com.fasterxml.jackson.databind.ObjectMapper;
 
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
 
public class Main {
 
    private static final String BASE_URL = "https://devapigw.vidalhealthtpa.com/srm-quiz-task";
    private static final String REG_NO = "RA2311026010478";
 
    public static void main(String[] args) throws Exception {
 
        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();
 
        Map<String, Integer> scores = new HashMap<>();
 
        Set<String> seen = new HashSet<>();
 
        for (int poll = 0; poll <= 9; poll++) {
 
            String url = BASE_URL + "/quiz/messages?regNo=" + REG_NO + "&poll=" + poll;
 
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
 
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
 
            System.out.println("Poll " + poll + " status: " + response.statusCode());
            System.out.println("Poll " + poll + " body: " + response.body());
 
            QuizResponse quizResponse = mapper.readValue(response.body(), QuizResponse.class);
 
            if (quizResponse.getEvents() != null) {
                for (Event event : quizResponse.getEvents()) {
 
                    String key = event.getRoundId() + "_" + event.getParticipant();
 
                    if (seen.contains(key)) {
                        System.out.println("Duplicate found, skipping: " + key);
                        continue;
                    }
 
                    seen.add(key);
 
                    scores.put(
                            event.getParticipant(),
                            scores.getOrDefault(event.getParticipant(), 0) + event.getScore()
                    );
                }
            }
 
            if (poll < 9) {
                System.out.println("Waiting 5 seconds before next poll...");
                Thread.sleep(5000);
            }
        }
 
        List<Map<String, Object>> leaderboard = new ArrayList<>();
 
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            Map<String, Object> row = new HashMap<>();
            row.put("participant", entry.getKey());
            row.put("totalScore", entry.getValue());
            leaderboard.add(row);
        }
 
        leaderboard.sort((a, b) -> (int) b.get("totalScore") - (int) a.get("totalScore"));
 
        int grandTotal = 0;
        for (Map<String, Object> row : leaderboard) {
            grandTotal += (int) row.get("totalScore");
        }
 
        System.out.println("Final leaderboard: " + leaderboard);
        System.out.println("Grand total score: " + grandTotal);
 
        Map<String, Object> submitBody = new HashMap<>();
        submitBody.put("regNo", REG_NO);
        submitBody.put("leaderboard", leaderboard);
 
        String submitJson = mapper.writeValueAsString(submitBody);
 
        HttpRequest submitRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/quiz/submit"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(submitJson))
                .build();
 
        HttpResponse<String> submitResponse = client.send(submitRequest, HttpResponse.BodyHandlers.ofString());
 
        System.out.println("Submit status: " + submitResponse.statusCode());
        System.out.println("Submit response: " + submitResponse.body());
    }
}