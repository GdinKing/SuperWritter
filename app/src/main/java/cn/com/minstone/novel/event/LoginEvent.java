package cn.com.minstone.novel.event;

/***
 * 登录/登出 事件，EventBus专用
 *
 * @since 2018/1/30
 * @author king
 */

public class LoginEvent {

    public boolean isLogin;

    public LoginEvent(boolean isLogin) {
        this.isLogin = isLogin;
    }
}
