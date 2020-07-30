package androidtest.example.com.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author : seven
 * e-mail : clx2216049367@163.com
 * date   : 2019/7/43:07 PM
 * desc   :
 */
public class PhotoUtils {
    public static boolean isMobileNO(String mobileNums) {
        String regExp = "^((13[0-9])|(15[^4])|(166)|(17[0-8])|(18[0-9])|(19[8-9])|(147,145))\\d{8}$";

        Pattern p = Pattern.compile(regExp);

        Matcher m = p.matcher(mobileNums);

        return m.matches();
    }
}
