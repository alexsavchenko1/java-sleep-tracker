
package ru.yandex.practicum.sleeptracker.analysis;

import org.junit.jupiter.api.Test;

import ru.yandex.practicum.sleeptracker.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.SleepQuality;
import ru.yandex.practicum.sleeptracker.SleepingSession;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DetermineChronotypeTest {

    private SleepingSession session(String start, String end) {
        return new SleepingSession(
                LocalDateTime.parse(start),
                LocalDateTime.parse(end),
                SleepQuality.NORMAL
        );
    }

    private String determine(List<SleepingSession> sessions) {
        SleepAnalysisResult result =
                new DetermineChronotype().apply(sessions);

        return (String) result.value();
    }

    // ===== Базовые хронотипы =====

    @Test
    void lateSleepAndLateWake_returnsOwl() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T23:40", "2025-10-02T09:30")
        );

        assertEquals("Сова", determine(sessions));
    }

    @Test
    void earlySleepAndEarlyWake_returnsLark() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T21:30", "2025-10-02T06:30")
        );

        assertEquals("Жаворонок", determine(sessions));
    }

    @Test
    void intermediateSleepSchedule_returnsPigeon() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T22:30", "2025-10-02T08:00")
        );

        assertEquals("Голубь", determine(sessions));
    }

    // ===== Засыпание после полуночи =====

    @Test
    void sleepAfterMidnightAndWakeAfterNine_returnsOwl() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-02T01:30", "2025-10-02T10:00")
        );

        assertEquals("Сова", determine(sessions));
    }

    @Test
    void sleepAfterMidnightAndWakeBeforeSeven_notLark() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-02T02:00", "2025-10-02T06:30")
        );

        assertEquals("Голубь", determine(sessions));
    }

    // ===== Агрегация хронотипов =====

    @Test
    void equalOwlAndLarkCounts_returnsPigeon() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T23:40", "2025-10-02T09:30"),
                session("2025-10-02T21:30", "2025-10-03T06:30")
        );

        assertEquals("Голубь", determine(sessions));
    }

    @Test
    void owlMajority_returnsOwl() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T23:40", "2025-10-02T09:30"),
                session("2025-10-02T21:30", "2025-10-03T06:30"),
                session("2025-10-03T23:45", "2025-10-04T10:00")
        );

        assertEquals("Сова", determine(sessions));
    }

    // ===== Дневной сон и отсутствие ночного сна =====

    @Test
    void daytimeNap_doesNotAffectChronotype() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T23:40", "2025-10-02T09:30"),
                session("2025-10-02T14:00", "2025-10-02T15:00")
        );

        assertEquals("Сова", determine(sessions));
    }

    @Test
    void onlyDaytimeSleep_returnsUndefined() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T14:00", "2025-10-01T15:00")
        );

        assertEquals("Не определён", determine(sessions));
    }

    @Test
    void noSleepBetweenMidnightAndSix_returnsUndefined() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T23:00", "2025-10-01T23:59"),
                session("2025-10-02T07:00", "2025-10-02T08:00")
        );

        assertEquals("Не определён", determine(sessions));
    }

    // ===== Несколько сессий за одну ночь =====

    @Test
    void twoSessionsWithinOneNight_classifiedOnce() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T23:40", "2025-10-02T02:00"),
                session("2025-10-02T04:00", "2025-10-02T10:00")
        );

        assertEquals("Сова", determine(sessions));
    }

    // ===== Строгие временные границы =====

    @Test
    void sleepExactlyAtElevenPm_notOwl() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T23:00", "2025-10-02T09:30")
        );

        assertEquals("Голубь", determine(sessions));
    }

    @Test
    void wakeExactlyAtNineAm_notOwl() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T23:40", "2025-10-02T09:00")
        );

        assertEquals("Голубь", determine(sessions));
    }

    @Test
    void sleepExactlyAtTenPm_notLark() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T22:00", "2025-10-02T06:30")
        );

        assertEquals("Голубь", determine(sessions));
    }

    @Test
    void wakeExactlyAtSevenAm_notLark() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T21:30", "2025-10-02T07:00")
        );

        assertEquals("Голубь", determine(sessions));
    }
}
