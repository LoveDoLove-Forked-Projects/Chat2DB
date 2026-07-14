package ai.chat2db.community.web.api.util;

import java.util.function.BiFunction;
import java.util.function.Function;


@SuppressWarnings("WeakerAccess")
public class StringUtils {

    private static final String EMPTY_STR = "null";


    private static final BiFunction<String, Function<Integer, Integer>, String> FIRST_CHAR_HANDLER_FUN = (str, firstCharFun) -> {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }

        final int firstCodepoint = str.codePointAt(0);
        final int newCodePoint = firstCharFun.apply(firstCodepoint);
        if (firstCodepoint == newCodePoint) {
            return str;
        }
        final int[] newCodePoints = new int[strLen];
        int outOffset = 0;
        newCodePoints[outOffset++] = newCodePoint;
        for (int inOffset = Character.charCount(firstCodepoint); inOffset < strLen; ) {
            final int codepoint = str.codePointAt(inOffset);
            newCodePoints[outOffset++] = codepoint;
            inOffset += Character.charCount(codepoint);
        }
        return new String(newCodePoints, 0, outOffset);
    };

    public static String isNullOrEmpty(String str) {
        if (StringUtils.isEmpty(str) || EMPTY_STR.equals(str)) {
            return "";
        }
        return str;
    }

    public static String isNull(String str) {
        if (StringUtils.isEmpty(str) || EMPTY_STR.equals(str)) {
            return "--";
        }
        return str;
    }

    public static String isNullForHtml(String str) {
        if (StringUtils.isEmpty(str) || EMPTY_STR.equals(str)) {
            return "<br>";
        }
        return str;
    }


    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }


    public static String capitalize(final String str) {
        return FIRST_CHAR_HANDLER_FUN.apply(str, Character::toTitleCase);
    }


    public static String uncapitalize(final String str) {
        return FIRST_CHAR_HANDLER_FUN.apply(str, Character::toLowerCase);
    }
}
