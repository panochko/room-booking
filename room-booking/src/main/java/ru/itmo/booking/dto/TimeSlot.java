package ru.itmo.booking.dto;

import java.time.Instant;

/**
 * Свободное окно комнаты.
 *
 * @param from начало окна
 * @param to   конец окна
 */
public record TimeSlot(Instant from, Instant to) {
}
