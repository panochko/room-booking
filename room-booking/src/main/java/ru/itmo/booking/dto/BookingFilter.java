package ru.itmo.booking.dto;

import jakarta.persistence.criteria.Path;
import org.springframework.data.jpa.domain.Specification;
import ru.itmo.booking.domain.Booking;
import ru.itmo.booking.domain.BookingStatus;

import java.time.Instant;

/**
 * Параметры фильтрации броней; незаполненные параметры игнорируются.
 *
 * @param roomId     идентификатор комнаты
 * @param ownerEmail точный email автора без учёта регистра
 * @param status     статус брони
 * @param from       брони, начинающиеся не раньше этого момента
 * @param to         брони, начинающиеся не позже этого момента
 */
public record BookingFilter(
        Long roomId,
        String ownerEmail,
        BookingStatus status,
        Instant from,
        Instant to) {

    /**
     * Собирает спецификацию из заполненных параметров.
     *
     * @return спецификация для {@code findAll}
     */
    public Specification<Booking> toSpecification() {
        return Specification.allOf(
                roomId == null ? null : (root, query, cb) -> cb.equal(root.get("room").get("id"), roomId),
                ownerEmail == null || ownerEmail.isBlank() ? null
                        : (root, query, cb) -> cb.equal(cb.lower(root.get("ownerEmail")), ownerEmail.toLowerCase()),
                status == null ? null : (root, query, cb) -> cb.equal(root.get("status"), status),
                startsBetween(from, to));
    }

    private static Specification<Booking> startsBetween(Instant from, Instant to) {
        return from == null && to == null ? null : (root, query, cb) -> {
            Path<Instant> start = root.get("startTime");
            return cb.and(
                    from == null ? cb.conjunction() : cb.greaterThanOrEqualTo(start, from),
                    to == null ? cb.conjunction() : cb.lessThanOrEqualTo(start, to));
        };
    }
}
