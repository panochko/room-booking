package ru.itmo.booking.dto;

import ru.itmo.booking.domain.Room;

/**
 * Представление комнаты в ответе API.
 *
 * @param id       идентификатор
 * @param name     название
 * @param capacity вместимость
 * @param location расположение
 * @param active   доступна ли для брони
 */
public record RoomResponse(Long id, String name, int capacity, String location, boolean active) {

    /**
     * Строит ответ из сущности.
     *
     * @param r сущность комнаты
     * @return DTO для клиента
     */
    public static RoomResponse from(Room r) {
        return new RoomResponse(r.getId(), r.getName(), r.getCapacity(), r.getLocation(), r.isActive());
    }
}
