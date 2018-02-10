package cn.com.minstone.novel.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/***
 * 字符串工具类
 *
 * @since 2018/2/2
 * @author king
 */

public class StringUtil {

    public static int getRandom(int count) {
        return (int) Math.round(Math.random() * (count));
    }

    private static String STR = "abcdefghijklmnopqrstuvwxyz0123456789";

    /**
     * 生成随机字符
     *
     * @param length
     * @return
     */
    public static String getRandomString(int length) {
        StringBuffer sb = new StringBuffer();
        int len = STR.length();
        for (int i = 0; i < length; i++) {
            sb.append(STR.charAt(getRandom(len - 1)));
        }
        return sb.toString();
    }

    /**
     * 判断一个字符是否是中文
     *
     * @param c
     * @return
     */
    public static boolean isChinese(char c) {
        return c >= 0x4E00 && c <= 0x9FA5;// 根据字节码判断
    }

    /**
     * 判断一个字符串是否含有中文
     *
     * @param str
     * @return
     */
    public static boolean isChinese(String str) {
        if (str == null) return false;
        for (char c : str.toCharArray()) {
            if (isChinese(c)) return true;// 有一个中文字符就返回
        }
        return false;
    }

    /**
     * 格式化日期
     * @param dateStr
     * @return
     */
    public static String formatDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date d = sdf.parse(dateStr);
            return sdf.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateStr;
    }
}
