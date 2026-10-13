package ru.itmo.booking.web;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;

/**
 * Преобразует исключения в ответы формата RFC 9457 ({@link ProblemDetail}).
 * Исключения {@code ResponseStatusException} обрабатываются базовым классом.
 */
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Комната или бронь не найдена.
     *
     * @param e исключение
     * @return ответ 404
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail notFound(EntityNotFoundException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    /**
     * Нарушено ограничение БД: дубликат названия комнаты или комната со связанными бронями.
     *
     * @param e исключение
     * @return ответ 409
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail conflict(DataIntegrityViolationException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                "Нарушено ограничение данных: название уже занято или есть связанные брони");
    }

    /**
     * Запись изменил параллельный запрос.
     *
     * @param e исключение
     * @return ответ 409
     */
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ProblemDetail optimisticLock(ObjectOptimisticLockingFailureException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "Запись уже изменена, повторите запрос");
    }

    /**
     * Ошибки валидации тела запроса с перечнем невалидных полей.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ProblemDetail body = ex.getBody();
        body.setProperty("errors", ex.getFieldErrors().stream().collect(Collectors.toMap(
                FieldError::getField, f -> String.valueOf(f.getDefaultMessage()), (a, b) -> a)));
        return handleExceptionInternal(ex, body, headers, status, request);
    }
}
