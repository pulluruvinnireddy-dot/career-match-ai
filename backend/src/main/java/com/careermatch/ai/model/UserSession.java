package com.careermatch.ai.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserSession {
    private Long chatId;
    private String username;
    private String state; // IDLE, AWAITING_RESUME, AWAITING_JOB_DESCRIPTION, INTERVIEW_MODE

    private final List<ResumeDocument> resumes = new ArrayList<>();
    private JobDescription jobDescription;
    private final List<AtsAnalysisResult> analysisResults = new ArrayList<>();

    private AtsAnalysisResult latestAnalysis;
    private AtsAnalysisResult previousAnalysis;

    private int currentVersion = 1;
    private int interviewQuestionIndex = 0;
    private LocalDateTime lastActivity;

    public UserSession(Long chatId, String username) {
        this.chatId = chatId;
        this.username = username;
        this.state = "IDLE";
        this.lastActivity = LocalDateTime.now();
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<ResumeDocument> getResumes() {
        return resumes;
    }

    public void addResume(ResumeDocument doc) {
        this.resumes.add(doc);
    }

    public JobDescription getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(JobDescription jobDescription) {
        this.jobDescription = jobDescription;
    }

    public List<AtsAnalysisResult> getAnalysisResults() {
        return analysisResults;
    }

    public void addAnalysisResult(AtsAnalysisResult result) {
        this.previousAnalysis = this.latestAnalysis;
        this.latestAnalysis = result;
        this.analysisResults.add(result);
    }

    public AtsAnalysisResult getLatestAnalysis() {
        return latestAnalysis;
    }

    public AtsAnalysisResult getPreviousAnalysis() {
        return previousAnalysis;
    }

    public int getCurrentVersion() {
        return currentVersion;
    }

    public void incrementVersion() {
        this.currentVersion++;
    }

    public int getInterviewQuestionIndex() {
        return interviewQuestionIndex;
    }

    public void setInterviewQuestionIndex(int interviewQuestionIndex) {
        this.interviewQuestionIndex = interviewQuestionIndex;
    }

    public void incrementInterviewQuestion() {
        this.interviewQuestionIndex++;
    }

    public LocalDateTime getLastActivity() {
        return lastActivity;
    }

    public void touch() {
        this.lastActivity = LocalDateTime.now();
    }

    public void reset() {
        this.state = "IDLE";
        this.resumes.clear();
        this.jobDescription = null;
        this.analysisResults.clear();
        this.latestAnalysis = null;
        this.previousAnalysis = null;
        this.currentVersion = 1;
        this.interviewQuestionIndex = 0;
        this.lastActivity = LocalDateTime.now();
    }
}
