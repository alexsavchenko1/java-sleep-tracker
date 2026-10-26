
package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SleepTrackerAppTest {

    @TempDir
    Path tempDir;

    // Проверяем запуск всех семи аналитических функций
    @Test
    void main_withValidFile_printsAllAnalysisResults()
            throws IOException {

        Path file = tempDir.resolve("sleep.log");

        Files.writeString(file, """
                01.10.25 22:15;02.10.25 08:00;GOOD
                02.10.25 23:00;03.10.25 08:00;NORMAL
                03.10.25 14:30;03.10.25 15:20;NORMAL
                03.10.25 23:30;04.10.25 06:20;BAD
                """);

        ByteArrayOutputStream output =
                new ByteArrayOutputStream();

        ByteArrayOutputStream errorOutput =
                new ByteArrayOutputStream();

        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;

        try (
                PrintStream testOut = new PrintStream(
                        output, true, StandardCharsets.UTF_8);
                PrintStream testErr = new PrintStream(
                        errorOutput, true, StandardCharsets.UTF_8)
        ) {
            System.setOut(testOut);
            System.setErr(testErr);

            SleepTrackerApp.main(new String[]{file.toString()});

        } finally {
            System.setOut(originalOut);
            System.setErr(originalErr);
        }

        String actual =
                output.toString(StandardCharsets.UTF_8);

        String errors =
                errorOutput.toString(StandardCharsets.UTF_8);

        // Диагностика: выводим оба потока после их восстановления
        System.out.println("STDOUT:");
        System.out.println(actual);

        System.out.println("STDERR:");
        System.out.println(errors);

        String expected = String.join(System.lineSeparator(),
                "Количество сессий сна: 4",
                "Количество сессий с плохим качеством сна: 1",
                "Длина самой короткой сессии сна (минуты): 50",
                "Длина самой длинной сессии сна (минуты): 585",
                "Средняя длина сессии сна (минуты): 396.25",
                "Количество бессонных ночей: 0",
                "Хронотип пользователя: Голубь"
        ) + System.lineSeparator();

        assertTrue(
                errors.isEmpty(),
                "Приложение вывело ошибку: " + errors
        );

        assertEquals(expected, actual);
    }

    // Проверяем запуск без аргументов командной строки
    @Test
    void main_withoutArguments_printsError()
            throws IOException {

        ByteArrayOutputStream errorOutput =
                new ByteArrayOutputStream();

        PrintStream originalErr = System.err;

        try (PrintStream testErr = new PrintStream(
                errorOutput, true, StandardCharsets.UTF_8)) {

            System.setErr(testErr);

            SleepTrackerApp.main(new String[]{});

        } finally {
            System.setErr(originalErr);
        }

        assertEquals(
                "Ошибка: укажите путь к лог-файлу."
                        + System.lineSeparator(),
                errorOutput.toString(StandardCharsets.UTF_8)
        );
    }

    // Проверяем обработку пустого лог-файла
    @Test
    void main_withEmptyFile_printsError()
            throws IOException {

        Path file = tempDir.resolve("empty.log");
        Files.createFile(file);

        ByteArrayOutputStream errorOutput =
                new ByteArrayOutputStream();

        PrintStream originalErr = System.err;

        try (PrintStream testErr = new PrintStream(
                errorOutput, true, StandardCharsets.UTF_8)) {

            System.setErr(testErr);

            SleepTrackerApp.main(new String[]{file.toString()});

        } finally {
            System.setErr(originalErr);
        }

        assertEquals(
                "Ошибка: лог-файл пуст."
                        + System.lineSeparator(),
                errorOutput.toString(StandardCharsets.UTF_8)
        );
    }
}
