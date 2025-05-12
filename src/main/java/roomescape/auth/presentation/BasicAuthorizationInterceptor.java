package roomescape.auth.presentation;

import static roomescape.auth.constants.AuthConstants.JWT_PAYLOAD;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.auth.exception.AuthErrorCode;
import roomescape.auth.exception.AuthorizationException;
import roomescape.auth.infrastructure.CookieAuthorizationExtractor;
import roomescape.auth.infrastructure.JwtPayload;
import roomescape.auth.infrastructure.JwtTokenProvider;

@Component
public class BasicAuthorizationInterceptor implements HandlerInterceptor {

    private final CookieAuthorizationExtractor extractor;
    private final JwtTokenProvider jwtTokenProvider;

    public BasicAuthorizationInterceptor(CookieAuthorizationExtractor extractor,
                                         JwtTokenProvider jwtTokenProvider) {
        this.extractor = extractor;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        Optional<String> result = extractor.extract(request);
        if (result.isEmpty()) {
            throw new AuthorizationException(AuthErrorCode.LOGIN_REQUIRED);
        }

        String token = result.get();
        validateToken(token);

        JwtPayload payLoad = jwtTokenProvider.getPayLoad(token);

        request.setAttribute(JWT_PAYLOAD, payLoad);
        return true;
    }

    private void validateToken(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new AuthorizationException(AuthErrorCode.INVALID_TOKEN);
        }
    }
}
