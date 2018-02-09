package cn.com.minstone.novel.frag;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.com.minstone.novel.R;
import cn.com.minstone.novel.base.BaseFragment;
import cn.com.minstone.novel.bean.User;
import cn.com.minstone.novel.event.LoginEvent;
import me.yokeyword.eventbusactivityscope.EventBusActivityScope;

/***
 * 登录界面
 *
 * @since 2018/1/30
 * @author king
 */

public class LoginFragment extends BaseFragment implements View.OnClickListener {

    private EditText etAccount;
    private EditText etPassword;

    private TextView tvReset;
    private TextView tvLogin;
    private TextView tvRegist;

    private String account;
    private String password;

    public static LoginFragment newInstance() {

        return new LoginFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_login;
    }

    @Override
    protected void initView() {
        etAccount = rootView.findViewById(R.id.et_account);
        etPassword = rootView.findViewById(R.id.et_password);
        tvRegist = rootView.findViewById(R.id.tv_regist);
        tvReset = rootView.findViewById(R.id.tv_reset_password);
        tvLogin = rootView.findViewById(R.id.tv_login);
        tvLogin.setOnClickListener(this);
        tvRegist.setOnClickListener(this);
        tvReset.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_login:
                account = etAccount.getText().toString().trim();
                password = etPassword.getText().toString().trim();
                if (TextUtils.isEmpty(account)) {
                    showToast("账号不能为空");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    showToast("密码不能为空");
                    return;
                }
                login();
                break;

            case R.id.tv_reset_password:
                //TODO 忘记密码，未实现
                break;

            case R.id.tv_regist:
                start(RegistFragment.newInstance());
                break;

        }
    }

    private void login() {
        showLoading();
        User user = new User();
        user.setUsername(account);
        user.setPassword(password);
        user.login(new SaveListener<User>() {

            @Override
            public void done(User bmobUser, BmobException e) {
                hideLoading();
                if (e == null) {
                    showToast("登录成功:");
                    EventBusActivityScope.getDefault(getActivity()).post(new LoginEvent(true));
                    pop();
                } else {
                    if (e.getErrorCode() == 101) {
                        showToast("用户名或密码错误");
                    } else {
                        showToast("登录失败");
                    }

                }
            }
        });
    }

    @Override
    public boolean onBackPressedSupport() {
        pop();
        return true;
    }
}
