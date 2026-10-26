package ru.yandex.practicum.sleeptracker.analysis;

import ru.yandex.practicum.sleeptracker.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.SleepQuality;
import ru.yandex.practicum.sleeptracker.SleepingSession;

import java.util.List;
import java.util.function.Function;

public class CountBadSessions implements Function<List<SleepingSession>, SleepAnalysisResult> {

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sleepingSessionsList) {

        long badSessions = sleepingSessionsList.stream()
                .filter(session -> SleepQuality.BAD.equals(session.quality()))
                .count();

        return new SleepAnalysisResult(
                "Количество сессий с плохим качеством сна",
                badSessions
        );
    }

}
