
package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.NoSuchFileException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import java.time.format.DateTimeParseException;


class SleepingSessionParserTest {

    @TempDir
    Path tempDir;

    // 1. Корректный разбор одной строки
    @Test
    void parseLine_validSession_returnsSleepingSession() {

        String line = "01.10.25 22:15;02.10.25 08:00;GOOD";

        SleepingSession result =
                SleepingSessionParser.parseLine(line);

        assertEquals(
                LocalDateTime.of(2025, 10, 1, 22, 15),
                result.start()
        );

        assertEquals(
                LocalDateTime.of(2025, 10, 2, 8, 0),
                result.end()
        );

        assertEquals(SleepQuality.GOOD, result.quality());
    }

    // 2. Пробелы после значения качества сна
    @Test
    void parseLine_qualityWithTrailingSpaces_parsesCorrectly() {

        String line = "01.10.25 22:15;02.10.25 08:00;BAD   ";

        SleepingSession result =
                SleepingSessionParser.parseLine(line);

        assertEquals(SleepQuality.BAD, result.quality());
    }

    // 3. Некорректная дата
    @Test
    void parseLine_invalidDate_throwsException() {

        String line = "invalid-date;02.10.25 08:00;GOOD";

        assertThrows(
                DateTimeParseException.class,
                () -> SleepingSessionParser.parseLine(line)
        );
    }

    // 4. Несуществующее значение enum
    @Test
    void parseLine_unknownQuality_throwsException() {

        String line = "01.10.25 22:15;02.10.25 08:00;EXCELLENT";

        assertThrows(
                IllegalArgumentException.class,
                () -> SleepingSessionParser.parseLine(line)
        );
    }

    // 5. Чтение файла с несколькими сессиями
    @Test
    void getSleepingSessions_validFile_returnsSessions()
            throws IOException {

        Path file = tempDir.resolve("sleep.log");

        Files.writeString(file, """
                01.10.25 22:15;02.10.25 08:00;GOOD
                02.10.25 23:00;03.10.25 08:00;NORMAL
                03.10.25 14:30;03.10.25 15:20;BAD
                """);

        List<SleepingSession> result =
                SleepingSessionParser.getSleepingSessions(file);

        assertEquals(3, result.size());

        assertEquals(SleepQuality.GOOD, result.get(0).quality());
        assertEquals(SleepQuality.NORMAL, result.get(1).quality());
        assertEquals(SleepQuality.BAD, result.get(2).quality());

        assertEquals(
                LocalDateTime.of(2025, 10, 3, 14, 30),
                result.get(2).start()
        );
    }

    // 6. Пустой файл
    @Test
    void getSleepingSessions_emptyFile_returnsEmptyList()
            throws IOException {

        Path file = tempDir.resolve("empty.log");

        Files.createFile(file);

        List<SleepingSession> result =
                SleepingSessionParser.getSleepingSessions(file);

        assertTrue(result.isEmpty());
    }

    // 7. Несуществующий файл
    @Test
    void getSleepingSessions_missingFile_throwsIOException() {

        Path file = tempDir.resolve("missing.log");

        assertThrows(
                NoSuchFileException.class,
                () -> SleepingSessionParser.getSleepingSessions(file)
        );
    }


    @Test
    void parseLine_invalidCalendarDate_throwsException() {
        String line = "31.02.25 22:00;01.03.25 07:00;GOOD";

        assertThrows(
                DateTimeParseException.class,
                () -> SleepingSessionParser.parseLine(line)
        );
    }

    @Test
    void parseLine_nonLeapYearFebruary29_throwsException() {
        String line = "29.02.25 22:00;01.03.25 07:00;GOOD";

        assertThrows(
                DateTimeParseException.class,
                () -> SleepingSessionParser.parseLine(line)
        );
    }

    @Test
    void parseLine_validLeapYear_acceptsFebruary29() {
        String line = "29.02.24 22:00;01.03.24 07:00;GOOD";

        SleepingSession result =
                SleepingSessionParser.parseLine(line);

        assertEquals(
                LocalDateTime.of(2024, 2, 29, 22, 0),
                result.start()
        );
    }

    @Test
    void parseLine_endBeforeStart_throwsException() {
        String line = "02.10.25 08:00;01.10.25 22:00;GOOD";

        assertThrows(
                IllegalArgumentException.class,
                () -> SleepingSessionParser.parseLine(line)
        );
    }

    @Test
    void parseLine_equalStartAndEnd_throwsException() {
        String line = "02.10.25 08:00;02.10.25 08:00;GOOD";

        assertThrows(
                IllegalArgumentException.class,
                () -> SleepingSessionParser.parseLine(line)
        );
    }

    @Test
    void parseLine_extraField_throwsException() {
        String line =
                "01.10.25 22:00;02.10.25 06:00;GOOD;EXTRA";

        assertThrows(
                IllegalArgumentException.class,
                () -> SleepingSessionParser.parseLine(line)
        );
    }

    @Test
    void parseLine_missingField_throwsException() {
        String line = "01.10.25 22:00;02.10.25 06:00";

        assertThrows(
                IllegalArgumentException.class,
                () -> SleepingSessionParser.parseLine(line)
        );
    }

    @Test
    void parseLine_blankLine_throwsException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SleepingSessionParser.parseLine("   ")
        );
    }

}
