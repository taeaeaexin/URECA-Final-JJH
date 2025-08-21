package com.ureca.ocean.jjh.batch.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ureca.ocean.jjh.batch.launcher.UserUsageStatJobLauncher;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserUsageStatScheduler {

    private final UserUsageStatJobLauncher jobLauncher;

    // 매일 새벽 3시 실행 (cron: 초 분 시 일 월 요일)
    @Scheduled(cron = "0 0 3 * * *")
    public void runAllUserUsageJobs() throws Exception {
        jobLauncher.runAllJobs();
    }
}
