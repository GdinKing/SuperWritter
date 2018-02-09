package cn.com.minstone.novel.frag;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.mylhyl.circledialog.CircleDialog;

import cn.bmob.v3.BmobUser;
import cn.com.minstone.novel.R;
import cn.com.minstone.novel.base.BaseFragment;
import cn.com.minstone.novel.bean.User;
import cn.com.minstone.novel.event.LoginEvent;
import me.yokeyword.eventbusactivityscope.EventBusActivityScope;

/***
 * 设置界面
 *
 * @since 2018/1/31
 * @author king
 */

public class SettingFragment extends BaseFragment implements View.OnClickListener {

    private TextView tvLogout;
    private TextView tvAbout;
    private TextView tvReset;

    public static SettingFragment newInstance() {

        return new SettingFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_setting;
    }

    @Override
    protected void initView() {
        setTitle("设置");
        enableBack();
        ivBack.setOnClickListener(this);
        tvAbout = rootView.findViewById(R.id.tv_about);
        tvReset = rootView.findViewById(R.id.tv_reset_pwd);
        tvLogout = rootView.findViewById(R.id.tv_logout);
        User user = BmobUser.getCurrentUser(User.class);
        tvLogout.setVisibility(user == null ? View.GONE : View.VISIBLE);
        tvLogout.setOnClickListener(this);
        tvAbout.setOnClickListener(this);
        tvReset.setOnClickListener(this);
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
            case R.id.tv_reset_pwd:
                if(BmobUser.getCurrentUser()==null){
                    showToast("登录后再操作");
                    return;
                }
                start(ResetPwdFragment.newInstance());
                break;
            case R.id.tv_logout:
                showLogoutDialog();
                break;
        }
    }

    /**
     * 退出登录对话框
     */
    private void showLogoutDialog() {

        new CircleDialog.Builder(getActivity())
                .setTitle("提示")
                .setTitleColor(ContextCompat.getColor(getActivity(), R.color.blue))
                .setText("是否退出登录?")
                .setNegative("取消", null)
                .setPositive("确定", view -> {
                    BmobUser.logOut();
                    showToast("退出成功");
                    EventBusActivityScope.getDefault(getActivity()).post(new LoginEvent(false));
                    pop();
                }).show();

    }

    @Override
    public boolean onBackPressedSupport() {
        pop();
        return true;
    }
}
