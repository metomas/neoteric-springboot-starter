package pl.poznachowski.springboot.exception;

import org.springframework.boot.autoconfigure.web.DefaultErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;

import javax.servlet.RequestDispatcher;
import java.util.Map;

@Component
public class CustomErrorAttributes extends DefaultErrorAttributes {

    @Bean
    public ErrorAttributes errorAttributes() {
        return new CustomErrorAttributes();
    }

    @Override
    public Map<String, Object> getErrorAttributes(RequestAttributes requestAttributes, boolean includeStackTrace) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(requestAttributes, includeStackTrace);

        Object exceptionType = requestAttributes.getAttribute(RequestDispatcher.ERROR_EXCEPTION_TYPE, RequestAttributes.SCOPE_REQUEST);
        if (exceptionType != null) {
            errorAttributes.put("errorType", exceptionType);
        }
        return errorAttributes;
    }

}
