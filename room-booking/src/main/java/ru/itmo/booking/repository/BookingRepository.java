package ru.itmo.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.itmo.booking.domain.Booking;
import ru.itmo.booking.domain.BookingStatus;

import java.time.Instant;
import java.util.List;

/**
 * Репозиторий бронирований.
 */
public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {

    /**
     * Поиск по спецификации; комната подгружается тем же запросом, чтобы не было N+1.
     *
     * @param spec     условия фильтрации
     * @param pageable параметры пагинации
     * @return страница броней
     */
    @Override
    @EntityGraph(attributePaths = "room")
    Page<Booking> findAll(Specification<Booking> spec, Pageable pageable);

    /**
     * Проверяет, пересекается ли период с бронями комнаты в заданном статусе.
     *
     * @param roomId      идентификатор комнаты
     * @param status      статус броней
     * @param periodStart начало периода
     * @param periodEnd   конец периода
     * @return {@code true}, если пересечение есть
     */
    @Query("""
            select count(b) > 0 from Booking b
            where b.room.id = :roomId and b.status = :status
              and b.startTime < :periodEnd and b.endTime > :periodStart""")
    boolean existsOverlapping(@Param("roomId") Long roomId, @Param("status") BookingStatus status,
                              @Param("periodStart") Instant periodStart, @Param("periodEnd") Instant periodEnd);

    /**
     * Возвращает брони комнаты, пересекающиеся с периодом, по возрастанию начала.
     *
     * @param roomId      идентификатор комнаты
     * @param status      статус броней
     * @param periodStart начало периода
     * @param periodEnd   конец периода
     * @return брони периода
     */
    @Query("""
            select b from Booking b
            where b.room.id = :roomId and b.status = :status
              and b.startTime < :periodEnd and b.endTime > :periodStart
            order by b.startTime""")
    List<Booking> findOverlapping(@Param("roomId") Long roomId, @Param("status") BookingStatus status,
                                  @Param("periodStart") Instant periodStart, @Param("periodEnd") Instant periodEnd);

    /**
     * Массово переводит завершившиеся брони в новый статус.
     *
     * @param oldStatus исходный статус
     * @param newStatus новый статус
     * @param now       момент, раньше которого должна закончиться бронь
     * @return число обновлённых броней
     */
    @Modifying
    @Query("update Booking b set b.status = :newStatus where b.status = :oldStatus and b.endTime < :now")
    int completeFinished(@Param("oldStatus") BookingStatus oldStatus, @Param("newStatus") BookingStatus newStatus,
                         @Param("now") Instant now);
}
