package ru.random.walk.club_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.random.walk.util.KeyRateLimiter;

import java.time.Duration;
import java.util.UUID;

@Configuration
public class RateLimiterConfig {
    @Bean
    public KeyRateLimiter<UUID> uploadPhotoForClubRateLimiter(
            @Value("${rate-limiter.uploadPhotoForClubRateLimiter.period-duration}")
            Duration period
    ) {
        return new KeyRateLimiter<>(period);
    }

    @Bean
    public KeyRateLimiter<String> getClubPhotoRateLimiter(
            @Value("${rate-limiter.getClubPhotoUserRateLimiter.period-duration}")
            Duration period
    ) {
        return new KeyRateLimiter<>(period);
    }

    @Bean
    public KeyRateLimiter<UUID> setAnswerStatusToSentUserRateLimiter(
            @Value("${rate-limiter.setAnswerStatusToSentUserRateLimiter.period-duration}")
            Duration period
    ) {
        return new KeyRateLimiter<>(period);
    }
}
