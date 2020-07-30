package androidtest.example.com.utils;

/**
 * author : seven
 * e-mail : clx2216049367@163.com
 * date   : 2019/7/1811:37 AM
 * desc   :规则验证
 */
public class RegexUtils {
    /**
     * 验证输入的身份证号是否合法
     */
    public static boolean isLegalId(String id){
        if (id.toUpperCase().matches("(^\\d{15}$)|(^\\d{17}([0-9]|X)$)")){
            return true;
        }else {
            return false;
        }
    }
}
