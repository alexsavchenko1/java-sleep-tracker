package ru.yandex.practicum.sleeptracker;

import java.io.IOException;
import java.util.List;
//import java.util.Scanner;
import java.nio.file.Path;
import java.util.function.Function;
import ru.yandex.practicum.sleeptracker.analysis.*;


public class SleepTrackerApp {

    private static final List<Function<List<SleepingSession>, SleepAnalysisResult>> listOfFunctions = List.of(
            new CountSessions()
    );

    public static void main(String[] args) throws IOException {
        // Scanner scanner = new Scanner(System.in);
        // String input = scanner.nextLine(); // прочитали входящий путь к строке
        Path path = Path.of(args[0]);
        List<SleepingSession> sleepingSessions = SleepingSessionParser.getSleepingSessions(path);
        //sleepingSessions.forEach(System.out::println); // вывести каждый элемент списка без цикла
        }
    }