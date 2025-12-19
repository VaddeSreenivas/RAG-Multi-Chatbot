package com.sreenu.RagPinecoe.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.openai.*;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.openai.audio.speech.SpeechPrompt;
import org.springframework.ai.openai.audio.speech.SpeechResponse;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/audio")
@CrossOrigin(origins = "*")
public class AudioController {

    private static final Logger logger =
            LoggerFactory.getLogger(AudioController.class);

    private final OpenAiAudioTranscriptionModel transcriptionModel;
    private final OpenAiAudioSpeechModel speechModel;

    public AudioController(OpenAiAudioTranscriptionModel transcriptionModel,
                           OpenAiAudioSpeechModel speechModel) {
        this.transcriptionModel = transcriptionModel;
        this.speechModel = speechModel;
    }

    @GetMapping("/to-text")
    public String audioToText() {

        logger.info("🎧 /api/audio/to-text endpoint hit");

        OpenAiAudioTranscriptionOptions options = OpenAiAudioTranscriptionOptions.builder()
                .language("en")
                .temperature(0.5f)
                .responseFormat(OpenAiAudioApi.TranscriptResponseFormat.TEXT)
                .build();

        logger.debug("Audio transcription options created: {}", options);

        AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(
                new ClassPathResource("audio/sameple_audio.mp3"),
                options);

        String result = transcriptionModel
                .call(prompt)
                .getResult()
                .getOutput();

        logger.info("✅ Transcription completed successfully");
        logger.debug("Transcription result: {}", result);

        return result;
    }

    @GetMapping("/text-to-audio/{text}")
    public ResponseEntity<Resource> textToAudio(@PathVariable String text) {

        logger.info("🔊 /api/audio/text-to-audio endpoint hit");
        logger.info("Input text: {}", text);

        OpenAiAudioSpeechOptions options = OpenAiAudioSpeechOptions.builder()
                .model(OpenAiAudioApi.TtsModel.TTS_1.getValue())
                .voice(OpenAiAudioApi.SpeechRequest.Voice.ALLOY.getValue())
                .responseFormat(OpenAiAudioApi.SpeechRequest.AudioResponseFormat.MP3)
                .speed(1.0f)
                .build();

        SpeechPrompt prompt = new SpeechPrompt(text, options);

        SpeechResponse response = speechModel.call(prompt);

        logger.info("✅ Text-to-speech audio generated successfully");
        logger.debug("Generated audio byte size: {}",
                response.getResult().getOutput().length);

        ByteArrayResource resource =
                new ByteArrayResource(response.getResult().getOutput());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename("tts_audio.mp3")
                                .toString())
                .body(resource);
    }
}
