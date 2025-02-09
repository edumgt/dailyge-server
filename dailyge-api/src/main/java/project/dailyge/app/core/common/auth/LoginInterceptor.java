package project.dailyge.app.core.common.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static java.nio.charset.StandardCharsets.UTF_8;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import static project.dailyge.app.codeandmessage.CommonCodeAndMessage.BAD_REQUEST;
import project.dailyge.app.common.auth.TokenProvider;
import static project.dailyge.app.common.utils.CookieUtils.createCookie;
import project.dailyge.app.core.common.web.Cookies;
import project.dailyge.app.core.user.external.oauth.TokenManager;
import project.dailyge.core.cache.user.UserCache;
import project.dailyge.core.cache.user.UserCacheReadService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    private final Logger log = LoggerFactory.getLogger(LoginInterceptor.class);

    private final UserCacheReadService userCacheReadService;
    private final TokenProvider tokenProvider;
    private final TokenManager tokenManager;
    private final ObjectMapper objectMapper;

    public LoginInterceptor(
        final UserCacheReadService userCacheReadService,
        final TokenProvider tokenProvider,
        final TokenManager tokenManager,
        final ObjectMapper objectMapper
    ) {
        this.userCacheReadService = userCacheReadService;
        this.tokenProvider = tokenProvider;
        this.tokenManager = tokenManager;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(
        final HttpServletRequest request,
        final HttpServletResponse response,
        final Object handler
    ) {
        try {
            final Cookies cookies = new Cookies(request.getCookies());
            if (!cookies.isLoggedIn()) {
                return true;
            }
            final String accessToken = cookies.getValueByKey("dg_sess");
            final Long userId = tokenProvider.getUserId(accessToken);
            if (!userCacheReadService.existsById(userId)) {
                return true;
            }
            setLoggedInResponse(request, response, accessToken);
            return false;
        } catch (ExpiredJwtException ex) {
            return refreshToken(request, response, ex);
        } catch (Exception ex) {
            log.error("abnormal token error", ex);
            return true;
        }
    }

    private boolean refreshToken(
        final HttpServletRequest request,
        final HttpServletResponse response,
        final ExpiredJwtException expiredJwtException
    ) {
        try {
            final Claims claims = expiredJwtException.getClaims();
            final String encryptedUserId = claims.get("id", String.class);
            final Long userId = tokenProvider.decryptUserId(encryptedUserId);
            final UserCache userCache = userCacheReadService.findById(userId);
            if (userCache == null) {
                return true;
            }
            final Cookies cookies = new Cookies(request.getCookies());
            final String refreshToken = cookies.getValueByKey("dg_res");
            if (!tokenManager.getRefreshToken(userId).equals(refreshToken)) {
                return true;
            }

            final DailygeToken token = tokenProvider.createToken(userCache.getId());
            setLoggedInResponse(request, response, token.accessToken());
            return false;
        } catch (Exception ex) {
            log.error("refresh token error", ex);
            return true;
        }
    }

    private void setLoggedInResponse(
        final HttpServletRequest request,
        final HttpServletResponse response,
        final String accessToken
    ) throws IOException {
        final Map<String, String> bodyMap = new HashMap<>();
        final String referer = request.getHeader("referer");
        bodyMap.put("url", referer == null ? "/" : referer);
        response.addCookie(createCookie("dg_sess", accessToken, "/", 900));
        response.setStatus(BAD_REQUEST.code());
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(UTF_8.name());
        objectMapper.writeValue(response.getWriter(), bodyMap);
    }
}
