package com.careermatch.ai.service;

import com.careermatch.ai.model.AtsAnalysisResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResumeComparisonService {

    public String compareResumes(List<AtsAnalysisResult> results) {
        if (results == null || results.isEmpty()) {
            return "⚠️ No resumes available to compare. Please upload at least two resumes first.";
        }

        if (results.size() == 1) {
            return "⚠️ Only 1 resume is available (" + results.get(0).getResumeName() + " - " + results.get(0).getAtsScore() + "/100). Please upload a second resume to run a comparison.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("📊 *RESUME COMPARISON MATRIX*\n\n");
        sb.append("Comparing ").append(results.size()).append(" resumes against target role: *").append(results.get(0).getTargetRole()).append("*\n\n");

        sb.append("```\n");
        sb.append(String.format("%-18s | %-5s | %-6s | %-6s\n", "Resume", "ATS", "Skills", "Keywds"));
        sb.append("-------------------|-------|--------|-------\n");
        for (AtsAnalysisResult r : results) {
            String name = r.getResumeName();
            if (name.length() > 18) name = name.substring(0, 15) + "...";
            sb.append(String.format("%-18s | %-5s | %-6s | %-6s\n",
                    name,
                    r.getAtsScore() + "/100",
                    r.getTechSkillsMatchPct() + "%",
                    r.getKeywordMatchPct() + "%"));
        }
        sb.append("```\n\n");

        sb.append("📌 *Measurable Differences:*\n\n");
        for (int i = 0; i < results.size(); i++) {
            AtsAnalysisResult r = results.get(i);
            sb.append("📄 *").append(r.getResumeName()).append(" (V").append(r.getVersion()).append(")*\n");
            sb.append("• *ATS Score:* ").append(r.getAtsScore()).append("/100\n");
            sb.append("• *Matched Skills:* ").append(r.getMatchingSkills().size()).append(" (").append(String.join(", ", r.getMatchingSkills())).append(")\n");
            sb.append("• *Missing Skills:* ").append(r.getMissingSkills().size()).append(" (").append(String.join(", ", r.getMissingSkills())).append(")\n");
            sb.append("• *Project Relevance:* ").append(r.getProjectMatchPct()).append("%\n\n");
        }

        sb.append("💡 *Key Observation:* ");
        AtsAnalysisResult highest = results.stream().max((a, b) -> Integer.compare(a.getAtsScore(), b.getAtsScore())).orElse(results.get(0));
        AtsAnalysisResult lowest = results.stream().min((a, b) -> Integer.compare(a.getAtsScore(), b.getAtsScore())).orElse(results.get(0));

        if (highest != lowest) {
            int diff = highest.getAtsScore() - lowest.getAtsScore();
            sb.append("`").append(highest.getResumeName()).append("` scores ").append(diff).append(" points higher primarily due to higher technical skill density and stronger keyword match for the target role.\n");
        } else {
            sb.append("Resumes have comparable keyword and technical match percentages.\n");
        }

        return sb.toString();
    }

    public String trackImprovement(AtsAnalysisResult oldResult, AtsAnalysisResult newResult) {
        StringBuilder sb = new StringBuilder();
        int scoreDiff = newResult.getAtsScore() - oldResult.getAtsScore();
        String diffSymbol = scoreDiff >= 0 ? "+" + scoreDiff : String.valueOf(scoreDiff);

        sb.append("🔄 *RESUME IMPROVEMENT TRACKER*\n\n");
        sb.append("Comparing *V").append(oldResult.getVersion()).append("* ➡️ *V").append(newResult.getVersion()).append("*\n\n");
        sb.append("• *Previous Score:* ").append(oldResult.getAtsScore()).append("/100\n");
        sb.append("• *New Score:* ").append(newResult.getAtsScore()).append("/100\n");
        sb.append("• *Score Change:* *").append(diffSymbol).append(" points*\n\n");

        sb.append("📈 *Improved Dimensions:*\n");
        if (newResult.getTechSkillsMatchPct() > oldResult.getTechSkillsMatchPct()) {
            sb.append("✅ Technical Skills Match (+").append(newResult.getTechSkillsMatchPct() - oldResult.getTechSkillsMatchPct()).append("%)\n");
        }
        if (newResult.getKeywordMatchPct() > oldResult.getKeywordMatchPct()) {
            sb.append("✅ Keyword Density (+").append(newResult.getKeywordMatchPct() - oldResult.getKeywordMatchPct()).append("%)\n");
        }
        if (newResult.getProjectMatchPct() > oldResult.getProjectMatchPct()) {
            sb.append("✅ Project Relevance & Metrics (+").append(newResult.getProjectMatchPct() - oldResult.getProjectMatchPct()).append("%)\n");
        }
        if (scoreDiff == 0) {
            sb.append("➖ Scores remained consistent between these two uploads.\n");
        }

        sb.append("\n🔴 *Still Missing:*\n");
        if (newResult.getMissingSkills().isEmpty()) {
            sb.append("🎉 All target skills are covered!\n");
        } else {
            for (String missing : newResult.getMissingSkills()) {
                sb.append("❌ `").append(missing).append("`\n");
            }
        }

        sb.append("\n💡 *Next Improvement Recommendation:*\n");
        sb.append("Focus on adding relevant missing skills only when they genuinely match your real-world experience. Next, refine project metrics to target 90%+ ATS score!");

        return sb.toString();
    }
}
