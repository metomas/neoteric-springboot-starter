package pl.poznachowski.springboot.controller;

import com.neoteric.starter.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller // @RestController
public class CustomErrorController implements ErrorController {

    private static final String DEFAULT_PATH = "/error";

    @Autowired
    private ErrorAttributes errorAttributes;

    @Override
    public String getErrorPath() {
        return DEFAULT_PATH;
    }

    @RequestMapping(value = DEFAULT_PATH)
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request, HttpServletResponse response) {
        boolean includeStackTrace = false;
        Map<String, Object> errorAttributes = getErrorAttributes(request, includeStackTrace);
        errorAttributes.put("contentType", request.getContentType());
        errorAttributes.put("requestId", response.getHeader(Constants.REQUEST_ID));
        return new ResponseEntity<Map<String, Object>>(errorAttributes, HttpStatus.valueOf(response.getStatus()));
    }

    private Map<String, Object> getErrorAttributes(HttpServletRequest request, boolean includeStackTrace) {
        ServletRequestAttributes requestAttributes = new ServletRequestAttributes(request);
        return errorAttributes.getErrorAttributes(requestAttributes, includeStackTrace);
    }
}
