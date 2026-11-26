
package com.example.teampilot;

import com.example.teampilot.document.chunking.HeadingBasedChunkingStrategy;
import com.example.teampilot.document.chunking.TokenBasedChunkingStrategy;
import com.example.teampilot.document.model.DocumentChunk;
import com.example.teampilot.document.model.DocumentStructure;
import com.example.teampilot.document.model.ExtractedDocument;
import com.example.teampilot.document.service.DocumentChunkingService;
import com.example.teampilot.document.service.DocumentStructureAnalyzer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TeampilotApplicationTests {

	@Autowired
	private DocumentStructureAnalyzer structureAnalyzer;

	@Autowired
	private HeadingBasedChunkingStrategy headingBasedChunkingStrategy;

	@Autowired
	private DocumentChunkingService documentChunkingService;

	@Autowired
	private TokenBasedChunkingStrategy tokenBasedChunkingStrategy;

	@Test
	void contextLoads() {
	}

	@Test
	void shouldDetectResumeSections() {

		String text = """
                Bhuvan Savant

                EDUCATION
                BMS College of Engineering

                TECHNICAL SKILLS
                Java, Python, Spring Boot

                WORK EXPERIENCE
                Software Engineer at Honeywell

                PROJECTS
                Fraud Detection System
                """;

		DocumentStructure structure =
				structureAnalyzer.analyze(text);

		System.out.println("Structured: " + structure.structured());
		System.out.println("Confidence: " + structure.confidence());
		System.out.println("Total sections: " + structure.sections().size());

		structure.sections().forEach(section -> {
			System.out.println("--------------------");
			System.out.println("Heading: " + section.heading());
			System.out.println("Content: " + section.content());
			System.out.println("Start line: " + section.startLine());
			System.out.println("End line: " + section.endLine());
		});

		assertTrue(structure.structured());
		assertEquals(4, structure.sections().size());
	}

	@Test
	void shouldNotTreatEveryUppercaseLineAsAHeading() {

		String text = """
                The system uses REST APIs.
                API INTEGRATION IS IMPORTANT FOR THIS SYSTEM.
                PostgreSQL stores application data.
                """;

		DocumentStructure structure =
				structureAnalyzer.analyze(text);

		System.out.println(structure);

		assertFalse(structure.structured());
	}

	@Test
	void shouldChunkStructuredDocumentBySections() {

		ExtractedDocument document = new ExtractedDocument(
				"resume.txt",
				"text/plain",
				"""
                Bhuvan Savant

                EDUCATION
                BMS College of Engineering

                TECHNICAL SKILLS
                Java, Python, Spring Boot

                WORK EXPERIENCE
                Software Engineer at Honeywell
                """
		);

		assertTrue(headingBasedChunkingStrategy.supports(document));

		List<DocumentChunk> chunks =
				headingBasedChunkingStrategy.chunk(document);

		chunks.forEach(chunk -> {
			System.out.println("--------------------");
			System.out.println("Chunk Index: " + chunk.chunkIndex());
			System.out.println("Section: " + chunk.section());
			System.out.println("Content: " + chunk.content());
		});

		assertFalse(chunks.isEmpty());

		assertTrue(chunks.stream().anyMatch(
				chunk -> chunk.content().contains("TECHNICAL SKILLS")
		));

		assertTrue(chunks.stream().anyMatch(
				chunk -> chunk.content().contains("WORK EXPERIENCE")
		));
	}

	@Test
	void shouldSelectHeadingBasedStrategyForStructuredDocument() {

		ExtractedDocument document = new ExtractedDocument(
				"resume.txt",
				"text/plain",
				"""
                EDUCATION
                BMS College of Engineering

                TECHNICAL SKILLS
                Java, Python, Spring Boot

                WORK EXPERIENCE
                Software Engineer at Honeywell
                """
		);

		List<DocumentChunk> chunks =
				documentChunkingService.chunk(document);

		chunks.forEach(chunk ->
				System.out.println(chunk.content())
		);

		assertFalse(chunks.isEmpty());

		assertTrue(chunks.stream().anyMatch(
				chunk -> chunk.content().contains("TECHNICAL SKILLS")
		));
	}

	@Test
	void shouldFallbackToTokenStrategyForUnstructuredDocument() {

		ExtractedDocument document = new ExtractedDocument(
				"notes.txt",
				"text/plain",
				"""
                The system uses REST APIs.
                Services communicate with each other.
                PostgreSQL stores application data.
                Redis is used for caching.
                """
		);

		assertFalse(headingBasedChunkingStrategy.supports(document));
		assertTrue(tokenBasedChunkingStrategy.supports(document));

		List<DocumentChunk> chunks =
				documentChunkingService.chunk(document);

		assertFalse(chunks.isEmpty());

		chunks.forEach(chunk -> {
			System.out.println("--------------------");
			System.out.println("Chunk Index: " + chunk.chunkIndex());
			System.out.println("Section: " + chunk.section());
			System.out.println("Content: " + chunk.content());
		});
	}
}
