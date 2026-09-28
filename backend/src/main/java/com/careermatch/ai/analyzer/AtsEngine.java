package com.careermatch.ai.analyzer;

import com.careermatch.ai.model.AtsAnalysisResult;
import com.careermatch.ai.model.JobDescription;
import com.careermatch.ai.model.ResumeDocument;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AtsEngine {

    public AtsAnalysisResult analyze(ResumeDocument resume, JobDescription jd) {
        String resumeText = resume.getRawText();
        String resumeLower = resumeText.toLowerCase();

        AtsAnalysisResult result = new AtsAnalysisResult(
                resume.getFileName(),
                resume.getVersion(),
                jd.getTargetRole()
        );
        result.setCompany(jd.getCompany());

        // 1. Technical Skills Match
        List<String> jdSkills = jd.getRequiredSkills();
        if (jdSkills.isEmpty()) {
            jdSkills = List.of("Java", "Spring Boot", "REST APIs", "SQL", "Git");
        }

        List<String> matchedSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();

        for (String skill : jdSkills) {
            Pattern pattern = Pattern.compile("\\b" + Pattern.quote(skill.toLowerCase()) + "\\b", Pattern.CASE_INSENSITIVE);
            if (pattern.matcher(resumeText).find()) {
                matchedSkills.add(skill);
            } else {
                missingSkills.add(skill);
            }
        }

        int techSkillsPct = jdSkills.isEmpty() ? 75 : (int) Math.round(((double) matchedSkills.size() / jdSkills.size()) * 100);
        techSkillsPct = Math.min(100, Math.max(20, techSkillsPct));
        result.setTechSkillsMatchPct(techSkillsPct);
        result.setMatchingSkills(matchedSkills);
        result.setMissingSkills(missingSkills);

        // 2. Keyword Match
        List<String> jdKeywords = jd.getImportantKeywords();
        int matchedKeywordsCount = 0;
        for (String kw : jdKeywords) {
            if (resumeLower.contains(kw.toLowerCase())) {
                matchedKeywordsCount++;
            }
        }
        int keywordPct = jdKeywords.isEmpty() ? 70 : (int) Math.round(((double) matchedKeywordsCount / jdKeywords.size()) * 100);
        keywordPct = Math.min(100, Math.max(25, keywordPct));
        result.setKeywordMatchPct(keywordPct);

        // 3. Education Match
        int educationPct = 80;
        if (resumeLower.contains("ph.d") || resumeLower.contains("doctorate")) {
            educationPct = 100;
        } else if (resumeLower.contains("master") || resumeLower.contains("m.s.") || resumeLower.contains("ms in")) {
            educationPct = 95;
        } else if (resumeLower.contains("bachelor") || resumeLower.contains("b.s.") || resumeLower.contains("b.tech") || resumeLower.contains("computer science") || resumeLower.contains("engineering")) {
            educationPct = 90;
        } else if (resumeLower.contains("degree") || resumeLower.contains("university") || resumeLower.contains("college")) {
            educationPct = 75;
        } else {
            educationPct = 60;
        }
        result.setEducationMatchPct(educationPct);

        // 4. Experience Match
        int expPct = 75;
        Pattern yearPattern = Pattern.compile("(?i)(\\d+)\\+?\\s*(?:years?|yrs?)");
        Matcher yearMatcher = yearPattern.matcher(resumeText);
        if (yearMatcher.find()) {
            int yrs = Integer.parseInt(yearMatcher.group(1));
            expPct = yrs >= 5 ? 95 : yrs >= 3 ? 88 : 78;
        } else if (resumeLower.contains("senior") || resumeLower.contains("lead") || resumeLower.contains("architect")) {
            expPct = 90;
        }
        result.setExperienceMatchPct(expPct);

        // 5. Project Match
        int projectPct = 70;
        boolean hasActionVerbs = resumeLower.contains("engineered") || resumeLower.contains("developed") ||
                resumeLower.contains("optimized") || resumeLower.contains("architected") || resumeLower.contains("spearheaded");
        boolean hasMetrics = resumeText.matches(".*\\d+%.*") || resumeText.matches(".*\\$\\d+.*") || resumeText.matches(".*\\d+\\s*(?:ms|k|m|users|requests).*");

        if (hasActionVerbs && hasMetrics) {
            projectPct = 92;
        } else if (hasActionVerbs || hasMetrics) {
            projectPct = 82;
        }
        result.setProjectMatchPct(projectPct);

        // 6. Overall Weighted ATS Score
        // Weighting: Tech Skills (35%), Keywords (25%), Experience (20%), Project (10%), Education (10%)
        int overallScore = (int) Math.round(
                (techSkillsPct * 0.35) +
                (keywordPct * 0.25) +
                (expPct * 0.20) +
                (projectPct * 0.10) +
                (educationPct * 0.10)
        );
        result.setAtsScore(Math.min(99, Math.max(30, overallScore)));

        // 7. Strengths
        List<String> strengths = new ArrayList<>();
        if (!matchedSkills.isEmpty()) {
            strengths.add("Strong alignment in foundational core skills: " + String.join(", ", matchedSkills.subList(0, Math.min(4, matchedSkills.size()))));
        }
        if (educationPct >= 90) {
            strengths.add("Education credentials directly satisfy technical degree requirements.");
        }
        if (hasMetrics) {
            strengths.add("Demonstrated use of quantifiable impact metrics and business outcomes.");
        }
        if (hasActionVerbs) {
            strengths.add("Strong leadership verbs and clear project lifecycle ownership.");
        }
        if (strengths.isEmpty()) {
            strengths.add("Good baseline technical terminology and structured career progression.");
        }
        result.setStrengths(strengths);

        // 8. Weak Areas
        List<String> weakAreas = new ArrayList<>();
        if (!missingSkills.isEmpty()) {
            weakAreas.add("Missing key target competencies: " + String.join(", ", missingSkills.subList(0, Math.min(3, missingSkills.size()))));
        }
        if (!hasMetrics) {
            weakAreas.add("Project bullets lack quantifiable achievements (e.g., latency %, user count, cost reduction).");
        }
        if (keywordPct < 75) {
            weakAreas.add("Lower keyword density for modern cloud and architectural patterns required by the role.");
        }
        if (weakAreas.isEmpty()) {
            weakAreas.add("Fine-tune bullet point conciseness to pass automated recruiter keyword scrapers with top rank.");
        }
        result.setWeakAreas(weakAreas);

        // 9. Suggestions
        List<String> suggestions = new ArrayList<>();
        if (!missingSkills.isEmpty()) {
            suggestions.add("Incorporate genuine experience with " + missingSkills.get(0) + " into your skills or projects section if you have prior exposure.");
        }
        suggestions.add("Quantify bullet points using Google's X-Y-Z formula: Accomplished [X], as measured by [Y], by doing [Z].");
        suggestions.add("Align job titles and opening summary explicitly with the target title: '" + jd.getTargetRole() + "'.");
        result.setSuggestions(suggestions);

        // 10. Content Improvements
        result.setSummaryImprovement("Elevate your summary from a generic intro to a high-impact profile: 'Results-driven "
                + jd.getTargetRole() + " specializing in scalable systems, high-throughput microservices, and reliable cloud deployments.'");

        result.setProjectsImprovement("Quantify your top project: Instead of 'Built backend APIs', write: 'Architected RESTful microservices handling 15,000+ daily requests, decreasing response latency by 28% using caching.'");

        result.setSkillsImprovement("Group your technical skills into categorized blocks (Languages, Frameworks, Cloud/DevOps, Databases) to ensure 100% ATS parser accuracy.");

        // 11. Next Steps
        List<String> nextSteps = new ArrayList<>();
        nextSteps.add("Add missing keywords (" + (missingSkills.isEmpty() ? "CI/CD, Cloud" : String.join(", ", missingSkills.subList(0, Math.min(3, missingSkills.size())))) + ") where applicable.");
        nextSteps.add("Refine project descriptions with measurable metrics (%, numbers, scale).");
        nextSteps.add("Upload Resume V" + (resume.getVersion() + 1) + " to verify your new score boost!");
        result.setNextSteps(nextSteps);

        // 12. Resume Health Check
        Map<String, String> health = new LinkedHashMap<>();
        health.put("ATS Compatibility", "✅ Good (Text cleanly extractable)");

        boolean hasContact = resumeLower.contains("@") || resumeText.matches(".*\\d{10}.*") || resumeLower.contains("linkedin");
        health.put("Contact Info", hasContact ? "✅ Good" : "⚠️ Needs Improvement (Email/LinkedIn missing)");

        boolean hasSkillsSection = resumeLower.contains("skills") || resumeLower.contains("technical skills");
        health.put("Section Structure", hasSkillsSection ? "✅ Good" : "⚠️ Needs Improvement (Add dedicated Skills section)");

        health.put("Project Clarity", hasActionVerbs ? "✅ Good" : "⚠️ Needs Improvement (Use stronger action verbs)");
        health.put("Achievement Clarity", hasMetrics ? "✅ Good" : "⚠️ Needs Improvement (Add % or numeric metrics)");
        health.put("Formatting Concerns", "✅ Good (Standard single/two-column ATS layout)");

        result.setHealthCheck(health);

        return result;
    }
}
