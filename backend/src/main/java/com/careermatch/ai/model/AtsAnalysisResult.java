package com.careermatch.ai.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AtsAnalysisResult {
    private String resumeName;
    private int version;
    private String targetRole;
    private String company;
    private int atsScore;
    private int keywordMatchPct;
    private int techSkillsMatchPct;
    private int educationMatchPct;
    private int experienceMatchPct;
    private int projectMatchPct;

    private List<String> matchingSkills = new ArrayList<>();
    private List<String> missingSkills = new ArrayList<>();
    private List<String> strengths = new ArrayList<>();
    private List<String> weakAreas = new ArrayList<>();
    private List<String> suggestions = new ArrayList<>();

    private String summaryImprovement;
    private String projectsImprovement;
    private String skillsImprovement;
    private List<String> nextSteps = new ArrayList<>();

    // Resume Health Check
    private Map<String, String> healthCheck = new LinkedHashMap<>();
    private LocalDateTime timestamp = LocalDateTime.now();

    public AtsAnalysisResult(String resumeName, int version, String targetRole) {
        this.resumeName = resumeName;
        this.version = version;
        this.targetRole = targetRole;
    }

    public String getResumeName() {
        return resumeName;
    }

    public void setResumeName(String resumeName) {
        this.resumeName = resumeName;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public int getAtsScore() {
        return atsScore;
    }

    public void setAtsScore(int atsScore) {
        this.atsScore = atsScore;
    }

    public int getKeywordMatchPct() {
        return keywordMatchPct;
    }

    public void setKeywordMatchPct(int keywordMatchPct) {
        this.keywordMatchPct = keywordMatchPct;
    }

    public int getTechSkillsMatchPct() {
        return techSkillsMatchPct;
    }

    public void setTechSkillsMatchPct(int techSkillsMatchPct) {
        this.techSkillsMatchPct = techSkillsMatchPct;
    }

    public int getEducationMatchPct() {
        return educationMatchPct;
    }

    public void setEducationMatchPct(int educationMatchPct) {
        this.educationMatchPct = educationMatchPct;
    }

    public int getExperienceMatchPct() {
        return experienceMatchPct;
    }

    public void setExperienceMatchPct(int experienceMatchPct) {
        this.experienceMatchPct = experienceMatchPct;
    }

    public int getProjectMatchPct() {
        return projectMatchPct;
    }

    public void setProjectMatchPct(int projectMatchPct) {
        this.projectMatchPct = projectMatchPct;
    }

    public List<String> getMatchingSkills() {
        return matchingSkills;
    }

    public void setMatchingSkills(List<String> matchingSkills) {
        this.matchingSkills = matchingSkills;
    }

    public List<String> getMissingSkills() {
        return missingSkills;
    }

    public void setMissingSkills(List<String> missingSkills) {
        this.missingSkills = missingSkills;
    }

    public List<String> getStrengths() {
        return strengths;
    }

    public void setStrengths(List<String> strengths) {
        this.strengths = strengths;
    }

    public List<String> getWeakAreas() {
        return weakAreas;
    }

    public void setWeakAreas(List<String> weakAreas) {
        this.weakAreas = weakAreas;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    public String getSummaryImprovement() {
        return summaryImprovement;
    }

    public void setSummaryImprovement(String summaryImprovement) {
        this.summaryImprovement = summaryImprovement;
    }

    public String getProjectsImprovement() {
        return projectsImprovement;
    }

    public void setProjectsImprovement(String projectsImprovement) {
        this.projectsImprovement = projectsImprovement;
    }

    public String getSkillsImprovement() {
        return skillsImprovement;
    }

    public void setSkillsImprovement(String skillsImprovement) {
        this.skillsImprovement = skillsImprovement;
    }

    public List<String> getNextSteps() {
        return nextSteps;
    }

    public void setNextSteps(List<String> nextSteps) {
        this.nextSteps = nextSteps;
    }

    public Map<String, String> getHealthCheck() {
        return healthCheck;
    }

    public void setHealthCheck(Map<String, String> healthCheck) {
        this.healthCheck = healthCheck;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
