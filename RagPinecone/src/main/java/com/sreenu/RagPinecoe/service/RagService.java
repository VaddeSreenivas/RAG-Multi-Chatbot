package com.sreenu.RagPinecoe.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagService {

    private static final Logger logger =
            LoggerFactory.getLogger(RagService.class);

    private final ChatModel chatModel;
    private final VectorStore vectorStore;

    public RagService(ChatModel chatModel, VectorStore vectorStore) {
        this.chatModel = chatModel;
        this.vectorStore = vectorStore;
    }

    public String ask(String query) {

        logger.info("🧠 RAG ask() invoked");
        logger.info("User query: {}", query);

        SearchRequest searchRequest = SearchRequest.builder()
                .query(query)
                .topK(5)
                .build();

        logger.debug("Executing vector similarity search");

        List<Document> docs = vectorStore.similaritySearch(searchRequest);

        logger.info("Vector search completed. Documents found: {}", docs.size());

        if (docs.isEmpty()) {
            logger.warn("⚠ No relevant documents found for query");
        }

        String context = docs.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n---\n\n"));

        logger.debug("Context assembled for LLM. Context length: {} characters",
                context.length());

        String prompt = """
                You are an intelligent RAG assistant.
                Use the context below to answer the user's question.

                CONTEXT:
                %s

                QUESTION:
                %s

                If answer not found, respond: "I don't know based on available context."
                """.formatted(context, query);

        logger.debug("Final prompt constructed for LLM");

        String response = chatModel.call(prompt);

        logger.info("✅ LLM response generated successfully");
        logger.debug("LLM response: {}", response);

        return response;
    }
}
