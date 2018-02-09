package cn.com.minstone.novel.frag;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.com.minstone.novel.R;
import cn.com.minstone.novel.base.BaseFragment;
import cn.com.minstone.novel.util.StringUtil;

/***
 *
 * 重置密码界面
 *
 * @since 2018/1/31
 * @author king
 */

public class ResetPwdFragment extends BaseFragment implements View.OnClickListener {

    private EditText etOld;
    private EditText etNew;
    private EditText etRepeat;
    private TextView tvUpdate;

    public static ResetPwdFragment newInstance() {

        return new ResetPwdFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_reset_pwd;
    }

    @Override
    protected void initView() {
        setTitle("修改密码");
        enableBack();
        ivBack.setOnClickListener(this);
        etOld = rootView.findViewById(R.id.et_old_pwd);
        etNew = rootView.findViewById(R.id.et_new_pwd);
        etRepeat = rootView.findViewById(R.id.et_repeat_pwd);
        tvUpdate = rootView.findViewById(R.id.tv_update);
        tvUpdate.setOnClickListener(this);
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
            case R.id.tv_update:
                validInput();
                break;

        }
    }

    /**
     * 检查输入
     */
    private void validInput() {
        String old = etOld.getText().toString().trim();
        String newPwd = etNew.getText().toString().trim();
        String repeatPwd = etRepeat.getText().toString().trim();

        if (TextUtils.isEmpty(old)) {
            showToast("请输入旧密码");
            return;
        }
        if (TextUtils.isEmpty(newPwd)) {
            showToast("请输入新密码");
            return;
        }
        if (TextUtils.isEmpty(repeatPwd)) {
            showToast("请重复输入新密码");
            return;
        }
        if (!newPwd.equals(repeatPwd)) {
            showToast("两次输入密码不一致");
            return;
        }
        if (StringUtil.isChinese(newPwd)) {
            showToast("密码必须为数字或字母");
            return;
        }
        if (newPwd.trim().length() < 8 || newPwd.trim().length() > 30) {
            showToast("请输入8-30位的密码");
            return;
        }
        resetPwd(old, newPwd);
    }

    /**
     * 修改密码
     * @param oldPwd
     * @param newPwd
     */
    private void resetPwd(String oldPwd, String newPwd) {
        if (BmobUser.getCurrentUser() == null) {
            showToast("您还未登录");
            return;
        }

        BmobUser.updateCurrentUserPassword(oldPwd, newPwd, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    showToast("密码修改成功，可以用新密码进行登录啦");
                    pop();
                } else {
                    if (e.getErrorCode() == 210) {
                        showToast("旧密码错误");
                    } else {
                        showToast("修改失败");
                    }
                }
            }
        });

    }
}
