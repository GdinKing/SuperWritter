package cn.com.minstone.novel.frag;

import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.bmob.v3.BmobUser;
import cn.com.minstone.novel.R;
import cn.com.minstone.novel.base.BaseFragment;
import cn.com.minstone.novel.bean.User;
import cn.com.minstone.novel.config.AppConfig;
import cn.com.minstone.novel.event.LoginEvent;
import cn.com.minstone.novel.util.SPUtil;
import de.hdodenhof.circleimageview.CircleImageView;
import it.beppi.tristatetogglebutton_library.TriStateToggleButton;
import me.yokeyword.eventbusactivityscope.EventBusActivityScope;

/***
 * 我的
 *
 * @since 2018/1/29
 * @author king
 */

public class MyFragment extends BaseFragment implements View.OnClickListener {

    private TriStateToggleButton toggleButton;
    private CircleImageView circleImageView;

    private TextView tvName;
    private TextView tvSetting;
    private TextView tvPublish;

    private User user;

    public static MyFragment newInstance() {

        return new MyFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my;
    }

    @Override
    protected void initView() {
        setTitle("我的");
        EventBusActivityScope.getDefault(getActivity()).register(this);
        boolean nightMode = SPUtil.getBoolean(getActivity(), AppConfig.NIGHT_MODE_KEY);
        circleImageView = rootView.findViewById(R.id.iv_avatar);
        tvName = rootView.findViewById(R.id.tv_nickname);
        tvPublish = rootView.findViewById(R.id.tv_publish);
        tvSetting = rootView.findViewById(R.id.tv_settings);
        tvPublish.setOnClickListener(this);
        tvSetting.setOnClickListener(this);
        toggleButton = rootView.findViewById(R.id.toggle_dark);
        if (nightMode) {
            toggleButton.setToggleOn();
        } else {
            toggleButton.setToggleOff();
        }
        toggleButton.setOnToggleChanged((toggleStatus, b, i) -> {//夜间模式切换
            if (toggleStatus.equals(TriStateToggleButton.ToggleStatus.on)) {
                SPUtil.setBoolean(getActivity(), AppConfig.NIGHT_MODE_KEY, true);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                SPUtil.setBoolean(getActivity(), AppConfig.NIGHT_MODE_KEY,false);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            getActivity().recreate();//需要重启Activity
        });
        tvName.setOnClickListener(this);
        circleImageView.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        user = BmobUser.getCurrentUser(User.class);

        if (user == null) {
            Glide.with(getActivity()).load(R.drawable.ic_default_head).into(circleImageView);
            tvName.setText("登录/注册");
            return;
        }
        if (user.getAvatar() != null) {
            Glide.with(getActivity()).load(user.getAvatar().getFileUrl()).into(circleImageView);
        }
        tvName.setText(TextUtils.isEmpty(user.getNick()) ? user.getUsername() : user.getNick());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_nickname:
            case R.id.iv_avatar:
                if (BmobUser.getCurrentUser() == null) {
                    ((MainFragment) getParentFragment()).openFragment(LoginFragment.newInstance());
                } else {
                    ((MainFragment) getParentFragment()).openFragment(UserInfoFragment.newInstance());
                }
                break;
            case R.id.tv_settings:
                ((MainFragment) getParentFragment()).openFragment(SettingFragment.newInstance());
                break;

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LoginEvent event) {
        if (event != null && event.isLogin) {
            initData();
        } else {
            Glide.with(getActivity()).load(R.drawable.ic_default_head).into(circleImageView);
            tvName.setText("登录/注册");
        }
    }

    @Override
    public void onDestroy() {
        EventBusActivityScope.getDefault(getActivity()).unregister(this);
        super.onDestroy();
    }
}
