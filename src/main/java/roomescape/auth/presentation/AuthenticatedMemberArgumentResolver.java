package roomescape.auth.presentation;

import static roomescape.auth.constants.AuthConstants.JWT_PAYLOAD;
import static roomescape.auth.exception.AuthErrorCode.LOGIN_REQUIRED;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import roomescape.auth.application.AuthService;
import roomescape.auth.exception.AuthorizationException;
import roomescape.auth.infrastructure.JwtPayload;
import roomescape.auth.presentation.dto.CurrentMember;
import roomescape.member.domain.Member;

@Component
public class AuthenticatedMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final AuthService authService;

    public AuthenticatedMemberArgumentResolver(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthenticatedMember.class)
                && parameter.getParameterType().equals(CurrentMember.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        JwtPayload jwtPayload = (JwtPayload) request.getAttribute(JWT_PAYLOAD);
        if (jwtPayload == null) {
            throw new AuthorizationException(LOGIN_REQUIRED);
        }

        Member member = authService.getMemberById(jwtPayload.id());
        return new CurrentMember(member.getId(), member.getName());
    }
}
