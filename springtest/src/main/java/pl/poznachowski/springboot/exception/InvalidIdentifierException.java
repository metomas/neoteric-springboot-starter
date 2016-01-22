package pl.poznachowski.springboot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Identifier cannot be null")
public class InvalidIdentifierException extends RuntimeException {

}
