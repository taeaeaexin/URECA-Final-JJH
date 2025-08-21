package com.ureca.ocean.jjh.batch.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ureca.ocean.jjh.batch.launcher.UserUsageStatJobLauncher;

@RestController
@RequestMapping("/api/map")
public class StatJobTriggerController {

    private final UserUsageStatJobLauncher jobLauncher;

    public StatJobTriggerController(UserUsageStatJobLauncher jobLauncher) {
        this.jobLauncher = jobLauncher;
    }

    @PostMapping("/stat-job/run")
    public ResponseEntity<String> runAllStatJobs() {
        try {
            jobLauncher.runAllJobs();
            return ResponseEntity.ok("All stat jobs started successfully.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Job execution failed: " + e.getMessage());
        }
    }
}

