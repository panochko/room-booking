package ru.itmo.booking.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Переговорная комната.
 */
@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
public class Room {

    /** Идентификатор. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Уникальное название. */
    @Column(nullable = false, unique = true)
    private String name;

    /** Вместимость, человек. */
    @Column(nullable = false)
    private int capacity;

    /** Расположение, например этаж. */
    private String location;

    /** Доступна ли комната для бронирования. */
    @Column(nullable = false)
    private boolean active = true;
}
