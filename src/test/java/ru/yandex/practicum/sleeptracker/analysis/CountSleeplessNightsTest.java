
package ru.yandex.practicum.sleeptracker.analysis;

import org.junit.jupiter.api.Test;

import ru.yandex.practicum.sleeptracker.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.SleepQuality;
import ru.yandex.practicum.sleeptracker.SleepingSession;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CountSleeplessNightsTest {

    private SleepingSession session(String start, String end) {
        return new SleepingSession(
                LocalDateTime.parse(start),
                LocalDateTime.parse(end),
                SleepQuality.NORMAL
        );
    }

    private long countSleepless(List<SleepingSession> sessions) {
        SleepAnalysisResult result =
                new CountSleeplessNights().apply(sessions);

        return (Long) result.value();
    }

    // Обычный ночной сон
    @Test
    void overnightSleep_noSleeplessNights() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T23:00", "2025-10-02T08:00")
        );

        assertEquals(0L, countSleepless(sessions));
    }

    // Короткий сон внутри интервала 00:00–06:00
    @Test
    void shortNightSleep_notSleepless() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-02T02:00", "2025-10-02T05:00")
        );

        assertEquals(0L, countSleepless(sessions));
    }

    // Между двумя ночными снами пропущена ночь
    @Test
    void missingNight_countsOneSleeplessNight() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T23:00", "2025-10-02T08:00"),
                session("2025-10-03T23:00", "2025-10-04T08:00")
        );

        // 02.10 — сон был
        // 03.10 — сна не было
        // 04.10 — сон был
        assertEquals(1L, countSleepless(sessions));
    }

    // Сон заканчивается ровно в 00:00
    @Test
    void sleepEndsAtMidnight_doesNotCoverNight() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T22:00", "2025-10-02T00:00"),
                session("2025-10-02T07:00", "2025-10-02T11:00")
        );

        // Ночью 02.10 с 00:00 до 06:00 сна не было
        assertEquals(1L, countSleepless(sessions));
    }

    // Сон начинается ровно в 06:00
    @Test
    void sleepStartsAtSix_doesNotCoverNight() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-02T06:00", "2025-10-02T08:00")
        );

        assertEquals(1L, countSleepless(sessions));
    }

    // Две сессии сна в течение одной ночи
    @Test
    void multipleSessionsSameNight_notSleepless() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T23:00", "2025-10-02T01:00"),
                session("2025-10-02T03:00", "2025-10-02T05:00")
        );

        assertEquals(0L, countSleepless(sessions));
    }

    //  Переход между месяцами
    @Test
    void monthBoundary_countsMissingNight() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-30T23:00", "2025-10-31T08:00"),
                session("2025-11-01T23:00", "2025-11-02T08:00")
        );

        // 31.10 — сон был
        // 01.11 — сна не было
        // 02.11 — сон был
        assertEquals(1L, countSleepless(sessions));
    }

    // Дневной сон в конце логирования
    @Test
    void lastSessionInAfternoon_doesNotAddFutureNight() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T23:00", "2025-10-02T08:00"),
                session("2025-10-02T14:00", "2025-10-02T15:00")
        );

        // Ночь 03.10 не входит в период наблюдения
        assertEquals(0L, countSleepless(sessions));
    }

    // Первая сессия — дневной сон
    @Test
    void firstSessionInAfternoon_startsFromNextNight() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T14:00", "2025-10-01T15:00"),
                session("2025-10-02T23:00", "2025-10-03T08:00")
        );

        // 02.10 — сна в 00:00–06:00 не было
        // 03.10 — сон был
        assertEquals(1L, countSleepless(sessions));
    }

    @Test
    void unorderedSessions_countsCorrectly() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-03T23:00", "2025-10-04T08:00"),
                session("2025-10-01T23:00", "2025-10-02T08:00")
        );

        // Должна быть обнаружена бессонная ночь 03.10
        assertEquals(1L, countSleepless(sessions));
    }

    // Одна сессия пересекает несколько ночей
    @Test
    void sleepSpanningMultipleNights_countsZeroSleepless() {
        List<SleepingSession> sessions = List.of(
                session(
                        "2025-10-01T23:00",
                        "2025-10-04T07:00"
                )
        );

        assertEquals(0L, countSleepless(sessions));
    }

    // Несколько бессонных ночей подряд
    @Test
    void threeConsecutiveSleeplessNights_countsThree() {
        List<SleepingSession> sessions = List.of(
                session(
                        "2025-10-01T23:00",
                        "2025-10-02T08:00"
                ),
                session(
                        "2025-10-05T23:00",
                        "2025-10-06T08:00"
                )
        );

        assertEquals(3L, countSleepless(sessions));
    }
}
