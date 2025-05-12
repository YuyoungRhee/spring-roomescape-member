package roomescape.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static roomescape.auth.exception.AuthErrorCode.INVALID_PASSWORD;
import static roomescape.auth.exception.AuthErrorCode.INVALID_TOKEN;
import static roomescape.auth.exception.AuthErrorCode.MEMBER_NOT_FOUND;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import roomescape.auth.application.dto.TokenDto;
import roomescape.auth.exception.AuthorizationException;
import roomescape.auth.infrastructure.JwtTokenProvider;
import roomescape.auth.presentation.dto.request.TokenRequest;
import roomescape.member.domain.Member;
import roomescape.member.domain.Role;
import roomescape.member.domain.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    JwtTokenProvider jwtTokenProvider;

    @Mock
    MemberRepository memberRepository;

    @InjectMocks
    AuthService authService;

    @Test
    @DisplayName("이메일과 비밀번호로 토큰을 생성할 수 있다")
    void createToken_success() {
        // given
        TokenRequest request = new TokenRequest("email@test.com", "1234");
        long memberId = 1L;
        Member member = new Member(memberId, "email@test.com", "1234", "멍구", Role.USER);

        given(memberRepository.findByEmail("email@test.com")).willReturn(Optional.of(member));
        String payload = String.valueOf(memberId);
        given(jwtTokenProvider.createToken(payload, member.getRole())).willReturn("token-value");

        // when
        TokenDto response = authService.createToken(request);


        // then
        assertThat(response.accessToken()).isEqualTo("token-value");
        verify(jwtTokenProvider).createToken(payload, member.getRole());
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않으면 예외가 발생한다")
    void createToken_wrongPassword() {
        // given
        TokenRequest request = new TokenRequest("email@test.com", "wrong");
        Member member = new Member(1L, "email@test.com", "password", "멍구", Role.USER);

        given(memberRepository.findByEmail("email@test.com")).willReturn(Optional.of(member));

        // when & then
        assertThatThrownBy(() -> authService.createToken(request))
                .isInstanceOf(AuthorizationException.class)
                .hasMessage(INVALID_PASSWORD.getMessage());
    }

    @DisplayName("존재하지 않는 이메일로 로그인 시도하면 예외를 발생시킨다.")
    @Test
    void createToken_emailNotFound() {
        // given
        TokenRequest tokenRequest = new TokenRequest("notfound@email.com", "password");
        given(memberRepository.findByEmail(tokenRequest.email())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.createToken(tokenRequest))
                .isInstanceOf(AuthorizationException.class)
                .hasMessageContaining(MEMBER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("member를 id로 조회할 수 있다.")
    void findMemberByToken_success() {
        // given
        String email = "email@test.com";
        long memberId = 1L;
        Member member = new Member(memberId, email, "pass", "멍구", Role.USER);

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

        // when
        Member result = authService.getMemberById(memberId);

        // then
        assertThat(result.getName()).isEqualTo("멍구");
        verify(memberRepository).findById(memberId);
    }

    @Test
    @DisplayName("찾을 수 없는 memberId는 유효성 예외를 발생시킨다.")
    void findMemberByToken_invalidToken() {
        // given
        long invalidMemberId = 99L;

        given(memberRepository.findById(invalidMemberId)).willThrow(new AuthorizationException(INVALID_TOKEN));

        // when & then
        assertThatThrownBy(() -> authService.getMemberById(invalidMemberId))
                .isInstanceOf(AuthorizationException.class)
                .hasMessage(INVALID_TOKEN.getMessage());
    }
}
