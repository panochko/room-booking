package ru.itmo.booking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import ru.itmo.booking.domain.Room;

/**
 * Тело запроса на создание или полное обновление комнаты.
 *
 * @param name     уникальное название
 * @param capacity вместимость, человек
 * @param location расположение
 * @param active   доступность для брони; если не указана, комната активна
 */
public record RoomRequest(
        @NotBlank String name,
        @Positive int capacity,
        String location,
        Boolean active) {

    /**
     * Переносит значения запроса в сущность.
     *
     * @param room целевая сущность
     * @return та же сущность
     */
    public Room applyTo(Room room) {
        room.setName(name);
        room.setCapacity(capacity);
        room.setLocation(location);
        room.setActive(active == null || active);
        return room;
    }
}
