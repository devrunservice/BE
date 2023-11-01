package com.devrun.util;
import org.springframework.http.ResponseCookie;
import javax.servlet.http.HttpServletResponse;


public class CookieUtil {

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
            .path("/")
            .sameSite("None")
            .httpOnly(false)
            .secure(true)
            .maxAge(maxAge)
            .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }
}