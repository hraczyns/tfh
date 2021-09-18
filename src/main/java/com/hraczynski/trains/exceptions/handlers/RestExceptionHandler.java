package com.hraczynski.trains.exceptions.handlers;

import com.hraczynski.trains.exceptions.definitions.*;
import com.hraczynski.trains.exceptions.responses.ApiError;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.Objects;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(InvalidRequestException.class)
    protected ResponseEntity<Object> handleInvalidRequestException(InvalidRequestException exception) {
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST);
        error.setMessage(exception.getMessage());
        return buildResponseEntity(error);
    }

    @ExceptionHandler(BadStopTimeIdsFormatRequestException.class)
    protected ResponseEntity<Object> handleBadStopTimeIdsFormatRequestException(BadStopTimeIdsFormatRequestException exception) {
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST);
        error.setMessage(exception.getMessage());
        return buildResponseEntity(error);
    }

    @ExceptionHandler(CannotBindToReservationException.class)
    protected ResponseEntity<Object> handleCannotBindToReservationException(CannotBindToReservationException exception) {
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST);
        error.setMessage(exception.getMessage());
        return buildResponseEntity(error);
    }

    @ExceptionHandler(InputDoesNotMatchesPattern.class)
    protected ResponseEntity<Object> handleInputDoesNotMatchesPattern(InputDoesNotMatchesPattern exception) {
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST);
        error.setMessage(exception.getMessage());
        return buildResponseEntity(error);
    }

    @ExceptionHandler(CannotReceiveImagesException.class)
    protected ResponseEntity<Object> handleCannotReceiveImagesException(CannotReceiveImagesException exception) {
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST);
        error.setMessage(exception.getMessage());
        return buildResponseEntity(error);
    }

    @ExceptionHandler(NonUniqueFieldException.class)
    protected ResponseEntity<Object> handleNonUniqueFieldException(NonUniqueFieldException exception) {
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST);
        error.setMessage(exception.getMessage());
        return buildResponseEntity(error);
    }

    @ExceptionHandler(IncoherentDataException.class)
    protected ResponseEntity<Object> handleIncoherentDataException(IncoherentDataException exception) {
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST);
        error.setMessage(exception.getMessage());
        return buildResponseEntity(error);
    }

    @ExceptionHandler(InvalidDataAccessResourceUsageException.class)
    protected ResponseEntity<Object> handleInvalidDataAccessResourceUsageException(InvalidDataAccessResourceUsageException exception) {
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST);
        error.setMessage("Internal critical api error!");
        error.setDebugMessage(exception.getMessage());
        return buildResponseEntity(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException exception) {
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST);
        error.addValidationErrors(exception.getConstraintViolations());
        error.setMessage("Validation error!");
        return buildResponseEntity(error);
    }

    @ExceptionHandler(InvalidRouteInput.class)
    protected ResponseEntity<Object> handleInvalidRouteInput(InvalidRouteInput exception) {
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST);
        error.setMessage(exception.getMessage());
        return buildResponseEntity(error);
    }

    @ExceptionHandler(CannotCalculatePriceException.class)
    protected ResponseEntity<Object> handleCannotCalculatePriceException(CannotCalculatePriceException exception) {
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST);
        error.setMessage(exception.getMessage());
        return buildResponseEntity(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setMessage(String.format("The parameter '%s' of value '%s' could not be converted to type '%s'", ex.getName(), ex.getValue(), Objects.requireNonNull(ex.getRequiredType()).getSimpleName()));
        apiError.setDebugMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(IllegalContextForParameter.class)
    protected ResponseEntity<Object> handleIllegalContextForParameter(IllegalContextForParameter e) {
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST);
        error.setMessage(e.getMessage());
        return buildResponseEntity(error);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFound(
            EntityNotFoundException ex) {
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
        apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(RouteNotExistException.class)
    protected ResponseEntity<Object> handleRouteNotExist(RouteNotExistException e) {
        ApiError error = new ApiError(HttpStatus.NOT_FOUND);
        error.setMessage(e.getMessage());
        return buildResponseEntity(error);
    }

    @ExceptionHandler(JpaObjectRetrievalFailureException.class)
    protected ResponseEntity<Object> handleEntityNotFoundOriginalException(
            JpaObjectRetrievalFailureException ex) {
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
        apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        if (!(ex.getCause() instanceof org.hibernate.exception.ConstraintViolationException)) {
            return buildResponseEntity(new ApiError(HttpStatus.CONFLICT, "Database error", ex.getCause()));
        }
        return buildResponseEntity(new ApiError(HttpStatus.CONFLICT, "Entity to delete seems to be used and cannot be removed due to bindings with other elements. Deleting would cause destructive behaviours on api and database. Try to delele elements that agregate entity to allow operation at this endpoint.", ex));
    }

    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    protected ResponseEntity<Object> handleInvalidDataAccessApiUsageException(InvalidDataAccessApiUsageException ex) {
        String message = "The probably reason of this error is malformed JSON input.";
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, message, ex);
        return buildResponseEntity(apiError);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String error = "Malformed JSON request. Please validate input JSON, probably has invalid data, which cannot be parsed or detected.";
        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, error, ex));
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String error = "Error writing JSON output";
        return buildResponseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, error, ex));
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String error = "Media type not supported. Please choose JSON or XML format.";

        return buildResponseEntity(new ApiError(HttpStatus.UNSUPPORTED_MEDIA_TYPE, error, ex));
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setMessage(String.format("Could not find the %s method for URL %s", ex.getHttpMethod(), ex.getRequestURL()));
        apiError.setDebugMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String error = "Method " + ex.getMethod() + " is not allowed on this endpoint or the endpoint is not supported.";
        ApiError apiError = new ApiError(HttpStatus.METHOD_NOT_ALLOWED, error, ex);
        return buildResponseEntity(apiError);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setMessage("Validation error");
        apiError.addValidationErrors(ex.getBindingResult().getFieldErrors());
        apiError.addValidationError(ex.getBindingResult().getGlobalErrors());
        return buildResponseEntity(apiError);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {
        String error = ex.getParameterName() + " parameter is missing";
        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, error, ex));
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

}