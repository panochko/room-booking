package ru.itmo.booking.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.itmo.booking.domain.Booking;
import ru.itmo.booking.domain.BookingStatus;
import ru.itmo.booking.domain.Room;

import java.time.Instant;

/**
 * Тело запроса на бронирование комнаты.
 *
 * @param roomId     идентификатор комнаты
 * @param ownerEmail email автора брони
 * @param startTime  начало брони, в будущем
 * @param endTime    конец брони, позже начала
 */
public record BookingRequest(
        @NotNull Long roomId,
        @NotBlank @Email String ownerEmail,
        @NotNull @Future Instant startTime,
        @NotNull Instant endTime) {

    /**
     * Проверяет, что конец позже начала.
     *
     * @return {@code true}, если период корректен или время не задано
     */
    @AssertTrue(message = "Время окончания должно быть позже начала")
    public boolean isPeriodValid() {
        return startTime == null || endTime == null || endTime.isAfter(startTime);
    }

    /**
     * Создаёт подтверждённую бронь комнаты.
     *
     * @param room комната
     * @return новая сущность
     */
    public Booking toEntity(Room room) {
        return Booking.builder().room(room).ownerEmail(ownerEmail).startTime(startTime)
                .endTime(endTime).status(BookingStatus.CONFIRMED).build();
    }
}
