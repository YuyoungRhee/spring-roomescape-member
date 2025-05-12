package roomescape.auth.presentation;

import static roomescape.auth.constants.AuthConstants.JWT_PAYLOAD;
import static roomescape.auth.exception.AuthErrorCode.LOGIN_REQUIRED;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.auth.exception.AuthErrorCode;
import roomescape.auth.exception.AuthorizationException;
import roomescape.auth.infrastructure.JwtPayload;
import roomescape.member.domain.Role;

@Component
public class AdminAuthorizationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        JwtPayload jwtPayload = (JwtPayload) request.getAttribute(JWT_PAYLOAD);
        if (jwtPayload == null) {
            throw new AuthorizationException(LOGIN_REQUIRED);
        }

        validateAdminRole(jwtPayload);

        request.setAttribute(JWT_PAYLOAD, jwtPayload);
        return true;
    }

    private void validateAdminRole(JwtPayload jwtPayload) {
        if (jwtPayload.role() != Role.ADMIN) {
            throw new AuthorizationException(AuthErrorCode.FORBIDDEN_ACCESS);
        }
    }
}
