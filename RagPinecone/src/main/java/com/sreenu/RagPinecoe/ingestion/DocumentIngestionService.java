package com.sreenu.RagPinecoe.ingestion;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class DocumentIngestionService {

    private static final Logger logger =
            LoggerFactory.getLogger(DocumentIngestionService.class);

    @Value("classpath:/pdf/Master+Generative+AI+with+JAVA+and+Spring+Boot.pdf")
    private Resource pdfResource;

    private final VectorStore vectorStore;

    public DocumentIngestionService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    /**
     * INITIAL INGESTION AT STARTUP
     */
    @PostConstruct
    public void init() {

        logger.info("🔍 Checking Pinecone vector store status at startup");

        if (isAlreadyIngested()) {
            logger.warn("⚠ Pinecone already contains vectors — skipping ingestion at startup");
            return;
        }

        logger.info("🚀 Pinecone empty — starting fresh PDF ingestion");
        ingestPdf();
    }

    /**
     * CHECK IF DATA ALREADY EXISTS IN VECTOR STORE
     */
    public boolean isAlreadyIngested() {
        try {
            SearchRequest request = SearchRequest.builder()
                    .query("test")
                    .topK(1)
                    .build();

            boolean exists = !vectorStore.similaritySearch(request).isEmpty();

            logger.debug("Vector store contains existing data: {}", exists);

            return exists;

        } catch (Exception e) {
            logger.error("❌ Error while checking vector store status", e);
            return false;
        }
    }

    /**
     * INGEST DEFAULT PDF FROM CLASSPATH
     */
    public void ingestPdf() {
        try {
            logger.info("📘 Starting ingestion of default PDF from classpath");

            TikaDocumentReader reader = new TikaDocumentReader(pdfResource);
            List<Document> rawDocs = reader.read();

            logger.info("Raw documents extracted: {}", rawDocs.size());

            TokenTextSplitter splitter = TokenTextSplitter.builder()
                    .withChunkSize(300)
                    .withMinChunkLengthToEmbed(50)
                    .build();

            List<Document> chunks = splitter.split(rawDocs);

            logger.info("Documents split into {} chunks", chunks.size());

            sanitizeMetadata(chunks);

            vectorStore.accept(chunks);

            logger.info("✅ Default PDF ingested successfully. Total chunks stored: {}", chunks.size());

        } catch (Exception e) {
            logger.error("❌ Error while ingesting default PDF", e);
        }
    }

    /**
     * INGEST UPLOADED PDF
     */
    public void ingestUploadedPdf(MultipartFile file) {
        try {
            logger.info("📄 Starting uploaded PDF ingestion");
            logger.info("Uploaded file name: {}", file.getOriginalFilename());
            logger.info("Uploaded file size: {} bytes", file.getSize());

            InputStreamResource resource =
                    new InputStreamResource(file.getInputStream());

            TikaDocumentReader reader = new TikaDocumentReader(resource);
            List<Document> rawDocs = reader.read();

            logger.debug("Extracted {} raw documents from uploaded PDF", rawDocs.size());

            TokenTextSplitter splitter = TokenTextSplitter.builder()
                    .withChunkSize(500)
                    .withMinChunkLengthToEmbed(50)
                    .build();

            List<Document> chunks = splitter.split(rawDocs);

            logger.info("PDF split into {} chunks", chunks.size());

            vectorStore.add(chunks);

            logger.info("✅ Uploaded PDF ingested successfully");

        } catch (Exception e) {
            logger.error("❌ PDF ingestion error", e);
            throw new RuntimeException("PDF ingestion failed", e);
        }
    }

    /**
     * FIX PINECONE METADATA BUG
     */
    private void sanitizeMetadata(List<Document> docs) {

        logger.debug("Sanitizing metadata for {} documents", docs.size());

        for (Document d : docs) {
            if (d.getMetadata() != null) {
                d.getMetadata().replaceAll(
                        (k, v) -> v == null ? "" : v.toString()
                );
            }
        }

        logger.debug("Metadata sanitization completed");
    }
}
