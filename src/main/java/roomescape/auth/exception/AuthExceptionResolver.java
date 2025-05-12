package roomescape.auth.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import roomescape.global.exception.ErrorCode;

@Component
public class AuthExceptionResolver implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response,
                                         Object handler, Exception ex) {

        if (!(ex instanceof AuthorizationException ae)) {
            return null;
        }

        ErrorCode code = ae.getErrorCode();

        try {
            handleAuthorizationError(response, code);
        } catch (IOException e) {
            System.out.println("IOException: " + e);
            throw new RuntimeException(e);
        }

        return new ModelAndView();
    }

    private void handleAuthorizationError(HttpServletResponse response, ErrorCode code) throws IOException {
        if (code == AuthErrorCode.FORBIDDEN_ACCESS) {
            response.sendRedirect("/error/403.html");
            return;
        }
        redirectWithAlert(response, code.getMessage(), "/login");
    }

    private void redirectWithAlert(HttpServletResponse response, String message, String redirectUrl)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        String html = "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'><title>Redirecting...</title></head>" +
                "<body>" +
                "<p>Redirecting...</p>" + // 렌더링 요소 추가
                "<script>" +
                "window.onload = function() {" +
                "  setTimeout(function() {" +
                "    alert('" + escape(message) + "');" +
                "    window.location.href = '" + redirectUrl + "';" +
                "  }, 100);" +
                "};" +
                "</script>" +
                "</body></html>";

        response.getWriter().write(html);
    }

    private String escape(String input) {
        return input.replace("'", "\\'").replace("\"", "\\\"");
    }
}
