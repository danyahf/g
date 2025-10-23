package com.danya;

import com.danya.security.authentication.AuthUser;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;

public final class AuthUserRequestPostProcessors {
    private AuthUserRequestPostProcessors() {
    }

    public static RequestPostProcessor authUser(String username, List<?> roles) {
        return (MockHttpServletRequest req) -> {
            req.setAttribute("authUser", new AuthUser(username, roles));
            return req;
        };
    }
}
