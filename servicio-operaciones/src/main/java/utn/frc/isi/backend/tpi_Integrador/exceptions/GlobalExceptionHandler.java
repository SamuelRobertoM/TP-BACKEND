package utn.frc.isi.backend.tpi_Integrador.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import utn.frc.isi.backend.tpi_Integrador.dtos.ErrorResponseDTO;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para la aplicación
 * Captura y procesa errores de todos los controladores REST
 * Proporciona respuestas de error estandarizadas y logging apropiado
 */
@RestControllerAdvice // Indica que esta clase manejará excepciones de forma global para los @RestController
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Manejador para errores de validación de @Valid
     * Captura errores cuando los DTOs no cumplen las restricciones de validación
     * 
     * @param ex Excepción de validación lanzada por Spring
     * @param request Información de la petición HTTP
     * @return ResponseEntity con ErrorResponseDTO y status 400 Bad Request
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Error de validación: " + errors.toString(), // Mensaje detallado con campos
                request.getRequestURI()
        );
        logger.warn("Error de validación: {} en {}", errors, request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Manejador para IllegalStateException
     * Captura errores de estado ilegal (ej: intentar finalizar una solicitud que no está EN_TRANSITO)
     * 
     * @param ex Excepción de estado ilegal
     * @param request Información de la petición HTTP
     * @return ResponseEntity con ErrorResponseDTO y status 400 Bad Request
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalStateException(
            IllegalStateException ex, HttpServletRequest request) {

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(), // Mensaje específico del error de estado
                request.getRequestURI()
        );
        logger.warn("Estado ilegal: {} en {}", ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Manejador para IllegalArgumentException
     * Captura errores de argumentos inválidos (ej: parámetros fuera de rango)
     * 
     * @param ex Excepción de argumento ilegal
     * @param request Información de la petición HTTP
     * @return ResponseEntity con ErrorResponseDTO y status 400 Bad Request
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(), // Mensaje específico del error de argumento
                request.getRequestURI()
        );
        logger.warn("Argumento ilegal: {} en {}", ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Manejador para excepciones generales (inesperadas)
     * Captura cualquier RuntimeException no manejada por otros handlers
     * Registra el stack trace completo para debugging
     * 
     * @param ex Excepción no esperada
     * @param request Información de la petición HTTP
     * @return ResponseEntity con ErrorResponseDTO y status 500 Internal Server Error
     */
    @ExceptionHandler(RuntimeException.class) // Puedes usar Exception.class para capturar todo
    public ResponseEntity<ErrorResponseDTO> handleGenericExceptions(
            RuntimeException ex, HttpServletRequest request) {

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "Ocurrió un error inesperado. Por favor, intente más tarde.", // Mensaje genérico
                request.getRequestURI()
        );
        // ¡Importante! Registrar el error completo para debugging
        logger.error("Error inesperado procesando la solicitud {} {}: {}",
                     request.getMethod(), request.getRequestURI(), ex.getMessage(), ex); // Loguea el stack trace
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Puedes añadir más manejadores para excepciones específicas aquí
    // Ej: @ExceptionHandler(ResourceNotFoundException.class), etc.
}
