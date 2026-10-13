package ru.itmo.booking.domain;

/**
 * Статус бронирования.
 */
public enum BookingStatus {

    /** Бронь действует и занимает комнату. */
    CONFIRMED,

    /** Бронь отменена, комната свободна. */
    CANCELLED,

    /** Время брони прошло. */
    COMPLETED
}
