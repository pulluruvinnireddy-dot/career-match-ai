package com.careermatch.ai.parser;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class ResumeParserService {

    private static final Logger log = LoggerFactory.getLogger(ResumeParserService.class);
    private static final long MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024; // 10 MB

    public static class ParseResult {
        private final boolean success;
        private final String text;
        private final String errorMessage;

        public ParseResult(boolean success, String text, String errorMessage) {
            this.success = success;
            this.text = text;
            this.errorMessage = errorMessage;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getText() {
            return text;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    public ParseResult extractText(String fileName, byte[] fileData) {
        if (fileData == null || fileData.length == 0) {
            return new ParseResult(false, null, "Uploaded file is empty.");
        }

        if (fileData.length > MAX_FILE_SIZE_BYTES) {
            return new ParseResult(false, null, "File size exceeds 10MB limit. Please upload a smaller resume.");
        }

        String lowerName = fileName != null ? fileName.toLowerCase() : "";

        try {
            if (lowerName.endsWith(".pdf")) {
                return parsePdf(fileData);
            } else if (lowerName.endsWith(".docx")) {
                return parseDocx(fileData);
            } else if (lowerName.endsWith(".txt")) {
                String content = new String(fileData, StandardCharsets.UTF_8).trim();
                return new ParseResult(true, content, null);
            } else {
                return new ParseResult(false, null, "Unsupported file format. Please upload a PDF (.pdf) or Word document (.docx).");
            }
        } catch (Exception e) {
            log.error("Failed to parse file: {}", fileName, e);
            return new ParseResult(false, null, "Failed to read document text: " + e.getMessage());
        }
    }

    private ParseResult parsePdf(byte[] pdfBytes) {
        try (PDDocument document = Loader.loadPDF(pdfBytes)) {
            if (document.isEncrypted()) {
                return new ParseResult(false, null, "The PDF file is password protected. Please upload an unlocked PDF.");
            }

            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            String text = stripper.getText(document);

            if (text == null || text.trim().length() < 30) {
                return new ParseResult(false, null,
                        "Scanned or Image-only PDF detected! The PDF has no selectable text. Please upload a text-based PDF or DOCX resume.");
            }

            return new ParseResult(true, cleanExtractedText(text), null);
        } catch (IOException e) {
            log.error("PDFBox extraction error", e);
            return new ParseResult(false, null, "Corrupted or invalid PDF file. Could not parse document.");
        }
    }

    private ParseResult parseDocx(byte[] docxBytes) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(docxBytes);
             XWPFDocument doc = new XWPFDocument(bis);
             XWPFWordExtractor extractor = new XWPFWordExtractor(doc)) {

            String text = extractor.getText();
            if (text == null || text.trim().length() < 30) {
                return new ParseResult(false, null, "DOCX file does not contain sufficient text.");
            }

            return new ParseResult(true, cleanExtractedText(text), null);
        } catch (Exception e) {
            log.error("POI DOCX extraction error", e);
            return new ParseResult(false, null, "Corrupted DOCX file. Please upload a valid Word document.");
        }
    }

    private String cleanExtractedText(String text) {
        if (text == null) return "";
        // Normalize whitespace while preserving paragraphs
        return text.replaceAll("[\\r\\t]", " ")
                .replaceAll(" +", " ")
                .replaceAll("\\n{3,}", "\n\n")
                .trim();
    }
}
