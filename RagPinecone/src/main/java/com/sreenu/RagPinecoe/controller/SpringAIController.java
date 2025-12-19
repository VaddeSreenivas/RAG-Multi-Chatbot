package com.sreenu.RagPinecoe.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.chat.prompt.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class SpringAIController {

    private static final Logger log = LoggerFactory.getLogger(SpringAIController.class);

    private final ChatClient chatClient;

    public SpringAIController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
        log.info("SpringAIController initialized");
    }

    @Value("classpath:/prompts/celeb-details.st")
    private Resource celebPrompt;

    /**
     * Simple prompt endpoint
     */
    @GetMapping("/prompt")
    public String simplePrompt(@RequestParam String message) {

        log.debug("Received /prompt request with message: {}", message);

        try {
            String response = chatClient.prompt(message).call().content();
            log.debug("Prompt response generated successfully");
            return response;
        } catch (Exception e) {
            log.error("Error while processing /prompt request", e);
            throw e;
        }
    }

    /**
     * Celebrity details using PromptTemplate
     */
    @GetMapping("/celeb")
    public String celebDetails(@RequestParam String name) {

        log.debug("Received /celeb request for name: {}", name);

        try {
            PromptTemplate template = new PromptTemplate(celebPrompt);
            Prompt prompt = template.create(Map.of("name", name));

            String response = chatClient.prompt(prompt)
                    .call()
                    .chatResponse()
                    .getResult()
                    .getOutput()
                    .toString();

            log.debug("Celebrity details fetched successfully for {}", name);
            return response;

        } catch (Exception e) {
            log.error("Error while fetching celebrity details for {}", name, e);
            throw e;
        }
    }

    /**
     * Sports information with system + user messages
     */
    @GetMapping("/sports")
    public String sportsDetails(@RequestParam String name) {

        log.debug("Received /sports request for sport: {}", name);

        String userText = """
                List all details of the sport %s including rules and regulations.
                Present the answer in a clean readable format.
                """.formatted(name);

        String systemText = """
                You are a smart Virtual Assistant.
                Only provide information about sports.
                If someone asks irrelevant questions, reply "I don't know the answer."
                """;

        try {
            Prompt prompt = new Prompt(
                    List.of(new UserMessage(userText), new SystemMessage(systemText))
            );

            String response = chatClient
                    .prompt(prompt)
                    .call()
                    .chatResponse()
                    .getResult()
                    .getOutput()
                    .toString();

            log.debug("Sports details generated successfully for {}", name);
            return response;

        } catch (Exception e) {
            log.error("Error while processing sports details for {}", name, e);
            throw e;
        }
    }
}
