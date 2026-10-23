
package ru.yandex.practicum.sleeptracker.analysis;

import ru.yandex.practicum.sleeptracker.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.SleepingSession;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public class CountSleeplessNights
        implements Function<List<SleepingSession>, SleepAnalysisResult> {

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {

        // Находим границы периода логирования
        SleepingSession first = sessions.stream()
                .min(Comparator.comparing(SleepingSession::start))
                .orElseThrow();

        SleepingSession last = sessions.stream()
                .max(Comparator.comparing(SleepingSession::end))
                .orElseThrow();

        // Определяем первую потенциальную ночь
        LocalDate startDate = first.start().toLocalDate();

        if (first.start().getHour() >= 12) {
            startDate = startDate.plusDays(1);
        }

        // Определяем последнюю потенциальную ночь
        LocalDate endDate = last.end().toLocalDate();

        // Если наблюдение закончилось ровно в полночь,
        // новая ночь ещё не началась
        if (last.end().toLocalTime().equals(java.time.LocalTime.MIDNIGHT)) {
            endDate = endDate.minusDays(1);
        }

        //  Если потенциальных ночей нет
        if (startDate.isAfter(endDate)) {
            return new SleepAnalysisResult(
                    "Количество бессонных ночей",
                    0L
            );
        }

        //  Проверяем все потенциальные ночи
        long sleeplessNightsCount = startDate
                .datesUntil(endDate.plusDays(1))
                .filter(date -> isSleeplessNight(date, sessions))
                .count();

        return new SleepAnalysisResult(
                "Количество бессонных ночей",
                sleeplessNightsCount
        );
    }

    // Проверяем, что ни одна сессия не пересекает ночь 00:00–06:00
    private boolean isSleeplessNight(
            LocalDate date,
            List<SleepingSession> sessions
    ) {
        LocalDateTime nightStart = date.atStartOfDay();
        LocalDateTime nightEnd = date.atTime(6, 0);

        return sessions.stream()
                .noneMatch(session ->
                        session.start().isBefore(nightEnd)
                                && session.end().isAfter(nightStart)
                );
    }
}
