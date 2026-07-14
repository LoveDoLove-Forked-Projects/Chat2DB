package ai.chat2db.spi.util;

import java.util.Objects;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;


public final class SqlCompletionTemplatePlaceholderCleaner {

    private static final String CDATA_OPEN = "<![CDATA[";
    private static final String CDATA_CLOSE = "]]>";
    private static final Set<String> TEMPLATE_TAGS = Set.of(
            "if",
            "foreach",
            "where",
            "trim",
            "choose",
            "when",
            "otherwise",
            "set",
            "script");

    private SqlCompletionTemplatePlaceholderCleaner() {
    }

    public static String clean(String sql) {
        if (StringUtils.isEmpty(sql)) {
            return "";
        }
        char[] chars = sql.toCharArray();
        int i = 0;
        char quote = 0;
        while (i < chars.length) {
            char ch = chars[i];
            if (quote != 0) {
                if ((quote == '\'' || quote == '"') && ch == '\\') {
                    i += 2;
                    continue;
                }
                if (ch == quote) {
                    if ((quote == '\'' || quote == '"') && i + 1 < chars.length && chars[i + 1] == quote) {
                        i += 2;
                        continue;
                    }
                    quote = 0;
                }
                i++;
                continue;
            }
            if (ch == '\'' || ch == '"' || ch == '`') {
                quote = ch;
                i++;
                continue;
            }
            if (ch == '-' && i + 1 < chars.length && chars[i + 1] == '-') {
                i = skipLineComment(chars, i + 2);
                continue;
            }
            if (matchesAt(chars, i, CDATA_OPEN)) {
                replaceWithSpaces(chars, i, i + CDATA_OPEN.length());
                i += CDATA_OPEN.length();
                continue;
            }
            if (matchesAt(chars, i, CDATA_CLOSE)) {
                replaceWithSpaces(chars, i, i + CDATA_CLOSE.length());
                i += CDATA_CLOSE.length();
                continue;
            }
            if (ch == '[' && i + 1 < chars.length && chars[i + 1] == '[') {
                replaceWithSpaces(chars, i, i + 2);
                i += 2;
                continue;
            }
            if (ch == ']' && i + 1 < chars.length && chars[i + 1] == ']') {
                replaceWithSpaces(chars, i, i + 2);
                i += 2;
                continue;
            }
            if (ch == '/' && i + 1 < chars.length && chars[i + 1] == '*') {
                i = skipBlockComment(chars, i + 2);
                continue;
            }
            if (ch == '<') {
                int end = findClosing(chars, i + 1, '>');
                if (end > i && isTemplateTag(chars, i, end)) {
                    replaceWithSpaces(chars, i, end + 1);
                    i = end + 1;
                    continue;
                }
            }
            if (ch == '#' && (i + 1 >= chars.length || chars[i + 1] != '{')) {
                i = skipLineComment(chars, i + 1);
                continue;
            }
            if ((ch == '#' || ch == '$') && i + 1 < chars.length && chars[i + 1] == '{') {
                int end = findClosing(chars, i + 2, '}');
                if (end > i) {
                    replaceWithExpressionPlaceholder(chars, i, end + 1);
                    i = end + 1;
                    continue;
                }
            }
            if (ch == '{' && i + 1 < chars.length && chars[i + 1] == '{') {
                int end = findDoubleClosingBrace(chars, i + 2);
                if (end > i) {
                    replaceWithExpressionPlaceholder(chars, i, end + 2);
                    i = end + 2;
                    continue;
                }
            }
            if (ch == ':' && i + 1 < chars.length && isIdentifierStart(chars[i + 1])
                    && (i == 0 || chars[i - 1] != ':')) {
                int end = i + 2;
                while (end < chars.length && isIdentifierPart(chars[end])) {
                    end++;
                }
                replaceWithExpressionPlaceholder(chars, i, end);
                i = end;
                continue;
            }
            i++;
        }
        return new String(chars);
    }

    private static boolean matchesAt(char[] chars, int start, String value) {
        if (Objects.isNull(chars) || Objects.isNull(value) || start < 0 || start + value.length() > chars.length) {
            return false;
        }
        for (int i = 0; i < value.length(); i++) {
            if (chars[start + i] != value.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isTemplateTag(char[] chars, int start, int end) {
        if (Objects.isNull(chars) || start < 0 || end >= chars.length || chars[start] != '<') {
            return false;
        }
        int i = start + 1;
        if (i < end && chars[i] == '/') {
            i++;
        }
        if (i >= end || !Character.isLetter(chars[i])) {
            return false;
        }
        StringBuilder name = new StringBuilder();
        while (i <= end && (Character.isLetterOrDigit(chars[i]) || chars[i] == '_' || chars[i] == '-')) {
            name.append(Character.toLowerCase(chars[i]));
            i++;
        }
        if (StringUtils.isBlank(name)) {
            return false;
        }
        return TEMPLATE_TAGS.contains(name.toString());
    }

    private static int skipLineComment(char[] chars, int start) {
        int i = start;
        while (i < chars.length && chars[i] != '\n' && chars[i] != '\r') {
            i++;
        }
        return i;
    }

    private static int skipBlockComment(char[] chars, int start) {
        int i = start;
        while (i + 1 < chars.length) {
            if (chars[i] == '*' && chars[i + 1] == '/') {
                return i + 2;
            }
            i++;
        }
        return chars.length;
    }

    private static int findClosing(char[] chars, int start, char closing) {
        for (int i = start; i < chars.length; i++) {
            if (chars[i] == closing) {
                return i;
            }
        }
        return -1;
    }

    private static int findDoubleClosingBrace(char[] chars, int start) {
        for (int i = start; i + 1 < chars.length; i++) {
            if (chars[i] == '}' && chars[i + 1] == '}') {
                return i;
            }
        }
        return -1;
    }

    private static void replaceWithExpressionPlaceholder(char[] chars, int start, int end) {
        if (start < 0 || start >= chars.length || end <= start) {
            return;
        }
        chars[start] = '0';
        replaceWithSpaces(chars, start + 1, end);
    }

    private static void replaceWithSpaces(char[] chars, int start, int end) {
        for (int i = Math.max(0, start); i < end && i < chars.length; i++) {
            chars[i] = ' ';
        }
    }

    private static boolean isIdentifierStart(char ch) {
        return Character.isLetter(ch) || ch == '_';
    }

    private static boolean isIdentifierPart(char ch) {
        return Character.isLetterOrDigit(ch) || ch == '_' || ch == '$';
    }
}
