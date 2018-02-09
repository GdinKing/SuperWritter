package cn.com.minstone.novel.frag;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.com.minstone.novel.R;
import cn.com.minstone.novel.base.BaseFragment;
import cn.com.minstone.novel.bean.User;
import cn.com.minstone.novel.util.StringUtil;

/***
 * 注册界面
 *
 * @since 2018/1/30
 * @author king
 */

public class RegistFragment extends BaseFragment implements View.OnClickListener {

    private EditText etAccount;
    private EditText etPassword;

    private EditText etRepeat;

    private TextView tvRegist;

    private String account;
    private String password;

    public static RegistFragment newInstance() {

        return new RegistFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_regist;
    }

    @Override
    protected void initView() {
        setTitle("用户注册");
        enableBack();
        ivBack.setOnClickListener(this);
        etAccount = rootView.findViewById(R.id.et_account);
        etPassword = rootView.findViewById(R.id.et_password);
        tvRegist = rootView.findViewById(R.id.tv_regist);
        etRepeat = rootView.findViewById(R.id.et_repeat_password);
        tvRegist.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                pop();
                break;


            case R.id.tv_regist:
                account = etAccount.getText().toString().trim();
                password = etPassword.getText().toString().trim();
                String repeat = etRepeat.getText().toString().trim();
                if (TextUtils.isEmpty(account)) {
                    showToast("账号不能为空");
                    return;
                }
                if (account.trim().length() < 8 || account.trim().length() > 20) {
                    showToast("请输入8-20位的账号");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    showToast("密码不能为空");
                    return;
                }
                if (StringUtil.isChinese(password)) {
                    showToast("密码必须为数字或字母");
                    return;
                }
                if (TextUtils.isEmpty(repeat)) {
                    showToast("请重复输入密码");
                    return;
                }
                if (!password.equals(repeat)) {
                    showToast("两次输入密码不一致");
                    return;
                }
                if (password.trim().length() < 8 || password.trim().length() > 30) {
                    showToast("请输入8-30位的密码");
                    return;
                }

                registUser();
                break;


        }
    }

    /**
     * 注册用户
     */
    private void registUser() {
        showLoading();
        User user = new User();
        user.setUsername(account);
        user.setPassword(password);
        user.setNick("Writer_" + StringUtil.getRandomString(5));
        user.signUp(new SaveListener<User>() {
            @Override
            public void done(User s, BmobException e) {
                hideLoading();
                if (e == null) {
                    showToast("注册成功");
                    pop();
                } else {
                    Log.e("king", e.getErrorCode() + "===" + e.getMessage(), e);
                    showToast(e.getMessage());
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
