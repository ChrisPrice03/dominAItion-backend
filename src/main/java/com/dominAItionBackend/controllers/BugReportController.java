package com.dominAItionBackend.controllers;

import com.dominAItionBackend.models.BugReport;
import com.dominAItionBackend.repository.BugReportRepository;
import com.dominAItionBackend.service.EmailService;  // ✅ import this

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/bugs")
@CrossOrigin(origins = "http://localhost:3000")  // ✅ re-enable this
public class BugReportController {

    @Autowired
    private BugReportRepository bugReportRepository;

    @Autowired
    private EmailService emailService; // ✅ inject EmailService bean

    private static final String UPLOAD_DIR = "uploads/bug-screenshots/";
    private static final String BUG_REPORT_EMAIL = "dominaitionproject@gmail.com";

    @PostMapping(value = "/report", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> reportBug(
            @RequestParam("description") String description,
            @RequestParam("steps") String steps,
            @RequestParam(value = "reporterEmail", required = false) String reporterEmail,
            @RequestParam(value = "screenshot", required = false) MultipartFile screenshot
    ) {
        try {
            String screenshotUrl = null;
            File imageFile = null;

            if (screenshot != null && !screenshot.isEmpty()) {
                File uploadDir = new File(UPLOAD_DIR);
                if (!uploadDir.exists()) uploadDir.mkdirs();

                String fileName = System.currentTimeMillis() + "_" + screenshot.getOriginalFilename();
                Path filePath = Paths.get(UPLOAD_DIR + fileName);
                Files.write(filePath, screenshot.getBytes());
                screenshotUrl = filePath.toAbsolutePath().toString();
                imageFile = filePath.toFile();
            }

            // Save bug report to DB
            BugReport report = new BugReport(description, steps, screenshotUrl, reporterEmail);
            bugReportRepository.save(report);

            // Email content with embedded image
            String subject = "New Bug Report Submitted";
            String htmlBody = """
                    <h2>New Bug Report Submitted</h2>
                    <p><b>Description:</b><br>%s</p>
                    <p><b>Steps to Reproduce:</b><br>%s</p>
                    <p><b>Reported by:</b> %s</p>
                    %s
                """.formatted(
                    description,
                    steps,
                    reporterEmail != null ? reporterEmail : "Unknown user",
                    (imageFile != null)
                        ? "<p><b>Screenshot:</b><br><img src='cid:screenshotImage' alt='Screenshot' style='max-width:600px;border:1px solid #ccc;'/></p>"
                        : "<p><b>Screenshot:</b> None attached.</p>"
                );

            emailService.sendEmailWithInlineImage("dominaitionproject@gmail.com", subject, htmlBody, imageFile);

            return ResponseEntity.ok("Bug report submitted and email sent.");

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error saving screenshot: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error sending bug report: " + e.getMessage());
        }
    }


    @GetMapping("/all")
    public ResponseEntity<?> getAllReports() {
        return ResponseEntity.ok(bugReportRepository.findAll());
    }

    @RequestMapping(value = "/report", method = RequestMethod.OPTIONS)
    public ResponseEntity<?> handlePreflight() {
        return ResponseEntity.ok().build();
    }
}
