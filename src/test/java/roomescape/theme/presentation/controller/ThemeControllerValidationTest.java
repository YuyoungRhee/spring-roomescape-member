package roomescape.theme.presentation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import roomescape.theme.application.ThemeService;
import roomescape.theme.application.dto.ThemeDto;
import roomescape.theme.presentation.dto.request.ThemeRequest;

@WebMvcTest(ThemeController.class)
@Import({TestInterceptorConfiguration.class, ResolverEnabledConfig.class})
public class ThemeControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ThemeService themeService;

    @Test
    @DisplayName("테마 생성 요청이 성공하면 201 상태코드와 테마 정보를 응답한다")
    void createThemeSuccess() throws Exception {
        // given
        String requestJson = """
                {
                    "name": "테마",
                    "description": "테마설명",
                    "thumbnail": "썸네일.jpg"
                }
                """;

        ThemeDto themeDto = new ThemeDto(1L, "테마", "테마설명", "썸네일.jpg");
        given(themeService.registerTheme(any(ThemeRequest.class))).willReturn(themeDto);

        // when
        ResultActions result = mockMvc.perform(post("/themes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                ).andDo(print());

        // then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(themeDto.id()))
                .andExpect(jsonPath("$.name").value(themeDto.name()));

        verify(themeService).registerTheme(any(ThemeRequest.class));
    }

    @Test
    @DisplayName("요청 시 정보가 빠져있으면 400 상태코드를 응답한다")
    void error_createTheme_whenInvalidRequest() throws Exception {
        // given
        String invalidRequestJson = """
                {
                    "name": "멍구",
                    "description": "설명"
                }
                """;

        // when
        ResultActions result = mockMvc.perform(post("/themes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestJson)
                ).andDo(print());

        // then
        result.andExpect(status().isBadRequest());
    }
}
