package com.dominAItionBackend.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "bugReports")
public class BugReport {

    @Id
    private String id;
    private String description;
    private String steps;
    private String screenshotUrl; // optional - stored path or URL to screenshot
    private String reporterEmail; // optional - track which user reported it
    private LocalDateTime createdAt;

    public BugReport() {
        this.createdAt = LocalDateTime.now();
    }

    public BugReport(String description, String steps, String screenshotUrl, String reporterEmail) {
        this.description = description;
        this.steps = steps;
        this.screenshotUrl = screenshotUrl;
        this.reporterEmail = reporterEmail;
        this.createdAt = LocalDateTime.now();
    }

    // 🧠 Getters & setters
    public String getId() { return id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSteps() { return steps; }
    public void setSteps(String steps) { this.steps = steps; }

    public String getScreenshotUrl() { return screenshotUrl; }
    public void setScreenshotUrl(String screenshotUrl) { this.screenshotUrl = screenshotUrl; }

    public String getReporterEmail() { return reporterEmail; }
    public void setReporterEmail(String reporterEmail) { this.reporterEmail = reporterEmail; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
