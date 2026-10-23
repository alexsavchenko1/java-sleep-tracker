package ru.yandex.practicum.sleeptracker;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
//import java.util.Scanner;
import java.nio.file.Path;
import java.util.function.Function;
import ru.yandex.practicum.sleeptracker.analysis.*;


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

    public static void main(String[] args) throws IOException {
        // Scanner scanner = new Scanner(System.in);
        // String input = scanner.nextLine(); // прочитали входящий путь к строке

        // Проверим, указан ли какой-нить файл
        if (args.length == 0) {
            System.err.println("Ошибка: укажите путь к лог-файлу.");
            return;
        }

        Path path = Path.of(args[0]);

        // Проверим, что путь указывает на обычный файл
        if (!Files.isRegularFile(path)) {
            System.err.println("Ошибка: файл не найден или путь не является файлом: " + path);
            return;
        }

        //sleepingSessions.forEach(System.out::println); // вывести каждый элемент списка без цикла

        try {
            // Проверим, что файл не пуст
            if (Files.size(path) == 0) {
                System.err.println("Ошибка: лог-файл пуст.");
                return;
            }

            List<SleepingSession> sleepingSessions = SleepingSessionParser.getSleepingSessions(path);

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
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            System.err.println(
                    "Ошибка формата данных в лог-файле: " + e.getMessage()
            );
        }

        }
    }