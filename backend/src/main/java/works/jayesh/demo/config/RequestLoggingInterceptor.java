package works.jayesh.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Interceptor for structured request/response logging
 * Logs incoming requests and outgoing responses with correlation IDs
 */
@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);

        log.info("Incoming Request | Method: {} | URI: {} | RemoteAddr: {}",
                request.getMethod(),
                request.getRequestURI(),
                request.getRemoteAddr());

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception ex) {
        long startTime = (Long) request.getAttribute("startTime");
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        String logLevel = response.getStatus() >= 400 ? "WARN" : "INFO";

        if (response.getStatus() >= 400) {
            log.warn("Request Completed | Method: {} | URI: {} | Status: {} | Duration: {}ms",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    duration);
        } else {
            log.info("Request Completed | Method: {} | URI: {} | Status: {} | Duration: {}ms",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    duration);
        }

        if (ex != null) {
            log.error("Request Failed | Method: {} | URI: {} | Exception: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    ex.getMessage(), ex);
        }
    }
}
