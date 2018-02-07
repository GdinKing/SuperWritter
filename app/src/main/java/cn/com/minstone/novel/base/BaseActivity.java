package cn.com.minstone.novel.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.com.minstone.novel.R;
import cn.com.minstone.novel.config.AppConfig;
import cn.com.minstone.novel.util.SPUtil;
import me.yokeyword.fragmentation.SupportActivity;

/***
 * 名称：<br>
 * 描述：
 * 最近修改时间：
 * @since 2018/1/29
 * @author king
 */

public abstract class BaseActivity extends SupportActivity {

    protected TextView tvTitle;
    protected TextView tvRight;
    protected ImageView ivBack;
    protected ImageView ivRight;
    protected boolean isNightMode= false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isNightMode = SPUtil.getBoolean(this, AppConfig.NIGHT_MODE_KEY);
        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        setContentView(getLayoutId());

        initTitle();
        initView();
    }

    protected void initTitle() {
        tvTitle = findViewById(R.id.tv_title);
        ivBack = findViewById(R.id.iv_back);
        ivRight = findViewById(R.id.iv_right);
        tvRight = findViewById(R.id.tv_right);
    }

    protected void setTitle(String title) {
        if (tvTitle == null) {
            return;
        }
        if (tvTitle.getVisibility() == View.GONE) {
            tvTitle.setVisibility(View.VISIBLE);
        }
        tvTitle.setText(title);
    }

    protected abstract int getLayoutId();

    protected abstract void initView();
}
