package ru.random.walk.club_service.util;

public record Pair<F, S>(
        F first,
        S second
) {
    public static <F, S> Pair<F, S> of(F first, S second) {
        return new Pair<>(first, second);
    }
}
