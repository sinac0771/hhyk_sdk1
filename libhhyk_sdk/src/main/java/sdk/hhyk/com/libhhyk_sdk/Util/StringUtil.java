package sdk.hhyk.com.libhhyk_sdk.Util;

public class StringUtil {

    public static boolean isEmpty(String str) {
        return (str == null || "".equals(str) || "null".equals(str));
    }

    public static int toInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception ex) {
            return 0;
        }
    }

}
