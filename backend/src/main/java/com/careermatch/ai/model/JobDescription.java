package com.careermatch.ai.model;

import java.util.ArrayList;
import java.util.List;

public class JobDescription {
    private String rawText;
    private String targetRole;
    private String company;
    private List<String> requiredSkills = new ArrayList<>();
    private List<String> preferredSkills = new ArrayList<>();
    private String education;
    private String experience;
    private List<String> importantKeywords = new ArrayList<>();
    private List<String> responsibilities = new ArrayList<>();

    public JobDescription(String rawText) {
        this.rawText = rawText;
    }

    public String getRawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    public String getTargetRole() {
        return targetRole != null ? targetRole : "Software Engineer";
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

    public List<String> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(List<String> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public List<String> getPreferredSkills() {
        return preferredSkills;
    }

    public void setPreferredSkills(List<String> preferredSkills) {
        this.preferredSkills = preferredSkills;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public List<String> getImportantKeywords() {
        return importantKeywords;
    }

    public void setImportantKeywords(List<String> importantKeywords) {
        this.importantKeywords = importantKeywords;
    }

    public List<String> getResponsibilities() {
        return responsibilities;
    }

    public void setResponsibilities(List<String> responsibilities) {
        this.responsibilities = responsibilities;
    }
}
