package com.sreenu.RagPinecoe.controller;

import com.sreenu.RagPinecoe.ingestion.DocumentIngestionService;
import com.sreenu.RagPinecoe.model.QueryRequest;
import com.sreenu.RagPinecoe.service.RagService;
import com.sreenu.RagPinecoe.service.UrlIngestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rag")
@CrossOrigin(origins = "*")
public class RagChatController {

    private static final Logger logger =
            LoggerFactory.getLogger(RagChatController.class);

    private final ChatModel chatModel;
    private final VectorStore vectorStore;
    private final UrlIngestService urlIngestService;
    private final DocumentIngestionService ingestionService;
    private final RagService ragService;

    public RagChatController(ChatModel chatModel, VectorStore vectorStore,
                             UrlIngestService urlIngestService,
                             DocumentIngestionService ingestionService,
                             RagService ragService) {
        this.chatModel = chatModel;
        this.vectorStore = vectorStore;
        this.urlIngestService = urlIngestService;
        this.ingestionService = ingestionService;
        this.ragService = ragService;
    }

    // ---------------- ASK (Query Param) ----------------

    @GetMapping("/ask")
    public String askQuestion(@RequestParam String question) {

        logger.info("💬 /api/rag/ask endpoint hit");
        logger.info("User question: {}", question);

        List<Document> docs = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(question)
                        .topK(5)
                        .build()
        );

        logger.info("Vector search completed. Documents found: {}", docs.size());

        String documents = docs.isEmpty()
                ? "NO_RELEVANT_DOCUMENTS_FOUND"
                : docs.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));

        String template = """
                You are an AI assistant that answers ONLY using the text in the DOCUMENTS section.
                
                If DOCUMENTS do not contain the answer, respond:
                "No relevant information found."
                
                QUESTION:
                {question}
                
                DOCUMENTS:
                {documents}
                """;

        PromptTemplate prompt = new PromptTemplate(template);

        Map<String, Object> vars = Map.of(
                "question", question,
                "documents", documents
        );

        String answer = chatModel.call(prompt.create(vars))
                .getResult()
                .getOutput()
                .getText();

        logger.info("✅ RAG answer generated successfully");
        logger.debug("RAG response: {}", answer);

        return answer;
    }

    // ---------------- INGEST URL ----------------

    @PostMapping("/ingest-url")
    public ResponseEntity<String> ingestUrl(@RequestBody Map<String, String> body) {

        logger.info("🌐 /api/rag/ingest-url endpoint hit");

        String url = body.get("url");

        if (url == null || url.isBlank()) {
            logger.warn("⚠️ URL ingestion failed – URL is missing");
            return ResponseEntity.badRequest().body("URL is missing");
        }

        logger.info("URL to ingest: {}", url);

        String result = urlIngestService.ingest(url);

        logger.info("✅ URL ingestion completed successfully");

        return ResponseEntity.ok(result);
    }

    // ---------------- INGEST PDF (Upload) ----------------

    @PostMapping("/ingest/pdf")
    public ResponseEntity<String> ingestPdf(@RequestParam("file") MultipartFile file) {

        logger.info("📄 /api/rag/ingest/pdf endpoint hit");
        logger.info("Uploaded PDF name: {}", file.getOriginalFilename());
        logger.info("Uploaded PDF size: {} bytes", file.getSize());

        try {
            ingestionService.ingestUploadedPdf(file);
            logger.info("✅ PDF ingested successfully");
            return ResponseEntity.ok("PDF ingested successfully.");
        } catch (Exception e) {
            logger.error("❌ PDF ingestion failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("PDF ingestion failed: " + e.getMessage());
        }
    }

    // ---------------- INGEST PDF (Batch) ----------------

    @PostMapping("/ingest/run")
    public String runPdfIngestion() {

        logger.info("📚 /api/rag/ingest/run endpoint hit");

        ingestionService.ingestPdf();

        logger.info("✅ Batch PDF ingestion completed");

        return "PDF ingestion completed!";
    }

    // ---------------- ASK (Request Body) ----------------

    @PostMapping("/ask-body-ulr")
    public String askBody(@RequestBody QueryRequest request) {

        logger.info("🧠 /api/rag/ask-body-ulr endpoint hit");
        logger.info("User query: {}", request.getQuery());

        String response = ragService.ask(request.getQuery());

        logger.info("✅ RAG response generated via service");
        logger.debug("RAG service response: {}", response);

        return response;
    }
}
