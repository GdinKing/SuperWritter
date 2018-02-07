package cn.com.minstone.novel.util;

/***
 * 名称：<br>
 * 描述：
 * 最近修改时间：
 * @since 2018/2/2
 * @author king
 */

public class StringUtil {

    public static int getRandom(int count) {
        return (int) Math.round(Math.random() * (count));
    }

    private static String STR = "abcdefghijklmnopqrstuvwxyz0123456789";

    public static String getRandomString(int length) {
        StringBuffer sb = new StringBuffer();
        int len = STR.length();
        for (int i = 0; i < length; i++) {
            sb.append(STR.charAt(getRandom(len - 1)));
        }
        return sb.toString();
    }

    // 判断一个字符是否是中文
    public static boolean isChinese(char c) {
        return c >= 0x4E00 &&  c <= 0x9FA5;// 根据字节码判断
    }
    // 判断一个字符串是否含有中文
    public static boolean isChinese(String str) {
        if (str == null) return false;
        for (char c : str.toCharArray()) {
            if (isChinese(c)) return true;// 有一个中文字符就返回
        }
        return false;
    }
}
