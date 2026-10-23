package ru.yandex.practicum.sleeptracker.analysis;

import ru.yandex.practicum.sleeptracker.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.SleepingSession;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;


public class CountSessions implements Function<List<SleepingSession>, SleepAnalysisResult> {

    // возвращаем SleepAnalysisResult для стандартизированной обработки потока в main
    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sleepingSessionsList) {
        return new SleepAnalysisResult(
                "Количество сессий сна",
                sleepingSessionsList.size()
        );
    }
}
