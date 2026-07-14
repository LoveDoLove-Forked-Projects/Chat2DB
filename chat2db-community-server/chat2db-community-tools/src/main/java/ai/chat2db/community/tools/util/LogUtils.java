package ai.chat2db.community.tools.util;

import java.util.Objects;
import java.util.regex.Pattern;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.net.NetUtil;
import org.apache.commons.lang3.StringUtils;
import org.zalando.logbook.HttpHeaders;
import org.zalando.logbook.HttpRequest;


public class LogUtils {

    private static final ThreadLocal<String> TRACE_ID_THREAD_LOCAL = new ThreadLocal<>();


    private static final String[] CLIENT_IP_HEADERS = {"X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP",
        "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};


    public static final int MAX_LOG_LENGTH = 20000;

    public static final String TRACE_ID = "TRACE_ID";

    public static final String CLIENT_IP = "CLIENT_IP";

    public static final String USER_ID = "USER_ID";

    public static final String TOKEN = "TOKEN";

    public static final String TRACE_ID_HEADER = "X-Chat2DB-Trace-Id";


    private static final Pattern LINE_FEED_PATTERN = Pattern.compile("\r|\n");


    public static String maskString(String input) {
        if (StringUtils.isBlank(input)) {
            return input;
        }

        StringBuilder maskedString = new StringBuilder(input);
        for (int i = 0; i < input.length(); i += 4) {
            maskedString.setCharAt(i, '*');
        }
        return maskedString.toString();
    }


    public static String removeCrlf(String log) {
        if (Objects.isNull(log)) {
            return null;
        }
        return LINE_FEED_PATTERN.matcher(log).replaceAll("");
    }


    public static String cutLog(Object log) {
        if (Objects.isNull(log)) {
            return null;
        }
        return EasyStringUtils.limitString(removeCrlf(log.toString()), MAX_LOG_LENGTH);
    }


    public static String generateTraceId() {
        String traceId = UUID.fastUUID().toString().replaceAll("-", "");
        TRACE_ID_THREAD_LOCAL.set(traceId);
        return traceId;
    }

    public static void setTraceId(String traceId) {
        TRACE_ID_THREAD_LOCAL.set(traceId);
    }


    public static String getTraceId() {
        return TRACE_ID_THREAD_LOCAL.get();
    }


    public static void removeTraceId() {
        TRACE_ID_THREAD_LOCAL.remove();
    }


    public static String getClientIp(HttpRequest request) {
        HttpHeaders httpHeaders = request.getHeaders();
        String ip;
        for (String header : CLIENT_IP_HEADERS) {
            ip = httpHeaders.getFirst(header);
            if (!NetUtil.isUnknown(ip)) {
                return NetUtil.getMultistageReverseProxyIp(ip);
            }
        }
        ip = request.getRemote();
        return NetUtil.getMultistageReverseProxyIp(ip);
    }
}
