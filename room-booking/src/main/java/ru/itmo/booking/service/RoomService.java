package ru.itmo.booking.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.booking.domain.Room;
import ru.itmo.booking.dto.RoomRequest;
import ru.itmo.booking.dto.RoomResponse;
import ru.itmo.booking.repository.RoomRepository;

/**
 * Бизнес-логика управления комнатами.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomService {

    private final RoomRepository repository;

    /**
     * Возвращает страницу комнат вместимостью не меньше заданной.
     *
     * @param minCapacity минимальная вместимость
     * @param pageable    параметры пагинации
     * @return страница комнат
     */
    public Page<RoomResponse> findAll(int minCapacity, Pageable pageable) {
        return repository.findByCapacityGreaterThanEqual(minCapacity, pageable).map(RoomResponse::from);
    }

    /**
     * Возвращает комнату по идентификатору.
     *
     * @param id идентификатор
     * @return комната
     * @throws EntityNotFoundException если комната не найдена
     */
    public RoomResponse get(Long id) {
        return RoomResponse.from(find(id));
    }

    /**
     * Создаёт комнату.
     *
     * @param request данные комнаты
     * @return созданная комната
     */
    @Transactional
    public RoomResponse create(RoomRequest request) {
        return RoomResponse.from(repository.save(request.applyTo(new Room())));
    }

    /**
     * Полностью обновляет комнату.
     *
     * @param id      идентификатор
     * @param request новые данные
     * @return обновлённая комната
     * @throws EntityNotFoundException если комната не найдена
     */
    @Transactional
    public RoomResponse update(Long id, RoomRequest request) {
        return RoomResponse.from(request.applyTo(find(id)));
    }

    /**
     * Удаляет комнату; если у неё есть брони, БД не позволит это сделать.
     *
     * @param id идентификатор
     * @throws EntityNotFoundException если комната не найдена
     */
    @Transactional
    public void delete(Long id) {
        repository.delete(find(id));
    }

    private Room find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Комната с id=" + id + " не найдена"));
    }
}
