package roomescape.auth.infrastructure;

import static roomescape.auth.constants.AuthConstants.COOKIE_TOKEN_KEY;

import jakarta.servlet.http.Cookie;
import org.springframework.stereotype.Component;

@Component
public class CookieProvider {

    public Cookie createTokenCookie(String token) {
        Cookie cookie = new Cookie(COOKIE_TOKEN_KEY, token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        return cookie;
    }
}
