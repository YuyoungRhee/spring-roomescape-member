package roomescape;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import roomescape.auth.presentation.AuthenticatedMemberArgumentResolver;

@TestConfiguration
public class ResolverDisabledConfig implements WebMvcConfigurer {
    @Bean
    public AuthenticatedMemberArgumentResolver authenticatedMemberArgumentResolver() {
        AuthenticatedMemberArgumentResolver resolver = mock(AuthenticatedMemberArgumentResolver.class);
        given(resolver.supportsParameter(any())).willReturn(false);
        return resolver;
    }
}
