package ru.yandex.practicum.sleeptracker.analysis;

import org.junit.jupiter.api.Test;

import ru.yandex.practicum.sleeptracker.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.SleepQuality;
import ru.yandex.practicum.sleeptracker.SleepingSession;


import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BasicSleepAnalysisTests {

    // Вспомогательный метод для создания тестовых сессий
    private SleepingSession session(
            String start,
            String end,
            SleepQuality quality
    ) {
        return new SleepingSession(
                LocalDateTime.parse(start),
                LocalDateTime.parse(end),
                quality
        );
    }

    // ===== 1. CountSessions =====

    @Test
    void countSessions_withThreeSessions_returnsThree() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T22:00", "2025-10-02T06:00", SleepQuality.GOOD),
                session("2025-10-02T23:00", "2025-10-03T07:00", SleepQuality.BAD),
                session("2025-10-03T14:00", "2025-10-03T15:00", SleepQuality.NORMAL)
        );

        SleepAnalysisResult result = new CountSessions().apply(sessions);

        assertEquals(3, result.value());
        assertEquals("Количество сессий сна", result.description());
    }

    @Test
    void countSessions_withEmptyList_returnsZero() {
        SleepAnalysisResult result = new CountSessions().apply(List.of());

        assertEquals(0, result.value());
    }

    // ===== 2. CountBadSessions =====

    @Test
    void countBadSessions_withMixedQuality_countsOnlyBad() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T22:00", "2025-10-02T06:00", SleepQuality.GOOD),
                session("2025-10-02T22:00", "2025-10-03T06:00", SleepQuality.BAD),
                session("2025-10-03T22:00", "2025-10-04T06:00", SleepQuality.BAD),
                session("2025-10-04T22:00", "2025-10-05T06:00", SleepQuality.NORMAL)
        );

        SleepAnalysisResult result = new CountBadSessions().apply(sessions);

        assertEquals(2L, result.value());
    }

    @Test
    void countBadSessions_withoutBadSessions_returnsZero() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T22:00", "2025-10-02T06:00", SleepQuality.GOOD),
                session("2025-10-02T22:00", "2025-10-03T06:00", SleepQuality.NORMAL)
        );

        SleepAnalysisResult result = new CountBadSessions().apply(sessions);

        assertEquals(0L, result.value());
    }

    @Test
    void countBadSessions_withEmptyList_returnsZero() {
        SleepAnalysisResult result = new CountBadSessions().apply(List.of());

        assertEquals(0L, result.value());
    }

    // ===== 3. FindShortestSessionDuration =====

    @Test
    void shortestDuration_withDifferentDurations_returnsMinimum() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T22:00", "2025-10-02T06:00", SleepQuality.GOOD),
                session("2025-10-02T14:00", "2025-10-02T14:45", SleepQuality.NORMAL),
                session("2025-10-02T23:00", "2025-10-03T06:00", SleepQuality.BAD)
        );

        SleepAnalysisResult result =
                new FindShortestSessionDuration().apply(sessions);

        assertEquals(45L, result.value());
    }

    @Test
    void shortestDuration_withSingleSession_returnsItsDuration() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T23:30", "2025-10-02T00:30", SleepQuality.GOOD)
        );

        SleepAnalysisResult result =
                new FindShortestSessionDuration().apply(sessions);

        assertEquals(60L, result.value());
    }

    // ===== 4. FindLongestSessionDuration =====

    @Test
    void longestDuration_withDifferentDurations_returnsMaximum() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T22:00", "2025-10-02T07:00", SleepQuality.GOOD),
                session("2025-10-02T14:00", "2025-10-02T15:00", SleepQuality.NORMAL),
                session("2025-10-02T23:00", "2025-10-03T06:00", SleepQuality.BAD)
        );

        SleepAnalysisResult result =
                new FindLongestSessionDuration().apply(sessions);

        assertEquals(540L, result.value());
    }

    @Test
    void longestDuration_withSingleSession_returnsItsDuration() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T14:00", "2025-10-01T16:00", SleepQuality.NORMAL)
        );

        SleepAnalysisResult result =
                new FindLongestSessionDuration().apply(sessions);

        assertEquals(120L, result.value());
    }

    // ===== 5. FindAvgSessionDuration =====

    @Test
    void averageDuration_withTwoSessions_returnsAverage() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T22:00", "2025-10-02T00:00", SleepQuality.GOOD),
                session("2025-10-02T14:00", "2025-10-02T15:00", SleepQuality.NORMAL)
        );

        SleepAnalysisResult result =
                new FindAvgSessionDuration().apply(sessions);

        assertEquals(90.0, result.value());
    }

    @Test
    void averageDuration_withFractionalAverage_preservesFraction() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T14:00", "2025-10-01T15:00", SleepQuality.GOOD),
                session("2025-10-02T14:00", "2025-10-02T15:01", SleepQuality.NORMAL)
        );

        SleepAnalysisResult result =
                new FindAvgSessionDuration().apply(sessions);

        assertEquals(60.5, result.value());
    }

    @Test
    void averageDuration_withEmptyList_returnsZero() {
        SleepAnalysisResult result =
                new FindAvgSessionDuration().apply(List.of());

        assertEquals(0.0, result.value());
    }
}
