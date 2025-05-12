package roomescape;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.List;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import roomescape.auth.presentation.AuthenticatedMemberArgumentResolver;
import roomescape.auth.presentation.dto.CurrentMember;

@TestConfiguration
public class ResolverEnabledConfig implements WebMvcConfigurer {
    @Bean
    public AuthenticatedMemberArgumentResolver authenticatedMemberArgumentResolver() {
        AuthenticatedMemberArgumentResolver resolver = mock(AuthenticatedMemberArgumentResolver.class);
        given(resolver.supportsParameter(any())).willReturn(true);
        given(resolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(new CurrentMember(1L, "멍구"));
        return resolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authenticatedMemberArgumentResolver());
    }
}
