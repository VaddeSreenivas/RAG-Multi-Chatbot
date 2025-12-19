package com.sreenu.RagPinecoe.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.image.*;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/image")
@CrossOrigin(origins = "*")
public class ImageController {

    private static final Logger logger =
            LoggerFactory.getLogger(ImageController.class);

    private final ChatModel chatModel;
    private final ImageModel imageModel;

    public ImageController(ChatModel chatModel, ImageModel imageModel) {
        this.chatModel = chatModel;
        this.imageModel = imageModel;
    }

    @PostMapping("/describe")
    public String describeImage(@RequestParam("file") MultipartFile file,
                                @RequestParam(value = "question", required = false) String question) {

        logger.info("🖼️ /api/image/describe endpoint hit");
        logger.info("Uploaded file name: {}", file.getOriginalFilename());
        logger.info("Uploaded file size: {} bytes", file.getSize());
        logger.debug("User question: {}", question);

        // Detect image MIME type
        String mimeType = (file.getContentType() != null)
                ? file.getContentType()
                : MimeTypeUtils.IMAGE_JPEG_VALUE;

        MimeType mt = MimeTypeUtils.parseMimeType(mimeType);
        logger.debug("Detected MIME type: {}", mt);

        // Load bytes safely
        byte[] imageBytes;
        try {
            imageBytes = file.getBytes();
            logger.debug("Image bytes loaded successfully");
        } catch (IOException e) {
            logger.error("❌ Failed to read uploaded file", e);
            throw new RuntimeException("Failed to read uploaded file", e);
        }

        // Universal image prompt
        String promptText = (question != null && !question.isEmpty())
                ? """
                You are an expert vision + language AI.
                Answer the user's question using ONLY what is visible in the image.
                Provide deep reasoning when helpful.

                USER QUESTION:
                """ + question
                : """
                You are an expert AI that analyzes images.
                Describe this image in detail, including structure, text, relationships, and insights.
                """;

        logger.debug("Prompt prepared for vision model");

        // Call model
        String response = ChatClient.create(chatModel)
                .prompt()
                .user(user -> user
                        .text(promptText)
                        .media(mt, new ByteArrayResource(imageBytes)))
                .call()
                .content();

        logger.info("✅ Image description generated successfully");
        logger.debug("Model response: {}", response);

        return response;
    }

    @GetMapping("/generate/{prompt}")
    public String generateImage(@PathVariable String prompt) {

        logger.info("🎨 /api/image/generate endpoint hit");
        logger.info("Image generation prompt: {}", prompt);

        ImagePrompt imagePrompt = new ImagePrompt(
                prompt,
                OpenAiImageOptions.builder()
                        .width(1024)
                        .height(1024)
                        .quality("hd")
                        .build()
        );

        ImageResponse response = imageModel.call(imagePrompt);

        String imageUrl = response.getResult().getOutput().getUrl();

        logger.info("✅ Image generated successfully");
        logger.debug("Generated image URL: {}", imageUrl);

        return imageUrl;
    }
}
