package ru.itmo.booking.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Бронирование комнаты на промежуток времени.
 */
@Entity
@Table(name = "bookings", indexes = @Index(name = "idx_bookings_room_period", columnList = "room_id, start_time, end_time"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    /** Идентификатор. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Забронированная комната. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id")
    private Room room;

    /** Email автора брони. */
    @Column(nullable = false)
    private String ownerEmail;

    /** Начало брони. */
    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    /** Конец брони. */
    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    /** Статус. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private BookingStatus status;

    /** Версия для оптимистичной блокировки. */
    @Version
    private Long version;
}
