package nl.andarabski.hogwartsartifactsonline.system.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.andarabski.hogwartsartifactsonline.system.Result;
import nl.andarabski.hogwartsartifactsonline.system.StatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    Result handelObjectNotFoundException(ObjectNotFoundException ex){
        return new Result(false, StatusCode.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Result handelValidationException(MethodArgumentNotValidException ex){
        List<ObjectError> errors = ex.getBindingResult().getAllErrors();
        Map<String, String> errorMap = new HashMap<>(errors.size());
        errors.forEach(error -> {
            String key = ((FieldError) error).getField();
            String value = ((FieldError) error).getDefaultMessage();
            errorMap.put(key, value);
        });
        return new Result(false, StatusCode.INVALID_ARGUMENT, "Provided arguments are invalid, see data for details.", errorMap );
    }


    @ExceptionHandler({UsernameNotFoundException.class, BadCredentialsException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    Result handelAuthentecationException(Exception ex){
        return new Result(false, StatusCode.UNAUTHORIZED, "username or password is incorrect.", ex.getMessage());
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    Result handleInsufficientAuthenticationException(InsufficientAuthenticationException ex) {
        return new Result(false, StatusCode.UNAUTHORIZED, "Login credentials are missing.", ex.getMessage());
    }

    @ExceptionHandler({AccountStatusException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    Result handelAccountStatusException(AccountStatusException ex){
        return new Result(false, StatusCode.UNAUTHORIZED, "User account is abnormal.", ex.getMessage());
    }

    @ExceptionHandler({InvalidBearerTokenException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    Result handelInvalidBearerException(InvalidBearerTokenException ex){
        return new Result(false, StatusCode.UNAUTHORIZED, "The access token is expired, revoked, malformed, or invalid for other reasons ", ex.getMessage());
    }

    @ExceptionHandler({AccessDeniedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    Result handelAccessDeniedException(AccessDeniedException ex){
        return new Result(false, StatusCode.FORBIDDEN, "No permission.", ex.getMessage());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    Result handleNoHandlerFoundException(NoHandlerFoundException ex) {
        return new Result(false, StatusCode.NOT_FOUND, "This API endpoint is not found.", ex.getMessage());
    }

    @ExceptionHandler({HttpClientErrorException.class, HttpServerErrorException.class})
    ResponseEntity<Result> handleRestClientException(HttpStatusCodeException ex) throws JsonProcessingException {
        String exceptionMessage = ex.getMessage();

        // Replace <EOL> with actual newlines.
        exceptionMessage = exceptionMessage.replace("<EOL>", "\n");

        // Extract the JSON part from the string.
        String jsonPart = exceptionMessage.substring(exceptionMessage.indexOf("{"), exceptionMessage.lastIndexOf("}") + 1);

        // Create an ObjectMapper instance.
        ObjectMapper mapper = new ObjectMapper();

        // Parse the JSON string to a JsonNode.
        JsonNode rootNode = mapper.readTree(jsonPart);

        // Extract the message.
        String formattedExceptionMessage = rootNode.path("error").path("message").asText();

        return new ResponseEntity<>(
                new Result(false,
                        ex.getStatusCode().value(),
                        "A rest client error occurs, see data for details.",
                        formattedExceptionMessage),
                ex.getStatusCode());
    }



//    @ExceptionHandler(CustomBlobStorageException.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    Result handleCustomBlobStorageException(CustomBlobStorageException ex) {
//        return new Result(false, StatusCode.INTERNAL_SERVER_ERROR, ex.getMessage(), ex.getCause().getMessage());
//    }

    @ExceptionHandler(PasswordChangeIllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Result handlePasswordChangeIllegalArgumentException(PasswordChangeIllegalArgumentException ex) {
        return new Result(false, StatusCode.INVALID_ARGUMENT, ex.getMessage());
    }

    /**
     * Fallback handles any unhandled exceptions.
     * @param ex
     * @return
     */
    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    Result handelOtherExceptions(Exception ex){
        return new Result(false, StatusCode.INTERNAL_SERVER_ERROR, "A server internal error occurs", ex.getMessage());
    }
}
