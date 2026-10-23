
package ru.yandex.practicum.sleeptracker;

import ru.yandex.practicum.sleeptracker.analysis.CountBadSessions;
import ru.yandex.practicum.sleeptracker.analysis.CountSessions;
import ru.yandex.practicum.sleeptracker.analysis.CountSleeplessNights;
import ru.yandex.practicum.sleeptracker.analysis.DetermineChronotype;
import ru.yandex.practicum.sleeptracker.analysis.FindAvgSessionDuration;
import ru.yandex.practicum.sleeptracker.analysis.FindLongestSessionDuration;
import ru.yandex.practicum.sleeptracker.analysis.FindShortestSessionDuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.function.Function;

public class SleepTrackerApp {

    private static final List<
            Function<List<SleepingSession>, SleepAnalysisResult>
            > listOfFunctions = List.of(
            new CountSessions(),
            new CountBadSessions(),
            new FindShortestSessionDuration(),
            new FindLongestSessionDuration(),
            new FindAvgSessionDuration(),
            new CountSleeplessNights(),
            new DetermineChronotype()
    );

    public static void main(String[] args) {

        // Проверим, указан ли какой-нить файл
        if (args.length == 0) {
            System.err.println("Ошибка: укажите путь к лог-файлу.");
            return;
        }

        try {
            Path path = Path.of(args[0]);

            // Проверим, что путь указывает на обычный файл
            if (!Files.isRegularFile(path)) {
                System.err.println(
                        "Ошибка: файл не найден или путь не является файлом: " + path
                );
                return;
            }

            // Проверим, что файл не пуст
            if (Files.size(path) == 0) {
                System.err.println("Ошибка: лог-файл пуст.");
                return;
            }

            // Читаем сессии сна из файла
            List<SleepingSession> sleepingSessions =
                    SleepingSessionParser.getSleepingSessions(path);

            // Проверим результат парсинга
            if (sleepingSessions.isEmpty()) {
                System.err.println("Ошибка: в файле нет сессий сна.");
                return;
            }

            // Запустим все аналитические функции
            listOfFunctions.forEach(analysis -> {
                SleepAnalysisResult result =
                        analysis.apply(sleepingSessions);

                System.out.println(
                        result.description() + ": " + result.value()
                );
            });

        } catch (IOException e) {
            System.err.println(
                    "Ошибка чтения файла: " + e.getMessage()
            );
        } catch (InvalidPathException e) {
            System.err.println(
                    "Некорректный путь к файлу: " + e.getMessage()
            );
        } catch (DateTimeParseException e) {
            System.err.println(
                    "Некорректная дата: " + e.getMessage()
            );
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            System.err.println(
                    "Ошибка формата данных в лог-файле: " + e.getMessage()
            );
        }
    }
}
