//package com.sreenu.RagPinecoe.service;
//
//import org.springframework.stereotype.Service;
//import org.springframework.beans.factory.annotation.Value;
//import java.io.*;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
//@Service
//public class VideoService {
//
//    @Value("${openai.api.key}")
//    private String openaiApiKey;
//
//    @Value("${openai.video.model}")
//    private String videoModel;
//
//    private static final String OPENAI_API_URL = "https://api.openai.com/v1/videos";
//
////    public String createVideo(String prompt) throws IOException {
////        HttpURLConnection connection = (HttpURLConnection) new URL(OPENAI_API_URL + "/create").openConnection();
////        connection.setRequestMethod("POST");
////        connection.setRequestProperty("Authorization", "Bearer " + openaiApiKey);
////        connection.setRequestProperty("Content-Type", "application/json");
////        connection.setDoOutput(true);
////
////        String requestBody = "{\"model\": \"" + videoModel + "\", \"prompt\": \"" + prompt + "\"}";
////        try (OutputStream os = connection.getOutputStream()) {
////            os.write(requestBody.getBytes());
////        }
////
////        if (connection.getResponseCode() == 200) {
////            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
////                StringBuilder response = new StringBuilder();
////                String line;
////                while ((line = br.readLine()) != null) {
////                    response.append(line);
////                }
////                // Extract video ID from response
////                return extractVideoId(response.toString());
////            }
////        } else {
////            throw new IOException("Failed to create video: " + connection.getResponseMessage());
////        }
////    }
//public String createVideo(String prompt) throws IOException {
//
//    HttpURLConnection connection = (HttpURLConnection) new URL(OPENAI_API_URL).openConnection();
//    connection.setRequestMethod("POST");
//    connection.setRequestProperty("Authorization", "Bearer " + openaiApiKey);
//    connection.setRequestProperty("Content-Type", "application/json");
//    connection.setDoOutput(true);
//
//    // IMPORTANT: correct OpenAI video request body
//    String requestBody = "{"
//            + "\"model\": \"" + videoModel + "\","
//            + "\"input\": \"" + prompt + "\""
//            + "}";
//
//    try (OutputStream os = connection.getOutputStream()) {
//        os.write(requestBody.getBytes());
//    }
//
//    int status = connection.getResponseCode();
//
//    if (status >= 200 && status < 300) {
//        StringBuilder response = new StringBuilder();
//        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
//            String line;
//            while ((line = br.readLine()) != null) {
//                response.append(line);
//            }
//        }
//
//        return extractVideoId(response.toString());
//
//    } else {
//        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
//        String error = br.readLine();
//        throw new IOException("OpenAI error: " + status + " => " + error);
//    }
//}
//
//
//    private String extractVideoId(String json) {
//        // naïve parser
//        int idStart = json.indexOf("\"id\":\"") + 6;
//        int idEnd = json.indexOf("\"", idStart);
//        return json.substring(idStart, idEnd);
//    }
//
//
//public String getVideoStatus(String videoId) throws IOException {
//        HttpURLConnection connection = (HttpURLConnection) new URL(OPENAI_API_URL + "/" + videoId).openConnection();
//        connection.setRequestMethod("GET");
//        connection.setRequestProperty("Authorization", "Bearer " + openaiApiKey);
//
//        if (connection.getResponseCode() == 200) {
//            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
//                StringBuilder response = new StringBuilder();
//                String line;
//                while ((line = br.readLine()) != null) {
//                    response.append(line);
//                }
//                return response.toString();
//            }
//        } else {
//            throw new IOException("Failed to retrieve video status: " + connection.getResponseMessage());
//        }
//    }
//
//    public void downloadVideo(String videoId) throws IOException {
//        HttpURLConnection connection = (HttpURLConnection) new URL(OPENAI_API_URL + "/" + videoId + "/content").openConnection();
//        connection.setRequestMethod("GET");
//        connection.setRequestProperty("Authorization", "Bearer " + openaiApiKey);
//
//        if (connection.getResponseCode() == 200) {
//            try (InputStream is = connection.getInputStream();
//                 FileOutputStream fos = new FileOutputStream("video.mp4")) {
//                byte[] buffer = new byte[1024];
//                int bytesRead;
//                while ((bytesRead = is.read(buffer)) != -1) {
//                    fos.write(buffer, 0, bytesRead);
//                }
//            }
//        } else {
//            throw new IOException("Failed to download video: " + connection.getResponseMessage());
//        }
//    }
//
////    private String extractVideoId(String jsonResponse) {
////        // Simple extraction logic for video ID from JSON response
////        int idStart = jsonResponse.indexOf("\"id\":\"") + 7;
////        int idEnd = jsonResponse.indexOf("\"", idStart);
////        return jsonResponse.substring(idStart, idEnd);
////    }
//}