package com.dominAItionBackend.repository;

import com.dominAItionBackend.models.BugReport;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BugReportRepository extends MongoRepository<BugReport, String> {
}
