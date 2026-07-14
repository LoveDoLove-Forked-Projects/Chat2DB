package ai.chat2db.community.tools.model;

import jakarta.servlet.http.Cookie;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HeaderAndCookies {

    private Map<String,String> headers;

    private Cookie[] cookies;
}
