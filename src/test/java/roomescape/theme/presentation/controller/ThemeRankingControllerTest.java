package roomescape.theme.presentation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static roomescape.theme.exception.InputErrorCode.INVALID_DATE_RANGE;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import roomescape.ResolverDisabledConfig;
import roomescape.TestInterceptorConfiguration;
import roomescape.theme.application.ThemeService;

@WebMvcTest(ThemeController.class)
@Import({TestInterceptorConfiguration.class, ResolverDisabledConfig.class})
class ThemeRankingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ThemeService themeService;


    @Test
    @DisplayName("테마 랭킹 조회에서 시작 날짜가 종료 날짜보다 후라면 비정상적인 요청으로 판단하여 400 응답")
    void error_createTheme_whenInvalidRankingCondition() throws Exception {
        // given
        String startDate = "2025-02-01";
        String endDate = "2025-01-01";
        String limit = "10";

        // when
        ResultActions result = mockMvc.perform(get("/themes/ranking")
                .param("startDate", startDate)
                .param("endDate", endDate)
                .param("limit", limit)
        ).andDo(print());

        // then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(INVALID_DATE_RANGE.getMessage()));
    }
}
