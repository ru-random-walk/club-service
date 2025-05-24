package ru.random.walk.club_service.service.rate_limiter;

import ru.random.walk.util.KeyRateLimiter;

import java.time.Duration;

public class GetClubPhotoRateLimiter extends KeyRateLimiter<String> {
    public GetClubPhotoRateLimiter(Duration period) {
        super(period);
    }
}
