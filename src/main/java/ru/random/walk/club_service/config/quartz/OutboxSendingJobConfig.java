package ru.random.walk.club_service.config.quartz;

import lombok.RequiredArgsConstructor;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.random.walk.club_service.service.job.OutboxSendingJob;

@Configuration
@RequiredArgsConstructor
public class OutboxSendingJobConfig {
    @Bean
    public JobDetail outboxSendingJobDetail() {
        return JobBuilder.newJob()
                .storeDurably()
                .withIdentity("OutboxSendingJob")
                .ofType(OutboxSendingJob.class)
                .build();
    }

    @Bean
    public Trigger outboxSendingJobTrigger(
            @Qualifier("outboxSendingJobDetail") JobDetail outboxSendingJobDetail,
            @Value("${outbox.job.startIntervalInSeconds}") Integer secondsInterval
    ) {
        return TriggerBuilder.newTrigger()
                .forJob(outboxSendingJobDetail)
                .withIdentity(outboxSendingJobDetail.getKey().getName())
                .withSchedule(
                        SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInSeconds(secondsInterval)
                                .repeatForever()
                )
                .build();
    }
}
