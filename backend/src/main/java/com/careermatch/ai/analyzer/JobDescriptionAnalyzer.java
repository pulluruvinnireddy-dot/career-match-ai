package com.careermatch.ai.analyzer;

import com.careermatch.ai.model.JobDescription;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class JobDescriptionAnalyzer {

    public static final List<String> COMMON_SKILLS = List.of(
            "Java", "Python", "JavaScript", "TypeScript", "C++", "C#", "Go", "Rust", "PHP", "Kotlin", "Swift",
            "Spring Boot", "Spring", "React", "React.js", "Angular", "Vue", "Node.js", "Express", "Django", "Flask",
            "MySQL", "PostgreSQL", "MongoDB", "Redis", "Oracle", "Elasticsearch", "Cassandra", "SQL",
            "Docker", "Kubernetes", "AWS", "Azure", "GCP", "CI/CD", "Jenkins", "GitHub Actions", "Terraform", "Linux",
            "Kafka", "RabbitMQ", "REST APIs", "RESTful", "GraphQL", "Microservices", "Hibernate", "JPA",
            "Git", "Maven", "Gradle", "JUnit", "Unit Testing", "System Design", "Distributed Systems", "Agile", "Scrum"
    );

    private static final List<String> ROLE_PATTERNS = List.of(
            "(?i)(?:role|position|job title|title)[:\\t ]+([A-Za-z0-9 /\\-]+)",
            "(?i)(Senior|Lead|Staff|Principal|Junior|Mid-Level)?\\s*(Full Stack|Backend|Frontend|Software|Java|Cloud|DevOps|Data|Mobile)\\s*(Engineer|Developer|Architect|Specialist)"
    );

    public JobDescription analyze(String rawText) {
        JobDescription jd = new JobDescription(rawText);

        // 1. Extract Role
        jd.setTargetRole(extractRole(rawText));

        // 2. Extract Company
        jd.setCompany(extractCompany(rawText));

        // 3. Extract Education
        jd.setEducation(extractEducation(rawText));

        // 4. Extract Experience
        jd.setExperience(extractExperience(rawText));

        // 5. Extract Skills (Required vs Preferred)
        extractSkills(rawText, jd);

        // 6. Extract Responsibilities
        jd.setResponsibilities(extractResponsibilities(rawText));

        // 7. Important Keywords
        jd.setImportantKeywords(extractKeywords(rawText, jd.getRequiredSkills()));

        return jd;
    }

    private String extractRole(String text) {
        for (String patternStr : ROLE_PATTERNS) {
            Matcher m = Pattern.compile(patternStr).matcher(text);
            if (m.find()) {
                String match = m.group(0).trim();
                match = match.replaceAll("(?i)^(?:role|position|job title|title)[:\\t ]+", "").trim();
                match = match.split("[\\r\\n]")[0].trim();
                if (match.length() > 3 && match.length() < 50) {
                    return match;
                }
            }
        }
        // Fallback to inspecting first non-empty lines
        String[] lines = text.split("\\n");
        for (int i = 0; i < Math.min(lines.length, 3); i++) {
            String line = lines[i].trim();
            if (line.toLowerCase().contains("engineer") || line.toLowerCase().contains("developer")) {
                return line.length() > 50 ? line.substring(0, 50) : line;
            }
        }
        return "Software Engineer";
    }

    private String extractCompany(String text) {
        Pattern p = Pattern.compile("(?i)(?:at|company|client)[:\\s]+([A-Z][A-Za-z0-9&.,\\s]+?)(?:\\n|\\.|,)");
        Matcher m = p.matcher(text);
        if (m.find()) {
            String company = m.group(1).trim();
            if (company.length() > 2 && company.length() < 40) {
                return company;
            }
        }
        return null;
    }

    private String extractEducation(String text) {
        String lower = text.toLowerCase();
        if (lower.contains("ph.d") || lower.contains("doctorate")) {
            return "Ph.D. in Computer Science or relevant technical field";
        } else if (lower.contains("master") || lower.contains("ms in") || lower.contains("m.s.")) {
            return "Master's or Bachelor's in Computer Science, Software Engineering or equivalent";
        } else if (lower.contains("bachelor") || lower.contains("bs in") || lower.contains("b.s.") || lower.contains("b.tech") || lower.contains("degree")) {
            return "Bachelor's Degree in Computer Science, STEM or equivalent professional experience";
        }
        return "Degree in Computer Science, Engineering, or equivalent practical experience";
    }

    private String extractExperience(String text) {
        Pattern p = Pattern.compile("(?i)(\\d+\\+?\\s*(?:to|-)?\\s*\\d*\\s*(?:years?|yrs?)(?:\\s+of)?(?:\\s+experience)?)");
        Matcher m = p.matcher(text);
        if (m.find()) {
            return m.group(1).trim() + " required";
        }
        return "3+ years of software development experience";
    }

    private void extractSkills(String text, JobDescription jd) {
        Set<String> required = new LinkedHashSet<>();
        Set<String> preferred = new LinkedHashSet<>();

        String[] lines = text.split("\\n");
        boolean inPreferredSection = false;

        for (String line : lines) {
            String trimmed = line.trim();
            String lowerLine = trimmed.toLowerCase();

            if (lowerLine.contains("nice to have") || lowerLine.contains("preferred") || lowerLine.contains("bonus") || lowerLine.contains("plus:")) {
                inPreferredSection = true;
            } else if (lowerLine.contains("required") || lowerLine.contains("requirements") || lowerLine.contains("qualifications") || lowerLine.contains("must have")) {
                inPreferredSection = false;
            }

            for (String skill : COMMON_SKILLS) {
                Pattern pattern = Pattern.compile("\\b" + Pattern.quote(skill) + "\\b", Pattern.CASE_INSENSITIVE);
                if (pattern.matcher(line).find()) {
                    if (inPreferredSection || lowerLine.contains("nice to have") || lowerLine.contains("plus")) {
                        preferred.add(skill);
                    } else {
                        required.add(skill);
                    }
                }
            }
        }

        // If a skill appears in both, required takes precedence
        preferred.removeAll(required);

        jd.setRequiredSkills(new ArrayList<>(required));
        jd.setPreferredSkills(new ArrayList<>(preferred));
    }

    private List<String> extractResponsibilities(String text) {
        List<String> list = new ArrayList<>();
        String[] lines = text.split("\\n");
        boolean inRespSection = false;

        for (String line : lines) {
            String trimmed = line.trim();
            String lower = trimmed.toLowerCase();
            if (lower.contains("responsibilities") || lower.contains("what you will do") || lower.contains("duties")) {
                inRespSection = true;
                continue;
            }
            if (inRespSection && (lower.contains("qualifications") || lower.contains("requirements") || lower.contains("skills"))) {
                break;
            }
            if (inRespSection && (trimmed.startsWith("-") || trimmed.startsWith("•") || trimmed.startsWith("*"))) {
                String clean = trimmed.replaceAll("^[\\-•*\\s]+", "");
                if (clean.length() > 10) {
                    list.add(clean);
                }
            }
        }

        if (list.isEmpty()) {
            list.add("Design, develop, and maintain scalable microservices and APIs.");
            list.add("Collaborate in cross-functional agile teams to deliver production features.");
            list.add("Optimize application performance, database queries, and test coverage.");
        }

        return list;
    }

    private List<String> extractKeywords(String text, List<String> skills) {
        Set<String> keywords = new LinkedHashSet<>(skills);
        List<String> extraTerms = List.of(
                "Scalability", "High Availability", "Code Review", "Agile", "Security",
                "Performance Optimization", "Database Architecture", "CI/CD Automation",
                "Cloud Deployment", "Test-Driven Development (TDD)"
        );

        for (String term : extraTerms) {
            if (text.toLowerCase().contains(term.toLowerCase())) {
                keywords.add(term);
            }
        }

        return new ArrayList<>(keywords);
    }
}
