package roomescape.auth.presentation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import roomescape.auth.infrastructure.JwtTokenProvider;
import roomescape.member.domain.Role;
import roomescape.reservation.presentation.dto.request.AdminReservationRequest;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("mock")
class AuthInterceptorTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Interceptor가 걸리지 않는 경로는 인증 없이 접근 가능")
    void publicApiAccessible() throws Exception {
        mockMvc.perform(get("/themes/ranking"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("BasicInterceptor가 걸리는 경로는 인증 없으면 401 반환")
    void protectedApiFailsWithoutAuth() throws Exception {
        mockMvc.perform(get("/reservations"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("BasicInterceptor가 걸리는 경로는 유효한 토큰의 회원이면 통과")
    void protectedApiWithAuthSucceeds() throws Exception {
        String token = jwtTokenProvider.createToken(1L, Role.USER);
        mockMvc.perform(get("/reservations")
                        .cookie(new Cookie("token", token)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("AdminInterceptor가 걸리는 경로는 인증 없으면 401 반환")
    void adminEndpointReturns401WithoutToken() throws Exception {
        mockMvc.perform(post("/admin/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(createSampleAdminRequest()))
        ).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("AdminInterceptor가 걸리는 경로는 인증은 됐으나 권한이 없으면 403 반환")
    void adminApiFailsWithoutPermission() throws Exception {
        String token = jwtTokenProvider.createToken(1L, Role.USER);
        mockMvc.perform(post("/admin/reservations")
                .cookie(new Cookie("token", token))
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(createSampleAdminRequest()))
        ).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("AdminInterceptor가 걸리는 경로는 admin 권한을 가진 회원이면 통과")
    void adminAccessAllowedForAdminRole() throws Exception {
        String token = jwtTokenProvider.createToken(1L, Role.ADMIN);
        mockMvc.perform(post("/admin/reservations")
                .cookie(new Cookie("token", token))
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(createSampleAdminRequest()))
        ).andExpect(status().isCreated());
    }

    private AdminReservationRequest createSampleAdminRequest() {
        LocalDate date = LocalDate.now().plusDays(1);
        return new AdminReservationRequest(date, 1L, 1L, 1L);
    }

    private String toJson(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }
}
