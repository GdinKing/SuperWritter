package cn.com.minstone.novel.event;

/***
 * 名称：<br>
 * 描述：
 * 最近修改时间：
 * @since 2018/1/30
 * @author king
 */

public class LoginEvent {
    public boolean isLogin;

    public LoginEvent(boolean isLogin) {
        this.isLogin = isLogin;
    }
}
