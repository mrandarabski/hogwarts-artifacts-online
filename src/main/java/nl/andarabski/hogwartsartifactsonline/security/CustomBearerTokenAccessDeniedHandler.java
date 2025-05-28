package nl.andarabski.hogwartsartifactsonline.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

/**
 * This class handle unsuccessful JWT authorization.
 */
@Component
public class CustomBearerTokenAccessDeniedHandler implements AccessDeniedHandler {

    /**
     * Here we've injected the DefaultHandlerExceptionResolver and delegated the handler to this resolver
     * This security exception can now be handled with controller advice with an exception handler method
     */

    private final HandlerExceptionResolver resolver;

    public CustomBearerTokenAccessDeniedHandler(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        this.resolver.resolveException(request, response, null, accessDeniedException);
    }
}
