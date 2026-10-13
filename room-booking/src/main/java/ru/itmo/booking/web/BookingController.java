package ru.itmo.booking.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.booking.dto.BookingFilter;
import ru.itmo.booking.dto.BookingRequest;
import ru.itmo.booking.dto.BookingResponse;
import ru.itmo.booking.service.BookingService;

/**
 * REST-контроллер бронирований.
 */
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService service;

    /**
     * Возвращает брони с фильтрацией, пагинацией и сортировкой.
     *
     * @param filter   параметры фильтрации из query-строки
     * @param pageable параметры {@code page}, {@code size}, {@code sort}
     * @return страница броней
     */
    @GetMapping
    public Page<BookingResponse> search(BookingFilter filter, @PageableDefault(sort = "startTime") Pageable pageable) {
        return service.search(filter, pageable);
    }

    /**
     * Возвращает бронь по идентификатору.
     *
     * @param id идентификатор
     * @return бронь
     */
    @GetMapping("/{id}")
    public BookingResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    /**
     * Создаёт бронь.
     *
     * @param request данные брони
     * @return созданная бронь
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse create(@Valid @RequestBody BookingRequest request) {
        return service.create(request);
    }

    /**
     * Отменяет бронь.
     *
     * @param id идентификатор
     * @return отменённая бронь
     */
    @PatchMapping("/{id}/cancel")
    public BookingResponse cancel(@PathVariable Long id) {
        return service.cancel(id);
    }
}
