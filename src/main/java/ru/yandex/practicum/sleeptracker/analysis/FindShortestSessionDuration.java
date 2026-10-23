package ru.yandex.practicum.sleeptracker.analysis;

import ru.yandex.practicum.sleeptracker.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.SleepingSession;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;
import static java.util.Collections.min;

public class FindShortestSessionDuration implements Function<List<SleepingSession>, SleepAnalysisResult> {

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sleepingSessionsList) {
        List<Long> listOfDurations = sleepingSessionsList.stream()
                    .map(session -> Duration.between(session.start(), session.end()).toMinutes())
                    .toList();

        return new SleepAnalysisResult(
                "Длина самой короткой сессии сна (минуты)",
                min(listOfDurations)
        );
    }
}
