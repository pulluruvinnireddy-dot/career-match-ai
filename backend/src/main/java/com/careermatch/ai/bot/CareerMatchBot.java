package com.careermatch.ai.bot;

import com.careermatch.ai.analyzer.AtsEngine;
import com.careermatch.ai.analyzer.JobDescriptionAnalyzer;
import com.careermatch.ai.config.TelegramBotConfig;
import com.careermatch.ai.model.AtsAnalysisResult;
import com.careermatch.ai.model.JobDescription;
import com.careermatch.ai.model.ResumeDocument;
import com.careermatch.ai.model.UserSession;
import com.careermatch.ai.parser.ResumeParserService;
import com.careermatch.ai.service.CareerCoachService;
import com.careermatch.ai.service.ResumeComparisonService;
import com.careermatch.ai.service.UserSessionService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.*;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.*;
import com.pengrad.telegrambot.response.GetFileResponse;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CareerMatchBot {

    private static final Logger log = LoggerFactory.getLogger(CareerMatchBot.class);

    private final TelegramBotConfig botConfig;
    private final UserSessionService sessionService;
    private final ResumeParserService parserService;
    private final JobDescriptionAnalyzer jdAnalyzer;
    private final AtsEngine atsEngine;
    private final CareerCoachService coachService;
    private final ResumeComparisonService comparisonService;

    private TelegramBot telegramBot;

    public CareerMatchBot(TelegramBotConfig botConfig,
                          UserSessionService sessionService,
                          ResumeParserService parserService,
                          JobDescriptionAnalyzer jdAnalyzer,
                          AtsEngine atsEngine,
                          CareerCoachService coachService,
                          ResumeComparisonService comparisonService) {
        this.botConfig = botConfig;
        this.sessionService = sessionService;
        this.parserService = parserService;
        this.jdAnalyzer = jdAnalyzer;
        this.atsEngine = atsEngine;
        this.coachService = coachService;
        this.comparisonService = comparisonService;
    }

    @PostConstruct
    public void init() {
        if (botConfig.isConfigured()) {
            log.info("Connecting to Telegram API with username: @{}", botConfig.getBotUsername());
            this.telegramBot = new TelegramBot(botConfig.getBotToken().trim());
            this.telegramBot.setUpdatesListener(updates -> {
                for (Update update : updates) {
                    try {
                        processUpdate(update);
                    } catch (Exception e) {
                        log.error("Error processing Telegram update ID: {}", update.updateId(), e);
                    }
                }
                return UpdatesListener.CONFIRMED_UPDATES_ALL;
            });
            log.info("CareerMatch AI Telegram Bot is online and actively polling!");
        } else {
            log.warn("CareerMatchBot loaded in STANDBY mode (TELEGRAM_BOT_TOKEN not configured).");
        }
    }

    @PreDestroy
    public void cleanup() {
        if (this.telegramBot != null) {
            log.info("Stopping Telegram Bot polling...");
            this.telegramBot.removeGetUpdatesListener();
        }
    }

    public void processUpdate(Update update) {
        // 1. Handle Inline Button Callbacks
        if (update.callbackQuery() != null) {
            handleCallbackQuery(update.callbackQuery());
            return;
        }

        if (update.message() == null) {
            return;
        }

        Message message = update.message();
        Long chatId = message.chat().id();
        String username = message.from() != null ? message.from().username() : "User";
        UserSession session = sessionService.getOrCreateSession(chatId, username);
        session.touch();

        // 2. Handle File / Resume Document Uploads
        if (message.document() != null) {
            handleDocumentUpload(chatId, message.document(), session);
            return;
        }

        // 3. Handle Text Messages & Commands
        if (message.text() != null) {
            String text = message.text().trim();
            handleTextMessage(chatId, text, session);
        }
    }

    private void handleDocumentUpload(Long chatId, Document document, UserSession session) {
        String fileName = document.fileName() != null ? document.fileName() : "resume.pdf";
        Long fileSize = document.fileSize() != null ? document.fileSize() : 0L;

        sendTextMessage(chatId, "⏳ *Downloading and parsing:* `" + fileName + "`...");

        try {
            GetFile getFile = new GetFile(document.fileId());
            GetFileResponse fileResponse = telegramBot.execute(getFile);

            if (!fileResponse.isOk() || fileResponse.file() == null) {
                sendTextMessage(chatId, "❌ *Download Failed:* Could not retrieve file from Telegram servers. Please try uploading again.");
                return;
            }

            byte[] fileBytes = telegramBot.getFileContent(fileResponse.file());
            ResumeParserService.ParseResult parseResult = parserService.extractText(fileName, fileBytes);

            if (!parseResult.isSuccess()) {
                sendTextMessage(chatId, "⚠️ *Parsing Error:*\n\n" + parseResult.getErrorMessage()
                        + "\n\n💡 *Action Needed:* Please upload a text-selectable PDF or DOCX file.");
                return;
            }

            int version = session.getCurrentVersion();
            ResumeDocument resumeDoc = new ResumeDocument(fileName, document.fileId(), parseResult.getText(), version, fileSize);
            session.addResume(resumeDoc);
            session.incrementVersion();

            // Acknowledgment
            StringBuilder sb = new StringBuilder();
            sb.append("✅ *Resume Successfully Added!*\n\n");
            sb.append("📄 *File:* `").append(fileName).append("`\n");
            sb.append("🔖 *Version:* `V").append(version).append("`\n");
            sb.append("📊 *Total Resumes in Session:* ").append(session.getResumes().size()).append("\n\n");

            if (session.getJobDescription() == null) {
                sb.append("👉 *Next Step:* Paste the target job description into this chat (or click *💼 Job Description*).");
            } else {
                sb.append("👉 Target role is set to: *").append(session.getJobDescription().getTargetRole()).append("*\n");
                sb.append("Click *📊 Analyze Resume* below to run the ATS diagnostic!");
            }

            sendTextMessage(chatId, sb.toString());

        } catch (Exception e) {
            log.error("Failed downloading Telegram document", e);
            sendTextMessage(chatId, "❌ *Error:* An unexpected error occurred while processing `" + fileName + "`: " + e.getMessage());
        }
    }

    private void handleTextMessage(Long chatId, String text, UserSession session) {
        log.info("Message from chatId [{}]: {}", chatId, text);

        // Core Commands
        if (text.equalsIgnoreCase("/start")) {
            sendWelcomeMessage(chatId, session);
            return;
        }

        if (text.equalsIgnoreCase("/help") || text.equals("❓ Help")) {
            sendHelpMessage(chatId);
            return;
        }

        if (text.equalsIgnoreCase("/reset")) {
            session.reset();
            sendTextMessage(chatId, "🔄 *Session Reset Successful*\n\nAll stored resumes, job descriptions, and audit history have been cleared.");
            return;
        }

        if (text.equalsIgnoreCase("/compare") || text.equals("📚 Compare Resumes") || text.equalsIgnoreCase("Compare my resumes")) {
            handleCompareResumes(chatId, session);
            return;
        }

        if (text.equalsIgnoreCase("/improve") || text.equals("🔄 Improve Resume")) {
            handleImproveResume(chatId, session);
            return;
        }

        if (text.equalsIgnoreCase("/interview") || text.equals("🎤 Interview Prep") || text.toLowerCase().contains("interview")) {
            handleInterviewPrep(chatId, session, text);
            return;
        }

        if (text.equalsIgnoreCase("/roadmap") || text.equals("📚 Learning Roadmap") || text.toLowerCase().contains("roadmap")) {
            handleRoadmap(chatId, session);
            return;
        }

        if (text.equalsIgnoreCase("/skillgap") || text.equals("🧩 Skill Gaps") || text.toLowerCase().contains("skill gap")) {
            handleSkillGap(chatId, session);
            return;
        }

        if (text.equalsIgnoreCase("/history") || text.equals("📜 Analysis History")) {
            handleHistory(chatId, session);
            return;
        }

        // Action Buttons
        switch (text) {
            case "📄 Upload Resume":
                session.setState("AWAITING_RESUME");
                sendTextMessage(chatId, "📄 *Resume Upload Mode*\n\nPlease attach your resume PDF directly in this chat.\n\n"
                        + "💡 *Multi-Resume Support:* You can upload multiple versions (e.g. `Resume_V1.pdf`, `Resume_V2.pdf`) and compare them!");
                return;

            case "💼 Job Description":
                session.setState("AWAITING_JOB_DESCRIPTION");
                sendTextMessage(chatId, "💼 *Job Description Mode*\n\nPlease paste the target job description text directly into this chat.");
                return;

            case "📊 Analyze Resume":
            case "/analyze":
                handleAnalyzeCommand(chatId, session);
                return;
        }

        // If in AWAITING_JOB_DESCRIPTION state or text looks like a Job Description (> 100 characters)
        if ("AWAITING_JOB_DESCRIPTION".equals(session.getState()) || text.length() > 120) {
            JobDescription jd = jdAnalyzer.analyze(text);
            session.setJobDescription(jd);
            session.setState("IDLE");

            StringBuilder sb = new StringBuilder();
            sb.append("🎯 *JOB DESCRIPTION PROCESSED*\n\n");
            sb.append("💼 *Role:* ").append(jd.getTargetRole()).append("\n");
            if (jd.getCompany() != null) {
                sb.append("🏢 *Company:* ").append(jd.getCompany()).append("\n");
            }
            sb.append("📚 *Education:* ").append(jd.getEducation()).append("\n");
            sb.append("💼 *Experience:* ").append(jd.getExperience()).append("\n\n");

            sb.append("💻 *Required Skills (").append(jd.getRequiredSkills().size()).append("):*\n");
            for (String skill : jd.getRequiredSkills()) {
                sb.append("• `").append(skill).append("`\n");
            }

            if (!jd.getPreferredSkills().isEmpty()) {
                sb.append("\n⭐ *Preferred Skills:*\n");
                for (String skill : jd.getPreferredSkills()) {
                    sb.append("• `").append(skill).append("`\n");
                }
            }

            sb.append("\n👉 *Next Step:* ");
            if (session.getResumes().isEmpty()) {
                sb.append("Send your resume PDF to calculate the match!");
            } else {
                sb.append("You have ").append(session.getResumes().size())
                        .append(" resume(s) uploaded. Click *📊 Analyze Resume* to generate the ATS diagnostic report!");
            }

            sendTextMessage(chatId, sb.toString());
            return;
        }

        // Natural Language AI Coach queries
        if (session.getLatestAnalysis() != null) {
            ResumeDocument latestDoc = session.getResumes().isEmpty() ? null : session.getResumes().get(session.getResumes().size() - 1);
            String coachReply = coachService.handleNaturalQuestion(text, latestDoc, session.getLatestAnalysis(), session.getJobDescription());
            sendTextMessage(chatId, coachReply);
            return;
        }

        // Fallback response
        sendTextMessage(chatId, "🤖 I received: \"" + text + "\"\n\nUse the buttons below to upload your resume PDF or set the target job description.");
    }

    private void handleAnalyzeCommand(Long chatId, UserSession session) {
        if (session.getResumes().isEmpty()) {
            sendTextMessage(chatId, "⚠️ *No Resume Uploaded!*\n\nPlease upload at least one resume PDF using *📄 Upload Resume* first.");
            return;
        }

        if (session.getJobDescription() == null) {
            sendTextMessage(chatId, "⚠️ *No Job Description Found!*\n\nPlease paste your target job description or click *💼 Job Description* before analyzing.");
            return;
        }

        List<ResumeDocument> docs = session.getResumes();
        JobDescription jd = session.getJobDescription();

        if (docs.size() > 1) {
            StringBuilder multiHeader = new StringBuilder();
            multiHeader.append("📚 *").append(docs.size()).append(" RESUMES RECEIVED*\n\n");
            for (ResumeDocument doc : docs) {
                AtsAnalysisResult r = atsEngine.analyze(doc, jd);
                session.addAnalysisResult(r);
                multiHeader.append("• *").append(doc.getFileName()).append(" (V").append(doc.getVersion()).append("):* ")
                        .append(r.getAtsScore()).append("/100\n");
            }
            multiHeader.append("\n_Sending detailed diagnostic report for each resume below..._");
            sendTextMessage(chatId, multiHeader.toString());
        }

        // Send detailed analysis for each resume
        for (ResumeDocument doc : docs) {
            AtsAnalysisResult result = atsEngine.analyze(doc, jd);
            if (docs.size() == 1) {
                session.addAnalysisResult(result);
            }
            sendAtsReport(chatId, result);
        }

        // If user uploaded a new version compared to previous version, show improvement tracker
        if (session.getPreviousAnalysis() != null) {
            String trackerMsg = comparisonService.trackImprovement(session.getPreviousAnalysis(), session.getLatestAnalysis());
            sendTextMessage(chatId, trackerMsg);
        }
    }

    private void sendAtsReport(Long chatId, AtsAnalysisResult result) {
        StringBuilder sb = new StringBuilder();
        sb.append("📄 *RESUME ANALYSIS*\n\n");
        sb.append("📄 *Resume:* `").append(result.getResumeName()).append("` (V").append(result.getVersion()).append(")\n");
        sb.append("🎯 *Target Role:* ").append(result.getTargetRole()).append("\n");
        if (result.getCompany() != null) {
            sb.append("🏢 *Company:* ").append(result.getCompany()).append("\n");
        }
        sb.append("📊 *ATS SCORE:* *").append(result.getAtsScore()).append("/100*\n\n");

        sb.append("📌 *SCORE BREAKDOWN:*\n");
        sb.append("• Keyword Match: ").append(result.getKeywordMatchPct()).append("%\n");
        sb.append("• Technical Skills: ").append(result.getTechSkillsMatchPct()).append("%\n");
        sb.append("• Education: ").append(result.getEducationMatchPct()).append("%\n");
        sb.append("• Experience: ").append(result.getExperienceMatchPct()).append("%\n");
        sb.append("• Projects: ").append(result.getProjectMatchPct()).append("%\n\n");

        sb.append("🟢 *MATCHING SKILLS (").append(result.getMatchingSkills().size()).append("):*\n");
        if (result.getMatchingSkills().isEmpty()) {
            sb.append("• None detected in exact keyword form\n");
        } else {
            for (String skill : result.getMatchingSkills()) {
                sb.append("• `").append(skill).append("`\n");
            }
        }

        sb.append("\n🔴 *MISSING SKILLS (").append(result.getMissingSkills().size()).append("):*\n");
        if (result.getMissingSkills().isEmpty()) {
            sb.append("• None! All target skills are covered.\n");
        } else {
            for (String skill : result.getMissingSkills()) {
                sb.append("• `").append(skill).append("`\n");
            }
        }

        sb.append("\n💪 *STRENGTHS:*\n");
        for (String s : result.getStrengths()) {
            sb.append("• ").append(s).append("\n");
        }

        sb.append("\n⚠️ *WEAK AREAS:*\n");
        for (String w : result.getWeakAreas()) {
            sb.append("• ").append(w).append("\n");
        }

        sb.append("\n💡 *IMPROVEMENT SUGGESTIONS:*\n");
        int idx = 1;
        for (String sugg : result.getSuggestions()) {
            sb.append(idx++).append(". ").append(sugg).append("\n");
        }

        sb.append("\n📝 *RESUME CONTENT IMPROVEMENTS:*\n");
        sb.append("• *SUMMARY:* ").append(result.getSummaryImprovement()).append("\n\n");
        sb.append("• *PROJECTS:* ").append(result.getProjectsImprovement()).append("\n\n");
        sb.append("• *SKILLS:* ").append(result.getSkillsImprovement()).append("\n\n");

        sb.append("🩺 *RESUME HEALTH CHECK:*\n");
        result.getHealthCheck().forEach((k, v) -> sb.append("• ").append(k).append(": ").append(v).append("\n"));

        sb.append("\n").append(coachService.generateNextBestAction(result));
        sb.append("\n_Would you like to upload an improved version for another analysis?_");

        // Action Inline Buttons
        InlineKeyboardMarkup inlineMarkup = new InlineKeyboardMarkup(
                new InlineKeyboardButton[]{
                        new InlineKeyboardButton("🧩 Skill Gaps").callbackData("SKILL_GAPS"),
                        new InlineKeyboardButton("✍️ Improve Resume").callbackData("IMPROVE_RESUME")
                },
                new InlineKeyboardButton[]{
                        new InlineKeyboardButton("🔄 Compare Resumes").callbackData("COMPARE_RESUMES"),
                        new InlineKeyboardButton("🎤 Interview Prep").callbackData("INTERVIEW_PREP")
                },
                new InlineKeyboardButton[]{
                        new InlineKeyboardButton("📚 Learning Roadmap").callbackData("LEARNING_ROADMAP"),
                        new InlineKeyboardButton("📜 History").callbackData("HISTORY")
                }
        );

        sendTextMessageWithInlineKeyboard(chatId, sb.toString(), inlineMarkup);
    }

    private void handleCallbackQuery(CallbackQuery cb) {
        String data = cb.data();
        Long chatId = cb.message().chat().id();
        UserSession session = sessionService.getOrCreateSession(chatId, cb.from() != null ? cb.from().username() : "User");

        telegramBot.execute(new AnswerCallbackQuery(cb.id()));

        switch (data) {
            case "SKILL_GAPS":
                handleSkillGap(chatId, session);
                break;
            case "IMPROVE_RESUME":
                handleImproveResume(chatId, session);
                break;
            case "COMPARE_RESUMES":
                handleCompareResumes(chatId, session);
                break;
            case "INTERVIEW_PREP":
                handleInterviewPrep(chatId, session, "");
                break;
            case "LEARNING_ROADMAP":
                handleRoadmap(chatId, session);
                break;
            case "HISTORY":
                handleHistory(chatId, session);
                break;
        }
    }

    private void handleCompareResumes(Long chatId, UserSession session) {
        if (session.getAnalysisResults().size() < 2 && session.getResumes().size() < 2) {
            sendTextMessage(chatId, "⚠️ *Need at least 2 Resumes:*\nUpload another resume PDF to see a side-by-side comparison of ATS scores and skills!");
            return;
        }

        List<AtsAnalysisResult> list = session.getAnalysisResults();
        if (list.size() < session.getResumes().size() && session.getJobDescription() != null) {
            // Re-analyze all
            list.clear();
            for (ResumeDocument doc : session.getResumes()) {
                list.add(atsEngine.analyze(doc, session.getJobDescription()));
            }
        }

        String comparison = comparisonService.compareResumes(list);
        sendTextMessage(chatId, comparison);
    }

    private void handleImproveResume(Long chatId, UserSession session) {
        if (session.getLatestAnalysis() == null) {
            sendTextMessage(chatId, "⚠️ Please upload and analyze a resume first using *📊 Analyze Resume*.");
            return;
        }

        AtsAnalysisResult r = session.getLatestAnalysis();
        StringBuilder sb = new StringBuilder();
        sb.append("✍️ *RESUME REWRITER & ACTIONABLE POLISH*\n\n");
        sb.append("📌 *Rewritten Summary:*\n").append(r.getSummaryImprovement()).append("\n\n");
        sb.append("📌 *Rewritten Project Bullet:*\n").append(r.getProjectsImprovement()).append("\n\n");
        sb.append("📌 *Skills Organization:*\n").append(r.getSkillsImprovement()).append("\n\n");
        sb.append("💡 *Next Step:* Update your resume in Word/Google Docs, export as PDF, and upload *Resume V")
                .append(session.getCurrentVersion()).append(".pdf* to see your score increase!");

        sendTextMessage(chatId, sb.toString());
    }

    private void handleSkillGap(Long chatId, UserSession session) {
        if (session.getLatestAnalysis() == null || session.getJobDescription() == null) {
            sendTextMessage(chatId, "⚠️ Please run an analysis first by uploading a resume and target job description.");
            return;
        }
        String gapText = coachService.generateSkillGapAnalysis(session.getLatestAnalysis(), session.getJobDescription());
        sendTextMessage(chatId, gapText);
    }

    private void handleRoadmap(Long chatId, UserSession session) {
        if (session.getLatestAnalysis() == null) {
            sendTextMessage(chatId, "⚠️ Please analyze your resume against a job description first to tailor your roadmap.");
            return;
        }
        String roadmap = coachService.generateLearningRoadmap(session.getLatestAnalysis());
        sendTextMessage(chatId, roadmap);
    }

    private void handleInterviewPrep(Long chatId, UserSession session, String userText) {
        if (session.getLatestAnalysis() == null || session.getJobDescription() == null) {
            sendTextMessage(chatId, "⚠️ Please submit a resume and target job description first so I can tailor your interview questions.");
            return;
        }

        if (userText.toLowerCase().contains("one question") || userText.toLowerCase().contains("one at a time")) {
            session.setState("INTERVIEW_MODE");
            session.setInterviewQuestionIndex(1);
            sendTextMessage(chatId, "🎤 *Mock Interview Started!*\n\n*Question 1 of 5:*\n"
                    + "Can you explain how you design and scale RESTful microservices under high transaction concurrency? Walk me through database caching and connection pooling.\n\n"
                    + "_Type your answer below and I will evaluate it!_");
            return;
        }

        if ("INTERVIEW_MODE".equals(session.getState())) {
            int qIndex = session.getInterviewQuestionIndex();
            session.incrementInterviewQuestion();

            if (qIndex == 1) {
                sendTextMessage(chatId, "👍 *Good Answer!* You clearly touched on caching and API structure.\n\n"
                        + "💡 *Coach Feedback:* Consider mentioning specific latency metrics (e.g. p99 response time) and Redis in-memory stores.\n\n"
                        + "*Question 2 of 5:*\n"
                        + "How do you handle database migration and backward compatibility when rolling out new schema changes?");
                return;
            } else if (qIndex >= 2 && qIndex < 5) {
                sendTextMessage(chatId, "🎯 *Solid Response!* Strong grasp of engineering best practices.\n\n"
                        + "*Question " + (qIndex + 1) + " of 5:*\n"
                        + "Describe a situation where a production service failed or threw unhandled errors. How did you diagnose the root cause?");
                return;
            } else {
                session.setState("IDLE");
                sendTextMessage(chatId, "🎉 *Mock Interview Complete!*\n\nYou demonstrated strong technical communication. You are ready for live technical screenings!");
                return;
            }
        }

        String prep = coachService.generateInterviewPreparation(session.getLatestAnalysis(), session.getJobDescription());
        sendTextMessage(chatId, prep);
    }

    private void handleHistory(Long chatId, UserSession session) {
        List<AtsAnalysisResult> history = session.getAnalysisResults();
        if (history.isEmpty()) {
            sendTextMessage(chatId, "📜 *Analysis History*\n\nNo audits have been executed in your current session yet.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("📜 *ANALYSIS AUDIT HISTORY*\n\n");
        int count = 1;
        for (AtsAnalysisResult r : history) {
            sb.append(count++).append(". *").append(r.getResumeName()).append(" (V").append(r.getVersion()).append(")*\n");
            sb.append("   • Target: ").append(r.getTargetRole()).append("\n");
            sb.append("   • ATS Score: *").append(r.getAtsScore()).append("/100*\n");
            sb.append("   • Matched Skills: ").append(r.getMatchingSkills().size()).append(" | Missing: ").append(r.getMissingSkills().size()).append("\n\n");
        }

        sendTextMessage(chatId, sb.toString());
    }

    private void sendWelcomeMessage(Long chatId, UserSession session) {
        String welcome = "👋 *Welcome to CareerMatch AI!*\n\n"
                + "I am your personal AI-powered Resume ATS & Career Coach.\n\n"
                + "🎯 *How It Works:*\n"
                + "1️⃣ *Send Resume(s):* Upload one or multiple resume PDFs.\n"
                + "2️⃣ *Send Job Description:* Paste target job requirements or attach them.\n"
                + "3️⃣ *Get ATS Diagnostic Report:* I will analyze every resume and provide your ATS match score, matching skills, missing keywords, weaknesses, and a concrete action plan!\n\n"
                + "Use the menu buttons below to begin:";

        sendTextMessage(chatId, welcome);
    }

    private void sendHelpMessage(Long chatId) {
        String help = "📖 *CareerMatch AI - Help & Commands*\n\n"
                + "*Commands:*\n"
                + "• `/start` - Start or restart the bot & view main menu\n"
                + "• `/help` - Display this command guide\n"
                + "• `/analyze` - Trigger ATS analysis on uploaded resumes\n"
                + "• `/compare` - Side-by-side multi-resume comparison\n"
                + "• `/improve` - Track improvements across resume versions\n"
                + "• `/skillgap` - Deep-dive into missing skills & learning tasks\n"
                + "• `/interview` - AI interview preparation & mock questions\n"
                + "• `/roadmap` - Personalized 4-week learning roadmap\n"
                + "• `/history` - View previous resume audits\n"
                + "• `/reset` - Clear current session\n\n"
                + "*Natural Questions you can ask anytime:*\n"
                + "• \"Why is my score low?\"\n"
                + "• \"What should I improve?\"\n"
                + "• \"Improve my summary\"\n"
                + "• \"What skills am I missing?\"\n"
                + "• \"Ask me one question at a time\" (Mock Interview)";

        sendTextMessage(chatId, help);
    }

    private void sendTextMessage(Long chatId, String text) {
        sendTextMessageWithInlineKeyboard(chatId, text, null);
    }

    private void sendTextMessageWithInlineKeyboard(Long chatId, String text, InlineKeyboardMarkup inlineMarkup) {
        if (telegramBot == null) {
            log.info("[STANDBY MODE] Simulated Telegram message to chatId [{}]:\n{}", chatId, text);
            return;
        }

        Keyboard replyKeyboardMarkup = new ReplyKeyboardMarkup(
                new KeyboardButton[]{
                        new KeyboardButton("📄 Upload Resume"),
                        new KeyboardButton("💼 Job Description")
                },
                new KeyboardButton[]{
                        new KeyboardButton("📊 Analyze Resume"),
                        new KeyboardButton("📚 Compare Resumes")
                },
                new KeyboardButton[]{
                        new KeyboardButton("🔄 Improve Resume"),
                        new KeyboardButton("🎤 Interview Prep")
                },
                new KeyboardButton[]{
                        new KeyboardButton("📜 Analysis History"),
                        new KeyboardButton("❓ Help")
                }
        ).resizeKeyboard(true);

        SendMessage message = new SendMessage(chatId, text)
                .replyMarkup(inlineMarkup != null ? inlineMarkup : replyKeyboardMarkup)
                .parseMode(ParseMode.Markdown);

        telegramBot.execute(message);
    }

    public boolean isConnected() {
        return telegramBot != null;
    }
}
