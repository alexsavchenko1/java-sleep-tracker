package ru.yandex.practicum.sleeptracker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.util.List;
//import java.util.Scanner;
import java.nio.file.Path;
import java.util.function.Function;
import ru.yandex.practicum.sleeptracker.analysis.*;
import java.time.format.DateTimeParseException;


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

            // Дальше оставляешь существующий код:
            // чтение сессий, проверка списка и запуск семи функций

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