package com.example.teampilot.document.extractor;
import com.example.teampilot.document.exception.DocumentExtractionException;
import com.example.teampilot.document.model.ExtractedDocument;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Component;

import org.xml.sax.SAXException;

import java.io.InputStream;
@Component
public class DocumentTextExtractor {

    public ExtractedDocument extract(
            InputStream inputStream,
            String filename) {

        try {
            AutoDetectParser parser = new AutoDetectParser();

            // Limit extracted text to 200,000 characters
            BodyContentHandler handler =
                    new BodyContentHandler(200_000);

            Metadata metadata = new Metadata();
            metadata.set("resourceName", filename);

            ParseContext context = new ParseContext();

            try (TikaInputStream tikaInputStream =
                         TikaInputStream.get(inputStream)) {

                parser.parse(
                        tikaInputStream,
                        handler,
                        metadata,
                        context
                );
            }

            String extractedText = handler.toString().trim();

            if (extractedText.isBlank()) {
                throw new DocumentExtractionException(
                        "No readable text found in document"
                );
            }

            return new ExtractedDocument(
                    filename,
                    metadata.get("Content-Type"),
                    extractedText
            );

        } catch (DocumentExtractionException e) {
            throw e;
        } catch (Exception e) {
            throw new DocumentExtractionException(
                    "Failed to extract text from: " + filename,
                    e
            );
        }
    }
}
