package pl.poznachowski.springboot.exception.controllerAdvise;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);

    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Unhandled exception")
    @ExceptionHandler(IllegalArgumentException.class)
    public void defaultErrorHandler() throws Exception {
        LOGGER.error("Unhandled exception");
    }

}
