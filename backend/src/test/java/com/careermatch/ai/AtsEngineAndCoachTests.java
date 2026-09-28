package com.careermatch.ai;

import com.careermatch.ai.analyzer.AtsEngine;
import com.careermatch.ai.analyzer.JobDescriptionAnalyzer;
import com.careermatch.ai.model.AtsAnalysisResult;
import com.careermatch.ai.model.JobDescription;
import com.careermatch.ai.model.ResumeDocument;
import com.careermatch.ai.parser.ResumeParserService;
import com.careermatch.ai.service.CareerCoachService;
import com.careermatch.ai.service.ResumeComparisonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AtsEngineAndCoachTests {

    private JobDescriptionAnalyzer jdAnalyzer;
    private AtsEngine atsEngine;
    private ResumeComparisonService comparisonService;
    private CareerCoachService coachService;
    private ResumeParserService parserService;

    @BeforeEach
    void setUp() {
        jdAnalyzer = new JobDescriptionAnalyzer();
        atsEngine = new AtsEngine();
        comparisonService = new ResumeComparisonService();
        coachService = new CareerCoachService();
        parserService = new ResumeParserService();
    }

    @Test
    void testJobDescriptionAnalyzer() {
        String sampleJdText = """
                Role: Senior Java Developer
                Company: Apex Systems
                We require 5+ years of experience with Java, Spring Boot, MySQL, REST APIs, and Docker.
                Nice to have: Kubernetes and AWS.
                Requirements: Bachelor's degree in Computer Science.
                Responsibilities:
                - Architect scalable microservices.
                - Optimize database queries.
                """;

        JobDescription jd = jdAnalyzer.analyze(sampleJdText);

        assertEquals("Senior Java Developer", jd.getTargetRole());
        assertEquals("Apex Systems", jd.getCompany());
        assertTrue(jd.getRequiredSkills().contains("Java"));
        assertTrue(jd.getRequiredSkills().contains("Spring Boot"));
        assertTrue(jd.getRequiredSkills().contains("MySQL"));
        assertTrue(jd.getPreferredSkills().contains("Kubernetes"));
        assertTrue(jd.getEducation().contains("Bachelor"));
    }

    @Test
    void testAtsScoringAndSkillGap() {
        JobDescription jd = new JobDescription("Job Description");
        jd.setTargetRole("Senior Java Developer");
        jd.setRequiredSkills(List.of("Java", "Spring Boot", "MySQL", "Docker", "Kubernetes", "AWS"));
        jd.setImportantKeywords(List.of("Microservices", "REST APIs", "Scalability", "CI/CD"));

        String resumeV1Text = """
                John Doe - Software Engineer
                Email: john.doe@example.com | Phone: 555-123-4567 | LinkedIn: linkedin.com/in/johndoe
                Technical Skills: Java, Spring Boot, MySQL, REST APIs, Git, JUnit
                Experience:
                Software Engineer with 4 years of experience.
                - Engineered backend REST services using Spring Boot and MySQL, improving query latency by 25%.
                - Built microservices serving 500k monthly active users.
                Education: Bachelor of Science in Computer Science.
                """;

        ResumeDocument docV1 = new ResumeDocument("Resume_V1.pdf", "file_1", resumeV1Text, 1, 102400);
        AtsAnalysisResult resultV1 = atsEngine.analyze(docV1, jd);

        assertTrue(resultV1.getAtsScore() >= 60 && resultV1.getAtsScore() <= 90);
        assertTrue(resultV1.getMatchingSkills().contains("Java"));
        assertTrue(resultV1.getMatchingSkills().contains("Spring Boot"));
        assertTrue(resultV1.getMatchingSkills().contains("MySQL"));
        assertTrue(resultV1.getMissingSkills().contains("Docker"));
        assertTrue(resultV1.getMissingSkills().contains("Kubernetes"));
        assertTrue(resultV1.getMissingSkills().contains("AWS"));

        // Verify Health Check
        assertEquals("✅ Good (Text cleanly extractable)", resultV1.getHealthCheck().get("ATS Compatibility"));
        assertEquals("✅ Good", resultV1.getHealthCheck().get("Contact Info"));
    }

    @Test
    void testResumeImprovementTracker() {
        JobDescription jd = new JobDescription("Job Description");
        jd.setTargetRole("Senior Java Developer");
        jd.setRequiredSkills(List.of("Java", "Spring Boot", "MySQL", "Docker", "AWS"));

        String v1Text = "Experience: 3 years in Java and MySQL.";
        String v2Text = "Experience: 5 years in Java, Spring Boot, MySQL, Docker, and AWS. Optimized latency by 35%.";

        ResumeDocument docV1 = new ResumeDocument("Resume_V1.pdf", "f1", v1Text, 1, 50000);
        ResumeDocument docV2 = new ResumeDocument("Resume_V2.pdf", "f2", v2Text, 2, 55000);

        AtsAnalysisResult res1 = atsEngine.analyze(docV1, jd);
        AtsAnalysisResult res2 = atsEngine.analyze(docV2, jd);

        assertTrue(res2.getAtsScore() > res1.getAtsScore(), "V2 should score higher than V1");

        String trackerOutput = comparisonService.trackImprovement(res1, res2);
        assertTrue(trackerOutput.contains("RESUME IMPROVEMENT TRACKER"));
        assertTrue(trackerOutput.contains("Score Change:"));
    }

    @Test
    void testMultiResumeComparison() {
        JobDescription jd = new JobDescription("Target JD");
        jd.setTargetRole("Backend Engineer");
        jd.setRequiredSkills(List.of("Java", "Spring Boot", "Docker"));

        ResumeDocument doc1 = new ResumeDocument("Candidate_A.pdf", "f1", "Java developer", 1, 50000);
        ResumeDocument doc2 = new ResumeDocument("Candidate_B.pdf", "f2", "Senior Java, Spring Boot, Docker specialist with 5 years experience.", 1, 60000);

        AtsAnalysisResult res1 = atsEngine.analyze(doc1, jd);
        AtsAnalysisResult res2 = atsEngine.analyze(doc2, jd);

        String comparison = comparisonService.compareResumes(List.of(res1, res2));
        assertTrue(comparison.contains("RESUME COMPARISON MATRIX"));
        assertTrue(comparison.contains("Candidate_A.pdf"));
        assertTrue(comparison.contains("Candidate_B.pdf"));
    }

    @Test
    void testAiCareerCoachQuestions() {
        JobDescription jd = new JobDescription("Target JD");
        jd.setTargetRole("Senior Cloud Engineer");
        jd.setRequiredSkills(List.of("Java", "Docker", "Kubernetes"));

        ResumeDocument doc = new ResumeDocument("Resume.pdf", "f1", "Java developer with 3 years experience", 1, 40000);
        AtsAnalysisResult result = atsEngine.analyze(doc, jd);

        String whyLow = coachService.handleNaturalQuestion("Why is my score low?", doc, result, jd);
        assertTrue(whyLow.contains("Why Your ATS Score is"));

        String summary = coachService.handleNaturalQuestion("Improve my summary", doc, result, jd);
        assertTrue(summary.contains("Suggested Professional Summary"));

        String missing = coachService.handleNaturalQuestion("What skills am I missing?", doc, result, jd);
        assertTrue(missing.contains("High-Priority Missing Skills"));

        String roadmap = coachService.generateLearningRoadmap(result);
        assertTrue(roadmap.contains("PERSONALIZED 4-WEEK LEARNING ROADMAP"));

        String interview = coachService.generateInterviewPreparation(result, jd);
        assertTrue(interview.contains("AI INTERVIEW PREPARATION COACH"));
    }

    @Test
    void testResumeParserServiceValidations() {
        // Empty file
        ResumeParserService.ParseResult emptyResult = parserService.extractText("test.pdf", new byte[0]);
        assertFalse(emptyResult.isSuccess());
        assertTrue(emptyResult.getErrorMessage().contains("empty"));

        // Unsupported extension
        ResumeParserService.ParseResult unsupported = parserService.extractText("test.exe", new byte[]{1, 2, 3});
        assertFalse(unsupported.isSuccess());
        assertTrue(unsupported.getErrorMessage().contains("Unsupported file format"));

        // Plain text
        byte[] txtBytes = "Hello World, this is a plain text resume content.".getBytes();
        ResumeParserService.ParseResult txtResult = parserService.extractText("resume.txt", txtBytes);
        assertTrue(txtResult.isSuccess());
        assertEquals("Hello World, this is a plain text resume content.", txtResult.getText());
    }
}
