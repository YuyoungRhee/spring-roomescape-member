package roomescape.auth.infrastructure;

import static roomescape.auth.constants.AuthConstants.COOKIE_TOKEN_KEY;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class CookieAuthorizationExtractor implements AuthorizationExtractor<Optional<String>> {

    public Optional<String> extract(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return Optional.empty();
        }

        return Arrays.stream(cookies)
                .filter(cookie -> COOKIE_TOKEN_KEY.equals(cookie.getName()))
                .findAny()
                .map(Cookie::getValue);
    }
}
