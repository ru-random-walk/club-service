package ru.random.walk.club_service.service.rate_limiter;

import ru.random.walk.util.KeyRateLimiter;

import java.time.Duration;

public class UploadClubPhotoRateLimiter extends KeyRateLimiter<String> {
    public UploadClubPhotoRateLimiter(Duration period) {
        super(period);
    }
}
