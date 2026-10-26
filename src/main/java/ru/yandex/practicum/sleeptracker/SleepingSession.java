package ru.yandex.practicum.sleeptracker;
import java.time.LocalDateTime;

/*
Здесь решил применить новые знания, которы получил, пока
рылся в возможностях современной Java.

Попробовал работу не писать бойлерплейт, а трайнуть
поработать с record
* */


public record SleepingSession(
        LocalDateTime start,
        LocalDateTime end,
        SleepQuality quality
) {
}
