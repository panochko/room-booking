package ru.itmo.booking.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import ru.itmo.booking.domain.Room;

import java.util.Optional;

/**
 * Репозиторий комнат.
 */
public interface RoomRepository extends JpaRepository<Room, Long> {

    /**
     * Возвращает комнаты вместимостью не меньше заданной.
     *
     * @param capacity минимальная вместимость
     * @param pageable параметры пагинации
     * @return страница комнат
     */
    Page<Room> findByCapacityGreaterThanEqual(int capacity, Pageable pageable);

    /**
     * Находит комнату и блокирует её строку до конца транзакции ({@code SELECT ... FOR UPDATE}).
     * Параллельные брони одной комнаты выстраиваются в очередь.
     *
     * @param id идентификатор комнаты
     * @return комната, если найдена
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Room> findWithLockById(Long id);
}
