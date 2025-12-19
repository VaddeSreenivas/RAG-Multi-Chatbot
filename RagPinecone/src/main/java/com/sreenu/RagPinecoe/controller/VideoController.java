//package com.sreenu.RagPinecoe.controller;
//
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//import com.sreenu.RagPinecoe.service.VideoService;
//import org.springframework.beans.factory.annotation.Autowired;
//
//@RestController
//@RequestMapping("/api/video")
//@CrossOrigin(origins = "*")
//public class VideoController {
//
//    @Autowired
//    private VideoService videoService;
//
//    @PostMapping("/upload")
//    public String uploadVideo(@RequestParam("file") MultipartFile file) {
//        // Placeholder for video upload logic
//        return "Video uploaded successfully: " + file.getOriginalFilename();
//    }
//
//    @GetMapping("/analyze")
//    public String analyzeVideo(@RequestParam("videoId") String videoId) {
//        // Placeholder for video analysis logic
//        return "Analysis result for video ID: " + videoId;
//    }
//
//    @PostMapping("/generate")
//    public String generateVideo(@RequestParam("prompt") String prompt) {
//        try {
//            String videoId = videoService.createVideo(prompt);
//            return "Video generation started with ID: " + videoId;
//        } catch (Exception e) {
//            return "Error starting video generation: " + e.getMessage();
//        }
//    }
//
//    @GetMapping("/status")
//    public String getVideoStatus(@RequestParam("videoId") String videoId) {
//        try {
//            return videoService.getVideoStatus(videoId);
//        } catch (Exception e) {
//            return "Error retrieving video status: " + e.getMessage();
//        }
//    }
//
//    @GetMapping("/download")
//    public String downloadVideo(@RequestParam("videoId") String videoId) {
//        try {
//            videoService.downloadVideo(videoId);
//            return "Video downloaded successfully.";
//        } catch (Exception e) {
//            return "Error downloading video: " + e.getMessage();
//        }
//    }
//}