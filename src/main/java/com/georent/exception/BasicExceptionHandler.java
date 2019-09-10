package com.georent.exception;

import com.georent.message.GeoRentIHttpStatus;
import com.georent.message.Message;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class BasicExceptionHandler {


    /**
     * Exception handler for LotNotFoundException
     *
     * @param ex      - The exception.
     * @param request The http request that caused the exception.
     * @return 404 "Not found" status and additional info.
     */
    @ExceptionHandler({LotNotFoundException.class})
    protected ResponseEntity<?> handleLotNotFound(LotNotFoundException ex,
                                                  HttpServletRequest request) {
        GenericResponse<String> response = new GenericResponse<>(
                request.getMethod(),
                request.getRequestURI(),
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                Message.ERROR.getDescription());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Exception handler for SuchUserExistsException.
     *
     * @param ex      - The exception.
     * @param request The http request that caused the exception.
     * @return 452 "REGISTRATION_USER_ERROR" and "HttpStatus.CONFLICT"  status and additional info.
     */
    @ExceptionHandler({RegistrationSuchUserExistsException.class})
    protected ResponseEntity<?> handleSuchUserExists(RegistrationSuchUserExistsException ex,
                                                     HttpServletRequest request) {
        GenericResponse<String> response = new GenericResponse<>(
                request.getMethod(),
                request.getRequestURI(),
                ex.getMessage(),
                GeoRentIHttpStatus.getValue(ex.getMessage()),
                Message.ERROR.getDescription());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * Exception handler for SuchUserExistsException.
     *
     * @param ex      - The exception.
     * @param request The http request that caused the exception.
     * @return 453 or 454 "INVALID_FILE_... " (multipartfile to s3) and "HttpStatus.PRECONDITION_FAILED"  status and additional info.
     */
    @ExceptionHandler({MultiPartFileValidationException.class})
    protected ResponseEntity<?> handleMultiPartFile(MultiPartFileValidationException ex,
                                                    HttpServletRequest request) {
        GenericResponse<String> response = new GenericResponse<>(
                request.getMethod(),
                request.getRequestURI(),
                ex.getMessage(),
                GeoRentIHttpStatus.getValue(ex.getMessage()),
                Message.ERROR.getDescription());

        return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(response);
    }

    /**
     * if failed, redirects user-agent to "error.jsp", with return code of 301.
     *
     * @param ex
     * @param request
     * @return
     */

    @ExceptionHandler({ForgotException.class})
    protected ResponseEntity<?> handleForgotException(ForgotException ex,
                                                      HttpServletRequest request) {
        GenericResponse<String> response = new GenericResponse<>(
                request.getMethod(),
                request.getRequestURI(),
                ex.getMessage(),
                HttpStatus.MOVED_PERMANENTLY.value(),
                Message.MAIL_NOT_SENT.getDescription());

        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).body(response);
    }

    /**
     * Exception handler for fields with notBlank validation
     *
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<?> handleConstraintExceptions(ConstraintViolationException ex,
                                                           HttpServletRequest request) {
        GenericResponse<String> response = new GenericResponse<>(
                request.getMethod(),
                request.getRequestURI(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                Message.INVALID_VALIDATE.getDescription());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Exception handler for fields with notNull validation
     *
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(TransactionSystemException.class)
    protected ResponseEntity<?> handleransactionExceptions(TransactionSystemException ex,
                                                           HttpServletRequest request) {
        GenericResponse<String> response = new GenericResponse<>(
                request.getMethod(),
                request.getRequestURI(),
                ex.getCause().getCause().getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                Message.INVALID_VALIDATE.getDescription());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * TransactionException (search)
     *
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(CannotCreateTransactionException.class)
    protected ResponseEntity<?> handleTransactionExceptions(CannotCreateTransactionException ex,
                                                            HttpServletRequest request) {
        GenericResponse<String> response = new GenericResponse<>(
                request.getMethod(),
                request.getRequestURI(),
                ex.getCause().getCause().getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                Message.INVALID_CONNECTION.getDescription());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * PersistenceException (search addpess, lotName)
     *
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(SearchConnectionNotAvailableException.class)
    protected ResponseEntity<?> handlerSearchConnectionNotAvailableException(SearchConnectionNotAvailableException ex,
                                                                             HttpServletRequest request) {
        GenericResponse<String> response = new GenericResponse<>(
                request.getMethod(),
                request.getRequestURI(),
                ex.getCause().getMessage() + ": " + ex.getCause().getCause().getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                Message.INVALID_CONNECTION_SERCH.getDescription());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }


//    @ExceptionHandler(ConstraintViolationException.class)
//    public void springHandleConstraintExceptions(HttpServletResponse response) throws IOException {
//        response.sendError(HttpStatus.BAD_REQUEST.value());
//    }
//
//    @ExceptionHandler(TransactionSystemException.class)
//    public void springHandleTransactionExceptions(HttpServletResponse response) throws IOException {
//        response.sendError(HttpStatus.BAD_REQUEST.value());
//    }
}
