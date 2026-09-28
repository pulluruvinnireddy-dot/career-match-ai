package com.careermatch.ai.service;

import com.careermatch.ai.model.AtsAnalysisResult;
import com.careermatch.ai.model.JobDescription;
import com.careermatch.ai.model.ResumeDocument;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CareerCoachService {

    public String generateSkillGapAnalysis(AtsAnalysisResult result, JobDescription jd) {
        StringBuilder sb = new StringBuilder();
        sb.append("🧩 *SKILL GAP ANALYSIS*\n\n");

        sb.append("*Already Have:*\n");
        if (result.getMatchingSkills().isEmpty()) {
            sb.append("• No exact skill matches detected yet.\n");
        } else {
            for (String skill : result.getMatchingSkills()) {
                sb.append("✅ `").append(skill).append("`\n");
            }
        }

        sb.append("\n*Missing Critical Skills:*\n");
        if (result.getMissingSkills().isEmpty()) {
            sb.append("🎉 None! Your resume covers all required skills in the job description.\n");
        } else {
            for (String skill : result.getMissingSkills()) {
                sb.append("❌ `").append(skill).append("`\n");
            }
        }

        if (!result.getMissingSkills().isEmpty()) {
            sb.append("\n📌 *Skill Deep Dive & Action Plan:*\n");
            int count = 0;
            for (String skill : result.getMissingSkills()) {
                if (count++ >= 3) break;
                sb.append("\n🔹 *").append(skill).append("*\n");
                sb.append("• *Why it matters:* Required for modern production microservices and enterprise ").append(jd.getTargetRole()).append(" roles.\n");
                sb.append("• *Learning Priority:* High\n");
                sb.append("• *Suggested Beginner Project:* Build a mini-project demonstrating ").append(skill)
                        .append(" (e.g., containerize your backend with Docker/K8s or connect a message queue/cache).\n");
            }
        }

        return sb.toString();
    }

    public String generateLearningRoadmap(AtsAnalysisResult result) {
        List<String> missing = result.getMissingSkills();
        String skill1 = missing.size() > 0 ? missing.get(0) : "System Architecture & Scalability";
        String skill2 = missing.size() > 1 ? missing.get(1) : "Cloud Deployment (AWS/Docker)";
        String skill3 = missing.size() > 2 ? missing.get(2) : "High-Throughput Caching & Messaging";

        StringBuilder sb = new StringBuilder();
        sb.append("📅 *PERSONALIZED 4-WEEK LEARNING ROADMAP*\n\n");

        sb.append("🗓️ *Week 1: Foundations of ").append(skill1).append("*\n");
        sb.append("• *Why it matters:* Core prerequisite frequently screened in preliminary ATS filters.\n");
        sb.append("• *Practice Task:* Set up a local development environment, follow official quickstarts, and build a small CRUD or service module.\n");
        sb.append("• *Free Resources:* Official Documentation, freeCodeCamp, MDN / Baeldung.\n\n");

        sb.append("🗓️ *Week 2: Deep Dive into ").append(skill2).append("*\n");
        sb.append("• *Why it matters:* Differentiates mid-level from senior candidates in architecture discussions.\n");
        sb.append("• *Practice Task:* Containerize your application or deploy a test instance on a free cloud tier.\n");
        sb.append("• *Free Resources:* Docker Official Getting Started, AWS Free Tier Hands-on Labs.\n\n");

        sb.append("🗓️ *Week 3: Advanced Integration with ").append(skill3).append("*\n");
        sb.append("• *Why it matters:* High demand in scalable, fault-tolerant enterprise ecosystems.\n");
        sb.append("• *Practice Task:* Implement asynchronous event streaming or in-memory caching to boost throughput.\n");
        sb.append("• *Free Resources:* Apache Kafka Documentation, Redis University (free).\n\n");

        sb.append("🗓️ *Week 4: Portfolio Project & Resume Polish*\n");
        sb.append("• *Task:* Combine the new tools into a cohesive GitHub project with an informative README and architecture diagram.\n");
        sb.append("• *Outcome:* Update your resume bullets with quantified impact and re-upload to CareerMatch AI!\n");

        return sb.toString();
    }

    public String generateInterviewPreparation(AtsAnalysisResult result, JobDescription jd) {
        StringBuilder sb = new StringBuilder();
        sb.append("🎤 *AI INTERVIEW PREPARATION COACH*\n");
        sb.append("Target Role: *").append(jd.getTargetRole()).append("*\n\n");

        sb.append("💻 *Technical Questions:*\n");
        if (!result.getMatchingSkills().isEmpty()) {
            sb.append("1. How do you optimize database performance and transaction boundaries when using Spring Boot and MySQL?\n");
            sb.append("2. Can you explain how you design RESTful APIs for idempotency and error handling under high concurrency?\n");
        } else {
            sb.append("1. Walk me through the core design patterns you implement in your primary backend language.\n");
            sb.append("2. How do you approach unit and integration testing to ensure production stability?\n");
        }

        sb.append("\n📚 *Project Architecture Questions:*\n");
        sb.append("3. Tell me about the most technically challenging feature you engineered in your recent experience.\n");
        sb.append("4. If your service experiences a 5x traffic surge, which component fails first and how do you mitigate it?\n");

        sb.append("\n🧠 *Problem Solving & System Design:*\n");
        sb.append("5. How would you design a distributed rate-limiter or notification system for 100k active users?\n");

        sb.append("\n👔 *Behavioral & HR:*\n");
        sb.append("6. Describe a situation where you had a technical disagreement with a team member. How did you resolve it?\n\n");

        sb.append("💡 *Tip:* You can type: `\"Ask me one question at a time\"` to enter interactive mock interview practice!");

        return sb.toString();
    }

    public String handleNaturalQuestion(String query, ResumeDocument resume, AtsAnalysisResult result, JobDescription jd) {
        String lower = query.toLowerCase();

        if (lower.contains("score low") || lower.contains("why is my score")) {
            return "📊 *Why Your ATS Score is " + result.getAtsScore() + "/100:*\n\n"
                    + "The ATS scoring algorithm evaluates 5 dimensions:\n"
                    + "• *Technical Skills:* " + result.getTechSkillsMatchPct() + "%\n"
                    + "• *Keywords:* " + result.getKeywordMatchPct() + "%\n"
                    + "• *Experience:* " + result.getExperienceMatchPct() + "%\n"
                    + "• *Project Relevance:* " + result.getProjectMatchPct() + "%\n"
                    + "• *Education:* " + result.getEducationMatchPct() + "%\n\n"
                    + "💡 *Main Reason:* Your resume has missing keywords ("
                    + (result.getMissingSkills().isEmpty() ? "cloud & distributed patterns" : String.join(", ", result.getMissingSkills()))
                    + ") and your project bullets could benefit from more quantifiable numbers (e.g. %, latency, scale).";
        }

        if (lower.contains("improve my summary") || lower.contains("summary")) {
            return "📝 *Suggested Professional Summary for " + jd.getTargetRole() + ":*\n\n"
                    + "\"Accomplished " + jd.getTargetRole() + " with proven experience engineering resilient backend microservices, "
                    + "optimizing relational databases, and delivering high-availability REST APIs. Experienced in driving technical agility, "
                    + "collaborating across cross-functional teams, and enhancing system reliability for high-traffic applications.\"\n\n"
                    + "💡 *Note:* Retains only factual skills and highlights your strengths.";
        }

        if (lower.contains("skills am i missing") || lower.contains("missing skills")) {
            if (result.getMissingSkills().isEmpty()) {
                return "🎉 You have matched all primary required skills in the job description!";
            }
            return "🔴 *High-Priority Missing Skills:*\n\n"
                    + String.join("\n", result.getMissingSkills().stream().map(s -> "• `" + s + "`").toList())
                    + "\n\n💡 If you have experience with these skills, add them directly into your Skills and Projects sections!";
        }

        if (lower.contains("increase") || lower.contains("boost") || lower.contains("improve")) {
            return "🚀 *How to Increase Your Score by +15-20 Points:*\n\n"
                    + "1️⃣ *Add Target Keywords:* Inject " + (result.getMissingSkills().isEmpty() ? "relevant domain terms" : String.join(", ", result.getMissingSkills())) + " into your experience.\n"
                    + "2️⃣ *Quantify Achievements:* Use the Google X-Y-Z formula (`Accomplished [X], measured by [Y]%, by doing [Z]`).\n"
                    + "3️⃣ *Match Job Title:* Ensure your resume title or summary explicitly matches `" + jd.getTargetRole() + "`.\n"
                    + "4️⃣ *Re-upload:* Save as PDF and upload Resume V" + (resume.getVersion() + 1) + "!";
        }

        return "🤖 *AI Career Coach Insights for " + jd.getTargetRole() + ":*\n\n"
                + "Based on your resume and target role:\n"
                + "• *Matched Skills:* " + String.join(", ", result.getMatchingSkills()) + "\n"
                + "• *Areas to Strengthen:* " + (result.getWeakAreas().isEmpty() ? "Add more metrics" : result.getWeakAreas().get(0)) + "\n\n"
                + "Feel free to ask me:\n"
                + "• \"Why is my score low?\"\n"
                + "• \"Improve my summary\"\n"
                + "• \"What skills am I missing?\"\n"
                + "• \"Prepare me for interview\"";
    }

    public String generateNextBestAction(AtsAnalysisResult result) {
        StringBuilder sb = new StringBuilder();
        sb.append("🚀 *NEXT BEST ACTIONS:*\n\n");
        sb.append("1️⃣ *Add relevant missing keywords* (");
        if (!result.getMissingSkills().isEmpty()) {
            sb.append(String.join(", ", result.getMissingSkills().subList(0, Math.min(3, result.getMissingSkills().size()))));
        } else {
            sb.append("Cloud & Architecture keywords");
        }
        sb.append(") if you have prior exposure.\n");
        sb.append("2️⃣ *Improve project descriptions* with metrics (%, latency, users).\n");
        sb.append("3️⃣ *Re-upload updated resume (V").append(result.getVersion() + 1).append(")* to test your improvement.\n");
        sb.append("4️⃣ *Run ATS analysis again* to measure score change.\n");
        sb.append("5️⃣ *Start interview preparation* using the 🎤 Interview Prep button below.\n");

        return sb.toString();
    }
}
