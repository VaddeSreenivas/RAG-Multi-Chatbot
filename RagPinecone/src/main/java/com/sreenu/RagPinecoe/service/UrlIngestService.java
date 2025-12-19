package com.sreenu.RagPinecoe.service;

import com.sreenu.RagPinecoe.util.WebScraper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UrlIngestService {

    private static final Logger logger =
            LoggerFactory.getLogger(UrlIngestService.class);

    private final VectorStore vectorStore;

    public UrlIngestService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public String ingest(String url) {

        logger.info("🌐 URL ingestion started");
        logger.info("Target URL: {}", url);

        // Fetch web content
        String text;
        try {
            text = WebScraper.fetchText(url);
            logger.info("✅ Web content fetched successfully");
            logger.debug("Fetched text length: {} characters", text.length());
        } catch (Exception e) {
            logger.error("❌ Failed to fetch content from URL: {}", url, e);
            throw new RuntimeException("URL scraping failed", e);
        }

        // Create document
        Document doc = Document.builder()
                .text(text)
                .metadata(Map.of("source", url))
                .build();

        logger.debug("Document created with metadata source={}", url);

        // Split into chunks
        TokenTextSplitter splitter = TokenTextSplitter.builder()
                .withChunkSize(500)
                .withMinChunkLengthToEmbed(50)
                .build();

        List<Document> chunks = splitter.split(List.of(doc));

        logger.info("Document split into {} chunks", chunks.size());

        // Store in vector database
        vectorStore.add(chunks);

        logger.info("✅ URL ingested and stored in vector database successfully");

        return "Ingested URL: " + url + " | Chunks stored: " + chunks.size();
    }
}
