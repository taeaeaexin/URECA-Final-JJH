package com.ureca.ocean.jjh.batch.config;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.ureca.ocean.jjh.batch.dto.BrandUsageDto;
import com.ureca.ocean.jjh.batch.dto.CategoryUsageDto;
import com.ureca.ocean.jjh.batch.dto.DailyUsageDto;
import com.ureca.ocean.jjh.batch.dto.HourlyUsageDto;
import com.ureca.ocean.jjh.batch.dto.MonthlyUsageDto;
import com.ureca.ocean.jjh.batch.dto.RegionUsageDto;
import com.ureca.ocean.jjh.batch.dto.StoreUsageDto;
import com.ureca.ocean.jjh.batch.dto.TotalUsageDto;
import com.ureca.ocean.jjh.batch.dto.WeekdayUsageDto;
import com.ureca.ocean.jjh.batch.dto.WeeklyUsageDto;
import com.ureca.ocean.jjh.stat.entity.UserBrandUsageStat;
import com.ureca.ocean.jjh.stat.entity.UserCategoryUsageStat;
import com.ureca.ocean.jjh.stat.entity.UserDailyUsageStat;
import com.ureca.ocean.jjh.stat.entity.UserHourlyUsageStat;
import com.ureca.ocean.jjh.stat.entity.UserMonthlyUsageStat;
import com.ureca.ocean.jjh.stat.entity.UserRegionUsageStat;
import com.ureca.ocean.jjh.stat.entity.UserStoreUsageStat;
import com.ureca.ocean.jjh.stat.entity.UserUsageStat;
import com.ureca.ocean.jjh.stat.entity.UserWeekdayUsageStat;
import com.ureca.ocean.jjh.stat.entity.UserWeeklyUsageStat;
import com.ureca.ocean.jjh.stat.entity.enums.Period;
import com.ureca.ocean.jjh.stat.repository.BrandUsageStatRepository;
import com.ureca.ocean.jjh.stat.repository.CategoryUsageStatRepository;
import com.ureca.ocean.jjh.stat.repository.DailyUsageStatRepository;
import com.ureca.ocean.jjh.stat.repository.HourlyUsageStatRepository;
import com.ureca.ocean.jjh.stat.repository.MonthlyUsageStatRepository;
import com.ureca.ocean.jjh.stat.repository.RegionUsageStatRepository;
import com.ureca.ocean.jjh.stat.repository.StoreUsageStatRepository;
import com.ureca.ocean.jjh.stat.repository.UsageStatRepository;
import com.ureca.ocean.jjh.stat.repository.WeekdayUsageStatRepository;
import com.ureca.ocean.jjh.stat.repository.WeeklyUsageStatRepository;
import com.ureca.ocean.jjh.storeUsage.repository.StoreUsageRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class UserUsageBatchConfig {

	private final StoreUsageRepository storeUsageRepository;
	private final UsageStatRepository usageStatRepository;
	private final DailyUsageStatRepository dailyUsageStatRepository;
	private final WeeklyUsageStatRepository weeklyUsageStatRepository;
	private final MonthlyUsageStatRepository monthlyUsageStatRepository;
	private final CategoryUsageStatRepository categoryUsageStatRepository;
	private final RegionUsageStatRepository regionUsageStatRepository;
	private final WeekdayUsageStatRepository weekdayUsageStatRepository;
	private final HourlyUsageStatRepository hourlyUsageStatRepository;
	private final BrandUsageStatRepository brandUsageStatRepository;
	private final StoreUsageStatRepository storeUsageStatRepository;
	
    @Bean(name = "userUsageStatJob")
    Job userUsageStatJob(JobRepository jobRepository, 
    					@Qualifier("userUsageStatStep") Step userUsageStatStep,
    					@Qualifier("userDailyUsageStatStep") Step userDailyUsageStatStep,
    					@Qualifier("userWeeklyUsageStatStep") Step userWeeklyUsageStatStep,
    					@Qualifier("userMonthlyUsageStatStep") Step userMonthlyUsageStatStep) {
		return new JobBuilder("userUsageStatJob", jobRepository)
				.start(userUsageStatStep)
				.next(userDailyUsageStatStep)
				.next(userWeeklyUsageStatStep)
				.next(userMonthlyUsageStatStep)
				.build();
	}
	
    // 1. totalUsageStat
	@Bean
	@Qualifier("userUsageStatStep")
    public Step userUsageStatStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("userUsageStatStep", jobRepository)
                .<TotalUsageDto, UserUsageStat>chunk(100, transactionManager)
                .reader(userUsageReader())
                .processor(userUsageProcessor())
                .writer(userUsageWriter())
                .build();
    }
	
	@Bean
    public ItemReader<TotalUsageDto> userUsageReader() {
        return new ListItemReader<>(storeUsageRepository.summarizeUsageByUser());
    }
	
	@Bean
    public ItemProcessor<TotalUsageDto, UserUsageStat> userUsageProcessor() {
        return dto -> {
            UserUsageStat stat = new UserUsageStat();
            stat.setUserId(dto.getUserId());
            stat.setCount(dto.getCount().intValue());
            stat.setAmount(dto.getAmount().intValue());
            stat.setUpdatedAt(LocalDateTime.now());
            return stat;
        };
    }
	
	@Bean
    public ItemWriter<UserUsageStat> userUsageWriter() {
        return items -> {
            for (UserUsageStat item : items) {
                // Upsert 방식
            	usageStatRepository.findByUserId(item.getUserId())
                        .ifPresentOrElse(existing -> {
                            existing.setCount(item.getCount());
                            existing.setAmount(item.getAmount());
                            existing.setUpdatedAt(item.getUpdatedAt());
                            usageStatRepository.save(existing);
                        }, () -> usageStatRepository.save(item));
            }
        };
    }
	
	// 2. DailyUsageStat
	@Bean
	@Qualifier("userDailyUsageStatStep")
	public Step userDailyUsageStatStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
	    return new StepBuilder("userDailyUsageStatStep", jobRepository)
	            .<DailyUsageDto, UserDailyUsageStat>chunk(100, transactionManager)
	            .reader(userDailyUsageReader())
	            .processor(userDailyUsageProcessor())
	            .writer(userDailyUsageWriter())
	            .build();
	}

	@Bean
	public ItemReader<DailyUsageDto> userDailyUsageReader() {
	    return new ListItemReader<>(storeUsageRepository.summarizeDailyUsageByUser());
	}

	@Bean
	public ItemProcessor<DailyUsageDto, UserDailyUsageStat> userDailyUsageProcessor() {
	    return dto -> {
	        UserDailyUsageStat stat = new UserDailyUsageStat();
	        stat.setUserId(dto.getUserId());
	        stat.setDate(dto.getDate());
	        stat.setCount(dto.getCount().intValue());
	        stat.setAmount(dto.getAmount().intValue());
	        stat.setUpdatedAt(LocalDateTime.now());
	        return stat;
	    };
	}

	@Bean
	public ItemWriter<UserDailyUsageStat> userDailyUsageWriter() {
	    return items -> {
	        for (UserDailyUsageStat item : items) {
	            dailyUsageStatRepository.findByUserIdAndDate(item.getUserId(), item.getDate())
	                .ifPresentOrElse(existing -> {
	                    existing.setCount(item.getCount());
	                    existing.setAmount(item.getAmount());
	                    existing.setUpdatedAt(item.getUpdatedAt());
	                    dailyUsageStatRepository.save(existing);
	                }, () -> dailyUsageStatRepository.save(item));
	        }
	    };
	}

	
	// 3. WeeklyUsageStat
	@Bean
    @Qualifier("userWeeklyUsageStatStep")
    public Step userWeeklyUsageStatStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("userWeeklyUsageStatStep", jobRepository)
                .<WeeklyUsageDto, UserWeeklyUsageStat>chunk(100, transactionManager)
                .reader(userWeeklyUsageReader())
                .processor(userWeeklyUsageProcessor())
                .writer(userWeeklyUsageWriter())
                .build();
    }

    @Bean
    public ItemReader<WeeklyUsageDto> userWeeklyUsageReader() {
        List<WeeklyUsageDto> data = storeUsageRepository.summarizeWeeklyUsageByUser(); // 쿼리 필요
        return new ListItemReader<>(data);
    }

    @Bean
    public ItemProcessor<WeeklyUsageDto, UserWeeklyUsageStat> userWeeklyUsageProcessor() {
        return dto -> {
            UserWeeklyUsageStat stat = new UserWeeklyUsageStat();
            stat.setUserId(dto.getUserId());
            stat.setYear(dto.getYear());
            stat.setMonth(dto.getMonth());
            stat.setWeek(dto.getWeek());
            stat.setCount(dto.getCount().intValue());
            stat.setAmount(dto.getAmount().intValue());
            stat.setUpdatedAt(LocalDateTime.now());
            return stat;
        };
    }

    @Bean
    public ItemWriter<UserWeeklyUsageStat> userWeeklyUsageWriter() {
        return items -> {
            for (UserWeeklyUsageStat item : items) {
                weeklyUsageStatRepository.findByUserIdAndYearAndMonthAndWeek(
                    item.getUserId(), item.getYear(), item.getMonth(), item.getWeek()
                ).ifPresentOrElse(existing -> {
                    existing.setCount(item.getCount());
                    existing.setAmount(item.getAmount());
                    existing.setUpdatedAt(item.getUpdatedAt());
                    weeklyUsageStatRepository.save(existing);
                }, () -> weeklyUsageStatRepository.save(item));
            }
        };
    }

    
    // 4. MonthlyUsageStat
    @Bean
    @Qualifier("userMonthlyUsageStatStep")
    public Step userMonthlyUsageStatStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("userMonthlyUsageStatStep", jobRepository)
                .<MonthlyUsageDto, UserMonthlyUsageStat>chunk(100, transactionManager)
                .reader(userMonthlyUsageReader())
                .processor(userMonthlyUsageProcessor())
                .writer(userMonthlyUsageWriter())
                .build();
    }

    @Bean
    public ItemReader<MonthlyUsageDto> userMonthlyUsageReader() {
        return new ListItemReader<>(storeUsageRepository.summarizeMonthlyUsageByUser());
    }

    @Bean
    public ItemProcessor<MonthlyUsageDto, UserMonthlyUsageStat> userMonthlyUsageProcessor() {
        return dto -> {
            UserMonthlyUsageStat stat = new UserMonthlyUsageStat();
            stat.setUserId(dto.getUserId());
            stat.setYear(dto.getYear());
            stat.setMonth(dto.getMonth());
            stat.setCount(dto.getCount().intValue());
            stat.setAmount(dto.getAmount().intValue());
            stat.setUpdatedAt(LocalDateTime.now());
            return stat;
        };
    }

    @Bean
    public ItemWriter<UserMonthlyUsageStat> userMonthlyUsageWriter() {
        return items -> {
            for (UserMonthlyUsageStat item : items) {
            	monthlyUsageStatRepository.findByUserIdAndYearAndMonth(
            	        item.getUserId(), item.getYear(), item.getMonth())
            	    .ifPresentOrElse(existing -> {
            	        existing.setCount(item.getCount());
            	        existing.setAmount(item.getAmount());
            	        existing.setUpdatedAt(item.getUpdatedAt());
            	        monthlyUsageStatRepository.save(existing);
            	    }, () -> monthlyUsageStatRepository.save(item));
            }
        };
    }
    
    // 5. CategoryUsageStat
    @Bean(name = "userCategoryUsageJob")
    public Job userCategoryUsageJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        JobBuilder builder = new JobBuilder("userCategoryUsageJob", jobRepository);
        Job job = builder
            .start(categoryStep(Period._7D, jobRepository, transactionManager))
            .next(categoryStep(Period._30D, jobRepository, transactionManager))
            .next(categoryStep(Period._180D, jobRepository, transactionManager))
            .next(categoryStep(Period._365D, jobRepository, transactionManager))
            .next(categoryStep(Period.ALL, jobRepository, transactionManager))
            .build();
        return job;
    }

    private Step categoryStep(Period period, JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        List<CategoryUsageDto> data = storeUsageRepository.summarizeCategoryUsageByUser(getStartDate(period));

        return new StepBuilder("categoryStep_" + period, jobRepository)
            .<CategoryUsageDto, UserCategoryUsageStat>chunk(100, transactionManager)
            .reader(new ListItemReader<>(data))
            .processor(categoryUsageProcessor(period))
            .writer(categoryUsageWriter())
            .build();
    }
    
    public ItemProcessor<CategoryUsageDto, UserCategoryUsageStat> categoryUsageProcessor(Period period) {
        return dto -> {
            var stat = new UserCategoryUsageStat();
            stat.setUserId(dto.getUserId());
            stat.setCategory(dto.getCategory());
            stat.setCount(dto.getCount().intValue());
            stat.setPeriod(period);
            stat.setUpdatedAt(LocalDateTime.now());
            return stat;
        };
    }
    
    @Bean
    public ItemWriter<UserCategoryUsageStat> categoryUsageWriter() {
        return items -> {
            for (UserCategoryUsageStat item : items) {
                categoryUsageStatRepository.findByUserIdAndPeriodAndCategory(
                    item.getUserId(), item.getPeriod(), item.getCategory()
                ).ifPresentOrElse(existing -> {
                    existing.setCount(item.getCount());
                    existing.setUpdatedAt(item.getUpdatedAt());
                    categoryUsageStatRepository.save(existing);
                }, () -> categoryUsageStatRepository.save(item));
            }
        };
    }
    
    
    // 6. RegionUsageStat
    @Bean(name = "userRegionUsageStatJob")
    public Job userRegionUsageStatJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("userRegionUsageStatJob", jobRepository)
                .start(regionStep(Period._7D, jobRepository, transactionManager))
                .next(regionStep(Period._30D, jobRepository, transactionManager))
                .next(regionStep(Period._180D, jobRepository, transactionManager))
                .next(regionStep(Period._365D, jobRepository, transactionManager))
                .next(regionStep(Period.ALL, jobRepository, transactionManager))
                .build();
    }
    
    public Step regionStep(Period period, JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        LocalDateTime startDate = getStartDate(period);
        List<RegionUsageDto> data = storeUsageRepository.summarizeRegionUsageByUser(startDate);

        return new StepBuilder("regionStep_" + period.name(), jobRepository)
                .<RegionUsageDto, UserRegionUsageStat>chunk(100, transactionManager)
                .reader(new ListItemReader<>(data))
                .processor(regionUsageProcessor(period))
                .writer(regionUsageWriter(regionUsageStatRepository))
                .build();
    }
    
    public ItemProcessor<RegionUsageDto, UserRegionUsageStat> regionUsageProcessor(Period period) {
        return dto -> {
            UserRegionUsageStat stat = new UserRegionUsageStat();
            stat.setUserId(dto.getUserId());
            stat.setRegion(dto.getRegion());
            stat.setCount(dto.getCount().intValue());
            stat.setPeriod(period);
            stat.setUpdatedAt(LocalDateTime.now());
            return stat;
        };
    }
    
    @Bean
    public ItemWriter<UserRegionUsageStat> regionUsageWriter(RegionUsageStatRepository repository) {
        return items -> {
            for (UserRegionUsageStat item : items) {
                repository.findByUserIdAndRegionAndPeriod(item.getUserId(), item.getRegion(), item.getPeriod())
                        .ifPresentOrElse(existing -> {
                            existing.setCount(item.getCount());
                            existing.setUpdatedAt(item.getUpdatedAt());
                            repository.save(existing);
                        }, () -> repository.save(item));
            }
        };
    }
    
    // 7. WeekdayUsageStat
    @Bean(name = "userWeekdayUsageStatJob")
    public Job userWeekdayUsageStatJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("userWeekdayUsageStatJob", jobRepository)
                .start(weekdayStep(Period._7D, jobRepository, transactionManager))
                .next(weekdayStep(Period._30D, jobRepository, transactionManager))
                .next(weekdayStep(Period._180D, jobRepository, transactionManager))
                .next(weekdayStep(Period._365D, jobRepository, transactionManager))
                .next(weekdayStep(Period.ALL, jobRepository, transactionManager))
                .build();
    }

    public Step weekdayStep(Period period, JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        LocalDateTime startDate = getStartDate(period);
        List<WeekdayUsageDto> data = storeUsageRepository.summarizeWeekdayUsageByUser(startDate);

        return new StepBuilder("weekdayStep_" + period.name(), jobRepository)
                .<WeekdayUsageDto, UserWeekdayUsageStat>chunk(100, transactionManager)
                .reader(new ListItemReader<>(data))
                .processor(weekdayUsageProcessor(period))
                .writer(weekdayUsageWriter(weekdayUsageStatRepository))
                .build();
    }

    public ItemProcessor<WeekdayUsageDto, UserWeekdayUsageStat> weekdayUsageProcessor(Period period) {
        return dto -> {
            UserWeekdayUsageStat stat = new UserWeekdayUsageStat();
            stat.setUserId(dto.getUserId());
            stat.setWeekdayName(engToKorDay.get(dto.getWeekdayName()));
            stat.setCount(dto.getCount().intValue());
            stat.setPeriod(period);
            stat.setUpdatedAt(LocalDateTime.now());
            return stat;
        };
    }

    @Bean
    public ItemWriter<UserWeekdayUsageStat> weekdayUsageWriter(WeekdayUsageStatRepository repository) {
        return items -> {
            for (UserWeekdayUsageStat item : items) {
                repository.findByUserIdAndWeekdayNameAndPeriod(item.getUserId(), item.getWeekdayName(), item.getPeriod())
                    .ifPresentOrElse(existing -> {
                        existing.setCount(item.getCount());
                        existing.setUpdatedAt(item.getUpdatedAt());
                        repository.save(existing);
                    }, () -> repository.save(item));
            }
        };
    }
    
    // 8. HourlyUsageStat
    @Bean(name = "userHourlyUsageStatJob")
    public Job userHourlyUsageStatJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("userHourlyUsageStatJob", jobRepository)
                .start(hourlyStep(Period._7D, jobRepository, transactionManager))
                .next(hourlyStep(Period._30D, jobRepository, transactionManager))
                .next(hourlyStep(Period._180D, jobRepository, transactionManager))
                .next(hourlyStep(Period._365D, jobRepository, transactionManager))
                .next(hourlyStep(Period.ALL, jobRepository, transactionManager))
                .build();
    }
    public Step hourlyStep(Period period, JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        LocalDateTime startDate = getStartDate(period);
        List<HourlyUsageDto> data = storeUsageRepository.summarizeHourlyUsageByUser(startDate);

        return new StepBuilder("hourlyStep_" + period.name(), jobRepository)
                .<HourlyUsageDto, UserHourlyUsageStat>chunk(100, transactionManager)
                .reader(new ListItemReader<>(data))
                .processor(hourlyUsageProcessor(period))
                .writer(hourlyUsageWriter(hourlyUsageStatRepository))
                .build();
    }
    
    public ItemProcessor<HourlyUsageDto, UserHourlyUsageStat> hourlyUsageProcessor(Period period) {
        return dto -> {
            UserHourlyUsageStat stat = new UserHourlyUsageStat();
            stat.setUserId(dto.getUserId());
            stat.setHourRange(dto.getHourRange());
            stat.setCount(dto.getCount().intValue());
            stat.setPeriod(period);
            stat.setUpdatedAt(LocalDateTime.now());
            return stat;
        };
    }
    
    @Bean
    public ItemWriter<UserHourlyUsageStat> hourlyUsageWriter(HourlyUsageStatRepository repository) {
        return items -> {
            for (UserHourlyUsageStat item : items) {
                repository.findByUserIdAndHourRangeAndPeriod(item.getUserId(), item.getHourRange(), item.getPeriod())
                    .ifPresentOrElse(existing -> {
                        existing.setCount(item.getCount());
                        existing.setUpdatedAt(item.getUpdatedAt());
                        repository.save(existing);
                    }, () -> repository.save(item));
            }
        };
    }
    
    // 9. BrandUsageStat
    @Bean(name = "userBrandUsageStatJob")
    public Job userBrandUsageStatJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("userBrandUsageStatJob", jobRepository)
                .start(brandStep(Period._7D, jobRepository, transactionManager))
                .next(brandStep(Period._30D, jobRepository, transactionManager))
                .next(brandStep(Period._180D, jobRepository, transactionManager))
                .next(brandStep(Period._365D, jobRepository, transactionManager))
                .next(brandStep(Period.ALL, jobRepository, transactionManager))
                .build();
    }

    public Step brandStep(Period period, JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        LocalDateTime startDate = getStartDate(period);
        List<BrandUsageDto> data = storeUsageRepository.summarizeBrandUsageByUser(startDate);

        return new StepBuilder("brandStep_" + period.name(), jobRepository)
                .<BrandUsageDto, UserBrandUsageStat>chunk(100, transactionManager)
                .reader(new ListItemReader<>(data))
                .processor(brandUsageProcessor(period))
                .writer(brandUsageWriter(brandUsageStatRepository))
                .build();
    }

    public ItemProcessor<BrandUsageDto, UserBrandUsageStat> brandUsageProcessor(Period period) {
        return dto -> {
            UserBrandUsageStat stat = new UserBrandUsageStat();
            stat.setUserId(dto.getUserId());
            stat.setBrandName(dto.getBrandName());
            stat.setCount(dto.getCount().intValue());
            stat.setPeriod(period);
            stat.setUpdatedAt(LocalDateTime.now());
            return stat;
        };
    }

    @Bean
    public ItemWriter<UserBrandUsageStat> brandUsageWriter(BrandUsageStatRepository repository) {
        return items -> {
            for (UserBrandUsageStat item : items) {
                repository.findByUserIdAndBrandNameAndPeriod(item.getUserId(), item.getBrandName(), item.getPeriod())
                        .ifPresentOrElse(existing -> {
                            existing.setCount(item.getCount());
                            existing.setUpdatedAt(item.getUpdatedAt());
                            repository.save(existing);
                        }, () -> repository.save(item));
            }
        };
    }

    
    // 10. StoreUsageStat
    @Bean(name = "userStoreUsageStatJob")
    public Job userStoreUsageStatJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("userStoreUsageStatJob", jobRepository)
                .start(storeStep(Period._7D, jobRepository, transactionManager))
                .next(storeStep(Period._30D, jobRepository, transactionManager))
                .next(storeStep(Period._180D, jobRepository, transactionManager))
                .next(storeStep(Period._365D, jobRepository, transactionManager))
                .next(storeStep(Period.ALL, jobRepository, transactionManager))
                .build();
    }

    
    public Step storeStep(Period period, JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        LocalDateTime startDate = getStartDate(period);
        List<StoreUsageDto> data = storeUsageRepository.summarizeStoreUsageByUser(startDate);

        return new StepBuilder("storeStep_" + period.name(), jobRepository)
                .<StoreUsageDto, UserStoreUsageStat>chunk(100, transactionManager)
                .reader(new ListItemReader<>(data))
                .processor(storeUsageProcessor(period))
                .writer(storeUsageWriter(storeUsageStatRepository))
                .build();
    }
    
    public ItemProcessor<StoreUsageDto, UserStoreUsageStat> storeUsageProcessor(Period period) {
        return dto -> {
            UserStoreUsageStat stat = new UserStoreUsageStat();
            stat.setUserId(dto.getUserId());
            stat.setStoreName(dto.getStoreName());
            stat.setCount(dto.getCount().intValue());
            stat.setPeriod(period);
            stat.setUpdatedAt(LocalDateTime.now());
            return stat;
        };
    }
    
    @Bean
    public ItemWriter<UserStoreUsageStat> storeUsageWriter(StoreUsageStatRepository repository) {
        return items -> {
            for (UserStoreUsageStat item : items) {
                repository.findByUserIdAndStoreNameAndPeriod(item.getUserId(), item.getStoreName(), item.getPeriod())
                    .ifPresentOrElse(existing -> {
                        existing.setCount(item.getCount());
                        existing.setUpdatedAt(item.getUpdatedAt());
                        repository.save(existing);
                    }, () -> repository.save(item));
            }
        };
    }

    private LocalDateTime getStartDate(Period period) {
        LocalDateTime now = LocalDateTime.now();
        return switch (period) {
            case _7D -> now.minusDays(7);
            case _30D -> now.minusDays(30);
            case _180D -> now.minusDays(180);
            case _365D -> now.minusDays(365);
            case ALL -> LocalDateTime.of(2000, 1, 1, 0, 0); // 전체 기간이라면 아주 오래 전
        };
    }
    
    Map<String, String> engToKorDay = Map.of(
    	    "Monday", "월",
    	    "Tuesday", "화",
    	    "Wednesday", "수",
    	    "Thursday", "목",
    	    "Friday", "금",
    	    "Saturday", "토",
    	    "Sunday", "일"
    	);

}
