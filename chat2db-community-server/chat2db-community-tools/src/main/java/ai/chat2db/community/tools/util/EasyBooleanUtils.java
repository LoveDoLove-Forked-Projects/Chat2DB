package ai.chat2db.community.tools.util;


public class EasyBooleanUtils {


    public static boolean equals(Boolean b1, Boolean b2, Boolean defaultValue) {
        if (b1 == b2) {
            return true;
        }
        if (b1 == null) {
            b1 = defaultValue;
        }
        if (b2 == null) {
            b2 = defaultValue;
        }
        return b1 == b2;
    }

}
