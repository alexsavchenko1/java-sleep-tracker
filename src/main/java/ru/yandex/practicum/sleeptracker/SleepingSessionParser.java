package ru.yandex.practicum.sleeptracker;
import java.time.format.DateTimeFormatter;
import java.util.List;
//import java.nio.file.Path;
//import java.nio.file.Files;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.util.stream.Stream;

// Класс для парсинга строк входного файла и преобразования
// Содержит два метода:
// 1. Преобразующий строку входного файла в SleepingSession
// 2. Читающий файл и возвращающий список SleepingSessions

public class SleepingSessionParser {

    public static SleepingSession parseLine(String line){
        // Нужен, потому что данные не в ISO формате у нас.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

        String[] array = line.split(";");
        return new SleepingSession(
                LocalDateTime.parse(array[0], formatter),
                LocalDateTime.parse(array[1], formatter),
                SleepQuality.valueOf(array[2].trim())
        );
    }

    public static List<SleepingSession> getSleepingSessions(Path path)
            throws IOException {
                try (Stream<String> lines = Files.lines(path)) {
                    return lines
                            .map(line -> parseLine(line))
                            .toList();
                }
    }
}
