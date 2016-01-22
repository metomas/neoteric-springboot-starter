package pl.poznachowski.springboot;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.poznachowski.springboot.exception.InvalidIdentifierException;

import javax.ws.rs.core.MediaType;

@RestController
@RequestMapping(path = "/user", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
public class SpringMVCUserEndpointController {
    private static final Logger LOG = LoggerFactory.getLogger(SpringMVCUserEndpointController.class);

    @RequestMapping(method = RequestMethod.GET)
    public String getUserUsingQueryVariable(@RequestParam String id) {
        final String userId = id.trim();
        validateId(userId);
        return "Query variable:" + id;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String getUserUsingPathVariable(@PathVariable String id) {
        final String userId = id.trim();
        validateId(userId);
        return "Path variable:" + id;
    }

    private void validateId(String id) {
        if (Strings.isNullOrEmpty(id))
            throw new InvalidIdentifierException();

        int parsedId = Integer.parseInt(id);
        if (parsedId < 0)
            throw new IllegalArgumentException();
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Unable to convert identifier to number")
    @ExceptionHandler(NumberFormatException.class)
    public void handleNumberFormatException() {
        LOG.error("Unable to parse identifier to integer");
    }
}
