package roomescape.auth.infrastructure;

import static roomescape.auth.exception.AuthErrorCode.INVALID_TOKEN;

import roomescape.auth.exception.AuthorizationException;
import roomescape.member.domain.Role;

public record JwtPayload(
        Long id,
        Role role
) {
    public JwtPayload {
        if (id == null || role == null) {
            throw new AuthorizationException(INVALID_TOKEN);
        }
    }
}
