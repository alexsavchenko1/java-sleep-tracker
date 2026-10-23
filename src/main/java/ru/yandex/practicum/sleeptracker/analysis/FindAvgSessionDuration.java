package ru.yandex.practicum.sleeptracker.analysis;

import ru.yandex.practicum.sleeptracker.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.SleepingSession;

import java.util.List;
import java.util.function.Function;

//import static java.util.Collections;
import static java.time.Duration.between;


public class FindAvgSessionDuration implements Function<List<SleepingSession>, SleepAnalysisResult> {

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sleepingSessionsList) {
        List<Long> listOfDurations = sleepingSessionsList.stream()
                .map(session -> between(session.start(), session.end()).toMinutes())
                .toList();

        double avgDuration = listOfDurations.stream()
                .mapToLong(duration -> duration.longValue())
                .average()
                .orElse(0);

        return new SleepAnalysisResult (
                "Средняя длина сессии сна (минуты)",
                avgDuration
        );
    }
}
