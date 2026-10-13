package ru.itmo.booking.dto;

import ru.itmo.booking.domain.Booking;
import ru.itmo.booking.domain.BookingStatus;

import java.time.Instant;

/**
 * Представление брони в ответе API.
 *
 * @param id         идентификатор
 * @param roomId     идентификатор комнаты
 * @param roomName   название комнаты
 * @param ownerEmail email автора
 * @param startTime  начало
 * @param endTime    конец
 * @param status     статус
 */
public record BookingResponse(
        Long id,
        Long roomId,
        String roomName,
        String ownerEmail,
        Instant startTime,
        Instant endTime,
        BookingStatus status) {

    /**
     * Строит ответ из сущности.
     *
     * @param b сущность брони
     * @return DTO для клиента
     */
    public static BookingResponse from(Booking b) {
        return new BookingResponse(b.getId(), b.getRoom().getId(), b.getRoom().getName(),
                b.getOwnerEmail(), b.getStartTime(), b.getEndTime(), b.getStatus());
    }
}
