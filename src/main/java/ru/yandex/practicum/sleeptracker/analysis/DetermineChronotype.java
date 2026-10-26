
package ru.yandex.practicum.sleeptracker.analysis;

import ru.yandex.practicum.sleeptracker.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.SleepingSession;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DetermineChronotype
        implements Function<List<SleepingSession>, SleepAnalysisResult> {

    private enum Chronotype {
        OWL, LARK, PIGEON
    }

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {

        // Входной список должен быть непустым.
        // Это проверяет вызывающий код (SleepTrackerApp).

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

        if (last.end().toLocalTime().equals(LocalTime.MIDNIGHT)) {
            endDate = endDate.minusDays(1);
        }

        if (startDate.isAfter(endDate)) {
            return new SleepAnalysisResult(
                    "Хронотип пользователя",
                    "Не определён"
            );
        }

        // Получаем хронотип каждой ночи и подсчитываем частоту
        Map<Chronotype, Long> counts = startDate
                .datesUntil(endDate.plusDays(1))
                .flatMap(date -> {
                    List<SleepingSession> nightSessions =
                            getNightSessions(date, sessions);

                    if (nightSessions.isEmpty()) {
                        return Stream.<Chronotype>empty();
                    }

                    return Stream.of(
                            classifyNight(date, nightSessions)
                    );
                })
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        Collectors.counting()
                ));

        // Бессонные ночи и дневные сессии не учитываются
        if (counts.isEmpty()) {
            return new SleepAnalysisResult(
                    "Хронотип пользователя",
                    "Не определён"
            );
        }

        long owls = counts.getOrDefault(Chronotype.OWL, 0L);
        long larks = counts.getOrDefault(Chronotype.LARK, 0L);
        long pigeons = counts.getOrDefault(Chronotype.PIGEON, 0L);

        // Определяем преобладающий хронотип
        String result = "Голубь";

        if (owls > larks && owls > pigeons) {
            result = "Сова";
        } else if (larks > owls && larks > pigeons) {
            result = "Жаворонок";
        }

        return new SleepAnalysisResult(
                "Хронотип пользователя",
                result
        );
    }

    // Получаем сессии, пересекающие интервал 00:00–06:00
    private List<SleepingSession> getNightSessions(
            LocalDate date,
            List<SleepingSession> sessions
    ) {
        LocalDateTime nightStart = date.atStartOfDay();
        LocalDateTime nightEnd = date.atTime(6, 0);

        return sessions.stream()
                .filter(session ->
                        session.start().isBefore(nightEnd)
                                && session.end().isAfter(nightStart)
                )
                .toList();
    }

    // Определяем хронотип конкретной ночи
    private Chronotype classifyNight(
            LocalDate date,
            List<SleepingSession> nightSessions
    ) {
        LocalDateTime bedTime = nightSessions.stream()
                .min(Comparator.comparing(SleepingSession::start))
                .orElseThrow()
                .start();

        LocalDateTime wakeTime = nightSessions.stream()
                .max(Comparator.comparing(SleepingSession::end))
                .orElseThrow()
                .end();

        // Пороговые значения привязаны к конкретной ночи
        LocalDateTime owlBedThreshold =
                date.minusDays(1).atTime(23, 0);

        LocalDateTime owlWakeThreshold =
                date.atTime(9, 0);

        LocalDateTime larkBedThreshold =
                date.minusDays(1).atTime(22, 0);

        LocalDateTime larkWakeThreshold =
                date.atTime(7, 0);

        if (bedTime.isAfter(owlBedThreshold)
                && wakeTime.isAfter(owlWakeThreshold)) {
            return Chronotype.OWL;
        }

        if (bedTime.isBefore(larkBedThreshold)
                && wakeTime.isBefore(larkWakeThreshold)) {
            return Chronotype.LARK;
        }

        return Chronotype.PIGEON;
    }
}
