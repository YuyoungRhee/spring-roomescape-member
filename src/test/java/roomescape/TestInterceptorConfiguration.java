package roomescape;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import roomescape.auth.presentation.AdminAuthorizationInterceptor;
import roomescape.auth.presentation.BasicAuthorizationInterceptor;

@TestConfiguration
public class TestInterceptorConfiguration implements WebMvcConfigurer {

    @Bean
    public BasicAuthorizationInterceptor basicAuthorizationInterceptor() {
        BasicAuthorizationInterceptor interceptor = mock(BasicAuthorizationInterceptor.class);
        given(interceptor.preHandle(any(), any(), any())).willReturn(true);
        return interceptor;
    }

    @Bean
    public AdminAuthorizationInterceptor adminAuthorizationInterceptor() {
        AdminAuthorizationInterceptor interceptor = mock(AdminAuthorizationInterceptor.class);
        given(interceptor.preHandle(any(), any(), any())).willReturn(true);
        return interceptor;
    }
}
