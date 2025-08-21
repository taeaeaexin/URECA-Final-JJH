package com.ureca.ocean.jjh.batch.launcher;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class UserUsageStatJobLauncher{

    private final JobLauncher jobLauncher;
    
    @Qualifier("userUsageStatJob")
    private final Job userUsageStatJob;
    
    @Qualifier("userCategoryUsageJob")
    private final Job userCategoryUsageJob;
    
    @Qualifier("userRegionUsageStatJob")
    private final Job userRegionUsageStatJob;
    
    @Qualifier("userWeekdayUsageStatJob")
    private final Job userWeekdayUsageStatJob;
    
    @Qualifier("userHourlyUsageStatJob")
    private final Job userHourlyUsageStatJob;
    
    @Qualifier("userBrandUsageStatJob")
    private final Job userBrandUsageStatJob;
    
    @Qualifier("userStoreUsageStatJob")
    private final Job userStoreUsageStatJob;

    public UserUsageStatJobLauncher(
            JobLauncher jobLauncher,
            @Qualifier("userUsageStatJob") Job userUsageStatJob,
            @Qualifier("userCategoryUsageJob") Job userCategoryUsageJob,
            @Qualifier("userRegionUsageStatJob") Job userRegionUsageStatJob,
            @Qualifier("userWeekdayUsageStatJob") Job userWeekdayUsageStatJob,
            @Qualifier("userHourlyUsageStatJob") Job userHourlyUsageStatJob,
            @Qualifier("userBrandUsageStatJob") Job userBrandUsageStatJob,
            @Qualifier("userStoreUsageStatJob") Job userStoreUsageStatJob) {
        this.jobLauncher = jobLauncher;
        this.userUsageStatJob = userUsageStatJob;
        this.userCategoryUsageJob = userCategoryUsageJob;
        this.userRegionUsageStatJob = userRegionUsageStatJob;
        this.userWeekdayUsageStatJob = userWeekdayUsageStatJob;
        this.userHourlyUsageStatJob = userHourlyUsageStatJob;
        this.userBrandUsageStatJob = userBrandUsageStatJob;
        this.userStoreUsageStatJob = userStoreUsageStatJob;
    }
    
    public void runAllJobs() throws Exception {
        JobParameters usageParams = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(userUsageStatJob, usageParams);

        jobLauncher.run(userCategoryUsageJob, nextParams(1));
        jobLauncher.run(userRegionUsageStatJob, nextParams(2));
        jobLauncher.run(userWeekdayUsageStatJob, nextParams(3));
        jobLauncher.run(userHourlyUsageStatJob, nextParams(4));
        jobLauncher.run(userBrandUsageStatJob, nextParams(5));
        jobLauncher.run(userStoreUsageStatJob, nextParams(6));
    }

    private JobParameters nextParams(int offset) {
        return new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis() + offset)
                .toJobParameters();
    }

}


