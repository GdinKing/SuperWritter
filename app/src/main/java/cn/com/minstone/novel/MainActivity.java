package cn.com.minstone.novel;

import android.widget.Toast;

import cn.com.minstone.novel.base.BaseActivity;
import cn.com.minstone.novel.frag.MainFragment;
import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

public class MainActivity extends BaseActivity {

    private long lastTime = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        if (findFragment(MainFragment.class) == null) {
            loadRootFragment(R.id.fl_container, MainFragment.newInstance());
        }
    }

    @Override
    public void onBackPressedSupport() {
        if (System.currentTimeMillis() - lastTime > 2000) {
            lastTime = System.currentTimeMillis();
            Toast.makeText(this, "再按一次退出应用",Toast.LENGTH_SHORT).show();
        } else {
            finish();
            System.exit(0);
        }
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        // 设置横向(和安卓4.x动画相同)
        return new DefaultHorizontalAnimator();
    }
}
