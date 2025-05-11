package roomescape.reservation.presentation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static roomescape.testFixture.Fixture.RESERVATION_1;
import static roomescape.testFixture.Fixture.RESERVATION_DETAIL_DATA;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import roomescape.ResolverEnabledConfig;
import roomescape.TestInterceptorConfiguration;
import roomescape.reservation.application.ReservationQueryService;
import roomescape.reservation.application.ReservationService;
import roomescape.reservation.application.dto.ReservationDto;
import roomescape.reservation.infrastructure.dto.ReservationDetailData;
import roomescape.reservation.presentation.dto.request.ReservationRequest;

@WebMvcTest(ReservationController.class)
@Import({TestInterceptorConfiguration.class, ResolverEnabledConfig.class})
public class ReservationControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservationService reservationService;

    @MockitoBean
    private ReservationQueryService reservationQueryService;

    @Test
    @DisplayName("예약 생성 요청이 성공하면 201 상태코드와 예약 정보를 응답한다")
    void createReservationSuccess() throws Exception {
        // given
        String requestJson = """
                {
                    "date" : "2025-05-12",
                    "timeId" : 1,
                    "themeId" : 1
                }
                """;

        ReservationDto reservationDto = ReservationDto.from(RESERVATION_1);
        Long memberId = RESERVATION_1.getMemberId();
        given(reservationService.registerReservationForUser(any(ReservationRequest.class),  eq(memberId)))
                .willReturn(reservationDto);

        ReservationDetailData reservationDetailData = RESERVATION_DETAIL_DATA;
        given(reservationQueryService.getReservationDetailById(RESERVATION_1.getId()))
                .willReturn(reservationDetailData);

        // when
        ResultActions result = mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                ).andDo(print());

        // then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.member.id").value(reservationDetailData.member().id()))
                .andExpect(jsonPath("$.member.name").value(reservationDetailData.member().name()));

        verify(reservationService).registerReservationForUser(any(ReservationRequest.class), eq(memberId));
    }

    @Test
    @DisplayName("요청 시 정보가 빠져있으면 400 응답")
    void error_createReservation_whenInvalidInput() throws Exception {
        // given
        String requestJson = """
                {
                    "date" : "2025-05-12",
                    "timeId" : 1,
                }
                """;

        ReservationDto reservationDto = ReservationDto.from(RESERVATION_1);
        Long memberId = RESERVATION_1.getMemberId();
        given(reservationService.registerReservationForUser(any(ReservationRequest.class),  eq(memberId)))
                .willReturn(reservationDto);

        given(reservationQueryService.getReservationDetailById(RESERVATION_1.getId()))
                .willReturn(RESERVATION_DETAIL_DATA);

        // when
        ResultActions result = mockMvc.perform(post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        ).andDo(print());

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("잘못된 형식의 날짜로 요청 시 400 응답")
    void error_createReservation_invalidFormattedDate() throws Exception {
        // given
        String requestJson = """
                {
                    "date" : "2025555-05-12",
                    "timeId" : 1
                }
                """;

        ReservationDto reservationDto = ReservationDto.from(RESERVATION_1);
        Long memberId = RESERVATION_1.getMemberId();
        given(reservationService.registerReservationForUser(any(ReservationRequest.class),  eq(memberId)))
                .willReturn(reservationDto);

        given(reservationQueryService.getReservationDetailById(RESERVATION_1.getId()))
                .willReturn(RESERVATION_DETAIL_DATA);

        // when
        ResultActions result = mockMvc.perform(post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        ).andDo(print());

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("잘못된 형식의 요청 시 400 응답")
    void error_createReservation_whenNotReadableJson() throws Exception {
        // given
        String requestJson = """
                {
                    wrong request Body
                }
                """;

        ReservationDto reservationDto = ReservationDto.from(RESERVATION_1);
        Long memberId = RESERVATION_1.getMemberId();
        given(reservationService.registerReservationForUser(any(ReservationRequest.class),  eq(memberId)))
                .willReturn(reservationDto);

        given(reservationQueryService.getReservationDetailById(RESERVATION_1.getId()))
                .willReturn(RESERVATION_DETAIL_DATA);

        // when
        ResultActions result = mockMvc.perform(post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        ).andDo(print());

        // then
        result.andExpect(status().isBadRequest());
    }
}
