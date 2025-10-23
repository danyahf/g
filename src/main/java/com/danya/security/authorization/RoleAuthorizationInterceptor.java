package com.danya.security.authorization;

import com.danya.security.authentication.AuthUser;
import com.danya.security.exception.ForbiddenException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RoleAuthorizationInterceptor implements HandlerInterceptor {
    private static final String ERROR_MESSAGE = "You don’t have permission to perform this action";

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {

        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        RequiredRoles requiredRoles = handlerMethod.getMethodAnnotation(RequiredRoles.class);
        if (requiredRoles == null) {
            return true;
        }

        AuthUser authUser = (AuthUser) request.getAttribute("authUser");
        List<?> roles = authUser.getRoles();
        boolean hasRequiredRole = Arrays.stream(requiredRoles.value()).anyMatch(roles::contains);

        if (!hasRequiredRole) {
            throw new ForbiddenException(ERROR_MESSAGE);
        }

        return true;
    }
}
