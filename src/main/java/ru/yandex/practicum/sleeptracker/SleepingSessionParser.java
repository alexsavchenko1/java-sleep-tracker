
package ru.yandex.practicum.sleeptracker;

import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.List;
//import java.nio.file.Path;
//import java.nio.file.Files;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.io.IOException;
import java.util.stream.Stream;

// Класс для парсинга строк входного файла и преобразования
// Содержит два метода:
// 1. Преобразующий строку входного файла в SleepingSession
// 2. Читающий файл и возвращающий список SleepingSessions

public class SleepingSessionParser {

    // Нужен, потому что данные не в ISO формате у нас.
    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd.MM.uu HH:mm")
                    .withResolverStyle(ResolverStyle.STRICT);

    public static SleepingSession parseLine(String line) {

        if (line == null || line.isBlank()) {
            throw new IllegalArgumentException(
                    "Строка сессии сна пуста"
            );
        }

        String[] array = line.split(";", -1);

        if (array.length != 3) {
            throw new IllegalArgumentException(
                    "Некорректный формат строки: ожидалось 3 поля"
            );
        }

        LocalDateTime start =
                LocalDateTime.parse(array[0].trim(), formatter);

        LocalDateTime end =
                LocalDateTime.parse(array[1].trim(), formatter);

        SleepQuality quality =
                SleepQuality.valueOf(array[2].trim());

        if (!end.isAfter(start)) {
            throw new IllegalArgumentException(
                    "Время окончания сна должно быть позже времени начала"
            );
        }

        return new SleepingSession(start, end, quality);
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
