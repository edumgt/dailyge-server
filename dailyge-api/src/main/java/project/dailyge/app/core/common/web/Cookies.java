package project.dailyge.app.core.common.web;

import jakarta.servlet.http.Cookie;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

public final class Cookies {

    private final Map<String, Cookie> cookieMap = new HashMap<>();

    public Cookies(final Cookie... cookies) {
        if (ObjectUtils.isEmpty(cookies)) {
            return;
        }
        for (final Cookie cookie : cookies) {
            cookieMap.put(cookie.getName(), cookie);
        }
    }

    public boolean isLoggedIn() {
        final String loggedIn = getValueByKey("logged_in");
        return loggedIn != null && loggedIn.equals("yes");
    }

    public String getValueByKey(final String key) {
        final Cookie cookie = cookieMap.get(key);
        if (cookie == null) {
            return null;
        }
        final String value = cookie.getValue();
        return value == null ? "" : value;
    }
}
