package ru.yandex.practicum.sleeptracker.analysis;

import ru.yandex.practicum.sleeptracker.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.SleepingSession;

import java.util.List;
import java.util.function.Function;

import static java.util.Collections.max;
import static java.time.Duration.between;


public class FindLongestSessionDuration implements Function<List<SleepingSession>, SleepAnalysisResult> {

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sleepingSessionsList) {
        List<Long> listOfDurations = sleepingSessionsList.stream()
                .map(session -> between(session.start(), session.end()).toMinutes())
                .toList();

        return new SleepAnalysisResult(
                "Длина самой длинной сессии сна (минуты)",
                max(listOfDurations)
        );
    }
}
