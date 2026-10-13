package ru.itmo.booking.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.itmo.booking.domain.Booking;
import ru.itmo.booking.domain.Room;
import ru.itmo.booking.dto.BookingFilter;
import ru.itmo.booking.dto.BookingRequest;
import ru.itmo.booking.dto.BookingResponse;
import ru.itmo.booking.dto.TimeSlot;
import ru.itmo.booking.repository.BookingRepository;
import ru.itmo.booking.repository.RoomRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.CONFLICT;
import static ru.itmo.booking.domain.BookingStatus.CANCELLED;
import static ru.itmo.booking.domain.BookingStatus.COMPLETED;
import static ru.itmo.booking.domain.BookingStatus.CONFIRMED;

/**
 * Бизнес-логика бронирования комнат.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService {

    private final BookingRepository bookings;
    private final RoomRepository rooms;

    /**
     * Возвращает страницу броней, подходящих под фильтр.
     *
     * @param filter   параметры фильтрации
     * @param pageable параметры пагинации и сортировки
     * @return страница броней
     */
    public Page<BookingResponse> search(BookingFilter filter, Pageable pageable) {
        return bookings.findAll(filter.toSpecification(), pageable).map(BookingResponse::from);
    }

    /**
     * Возвращает бронь по идентификатору.
     *
     * @param id идентификатор
     * @return бронь
     * @throws EntityNotFoundException если бронь не найдена
     */
    public BookingResponse get(Long id) {
        return BookingResponse.from(find(id));
    }

    /**
     * Создаёт бронь. Строка комнаты блокируется до конца транзакции, поэтому проверка пересечений
     * и вставка выполняются атомарно, а двойное бронирование невозможно.
     *
     * @param request данные брони
     * @return созданная бронь
     * @throws EntityNotFoundException если комната не найдена
     * @throws ResponseStatusException {@code 409}, если комната неактивна или занята на это время
     */
    @Transactional
    public BookingResponse create(BookingRequest request) {
        Room room = rooms.findWithLockById(request.roomId()).orElseThrow(() -> roomNotFound(request.roomId()));
        if (!room.isActive()) {
            throw new ResponseStatusException(CONFLICT, "Комната недоступна для бронирования");
        }
        if (bookings.existsOverlapping(room.getId(), CONFIRMED, request.startTime(), request.endTime())) {
            throw new ResponseStatusException(CONFLICT, "Комната занята на выбранное время");
        }
        return BookingResponse.from(bookings.save(request.toEntity(room)));
    }

    /**
     * Отменяет бронь, которая ещё не началась.
     *
     * @param id идентификатор
     * @return отменённая бронь
     * @throws EntityNotFoundException если бронь не найдена
     * @throws ResponseStatusException {@code 409}, если бронь уже начата, отменена или завершена
     */
    @Transactional
    public BookingResponse cancel(Long id) {
        Booking booking = find(id);
        if (booking.getStatus() != CONFIRMED || !booking.getStartTime().isAfter(Instant.now())) {
            throw new ResponseStatusException(CONFLICT, "Отменить можно только бронь, которая ещё не началась");
        }
        booking.setStatus(CANCELLED);
        return BookingResponse.from(booking);
    }

    /**
     * Возвращает свободные окна комнаты на сутки по UTC.
     *
     * @param roomId идентификатор комнаты
     * @param date   дата
     * @return свободные окна по возрастанию времени
     * @throws EntityNotFoundException если комната не найдена
     */
    public List<TimeSlot> availability(Long roomId, LocalDate date) {
        if (!rooms.existsById(roomId)) {
            throw roomNotFound(roomId);
        }
        Instant dayStart = date.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant dayEnd = dayStart.plus(1, ChronoUnit.DAYS);
        List<TimeSlot> free = new ArrayList<>();
        Instant cursor = dayStart;
        for (Booking b : bookings.findOverlapping(roomId, CONFIRMED, dayStart, dayEnd)) {
            if (b.getStartTime().isAfter(cursor)) {
                free.add(new TimeSlot(cursor, b.getStartTime()));
            }
            if (b.getEndTime().isAfter(cursor)) {
                cursor = b.getEndTime();
            }
        }
        if (cursor.isBefore(dayEnd)) {
            free.add(new TimeSlot(cursor, dayEnd));
        }
        return free;
    }

    /**
     * Раз в минуту переводит прошедшие брони в статус {@code COMPLETED}.
     */
    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void completeFinished() {
        int count = bookings.completeFinished(CONFIRMED, COMPLETED, Instant.now());
        if (count > 0) {
            log.info("Завершено броней: {}", count);
        }
    }

    private Booking find(Long id) {
        return bookings.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Бронь с id=" + id + " не найдена"));
    }

    private static EntityNotFoundException roomNotFound(Long id) {
        return new EntityNotFoundException("Комната с id=" + id + " не найдена");
    }
}
