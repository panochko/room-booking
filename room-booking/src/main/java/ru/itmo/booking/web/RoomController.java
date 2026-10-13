package ru.itmo.booking.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.booking.dto.RoomRequest;
import ru.itmo.booking.dto.RoomResponse;
import ru.itmo.booking.dto.TimeSlot;
import ru.itmo.booking.service.BookingService;
import ru.itmo.booking.service.RoomService;

import java.time.LocalDate;
import java.util.List;

/**
 * REST-контроллер комнат.
 */
@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService rooms;
    private final BookingService bookings;

    /**
     * Возвращает комнаты с пагинацией и сортировкой.
     *
     * @param minCapacity минимальная вместимость
     * @param pageable    параметры {@code page}, {@code size}, {@code sort}
     * @return страница комнат
     */
    @GetMapping
    public Page<RoomResponse> findAll(@RequestParam(defaultValue = "0") int minCapacity,
                                      @PageableDefault(sort = "id") Pageable pageable) {
        return rooms.findAll(minCapacity, pageable);
    }

    /**
     * Возвращает комнату по идентификатору.
     *
     * @param id идентификатор
     * @return комната
     */
    @GetMapping("/{id}")
    public RoomResponse get(@PathVariable Long id) {
        return rooms.get(id);
    }

    /**
     * Возвращает свободные окна комнаты на сутки по UTC.
     *
     * @param id   идентификатор комнаты
     * @param date дата в формате {@code yyyy-MM-dd}
     * @return свободные окна
     */
    @GetMapping("/{id}/availability")
    public List<TimeSlot> availability(@PathVariable Long id,
                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return bookings.availability(id, date);
    }

    /**
     * Создаёт комнату.
     *
     * @param request данные комнаты
     * @return созданная комната
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoomResponse create(@Valid @RequestBody RoomRequest request) {
        return rooms.create(request);
    }

    /**
     * Полностью обновляет комнату.
     *
     * @param id      идентификатор
     * @param request новые данные
     * @return обновлённая комната
     */
    @PutMapping("/{id}")
    public RoomResponse update(@PathVariable Long id, @Valid @RequestBody RoomRequest request) {
        return rooms.update(id, request);
    }

    /**
     * Удаляет комнату.
     *
     * @param id идентификатор
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        rooms.delete(id);
    }
}
