package cn.com.minstone.novel.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.com.minstone.novel.R;
import cn.com.minstone.novel.config.AppConfig;
import cn.com.minstone.novel.util.SPUtil;
import me.yokeyword.fragmentation.SupportFragment;

/***
 * 基础的Fragment，封装一些通用的方法，所有Fragment继承自这个
 *
 * @since 2018/1/29
 * @author king
 */
public abstract class BaseFragment extends SupportFragment {

    protected TextView tvTitle;
    protected TextView tvRight;
    protected TextView tvRightSecond;
    protected TextView tvLeft;
    protected TextView tvSubTitle;
    protected ImageView ivBack;
    protected ImageView ivRight;

    protected View rootView;

    protected ProgressDialog progressDialog;

    /**
     * 是否夜间模式
     */
    protected boolean isNightMode = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isNightMode = SPUtil.getBoolean(getActivity(), AppConfig.NIGHT_MODE_KEY);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(getLayoutId(), container, false);
        initTitle();
        initView();
        initData();
        return rootView;
    }


    /**
     * 初始化标题栏视图
     */
    protected void initTitle() {
        tvLeft = rootView.findViewById(R.id.tv_left);
        tvTitle = rootView.findViewById(R.id.tv_title);
        ivBack = rootView.findViewById(R.id.iv_back);
        ivRight = rootView.findViewById(R.id.iv_right);
        tvRightSecond = rootView.findViewById(R.id.tv_right_second);
        tvRight = rootView.findViewById(R.id.tv_right);
        tvSubTitle = rootView.findViewById(R.id.tv_sub_title);
    }

    /**
     * 设置标题
     * @param title
     */
    protected void setTitle(String title) {
        if (tvTitle == null) {
            return;
        }
        if (tvTitle.getVisibility() == View.GONE) {
            tvTitle.setVisibility(View.VISIBLE);
        }
        tvTitle.setText(title);
    }

    /**
     * 设置副标题
     * @param title
     */
    protected void setSubTitle(String title) {
        if (tvSubTitle == null) {
            return;
        }
        if (tvSubTitle.getVisibility() == View.GONE) {
            tvSubTitle.setVisibility(View.VISIBLE);
        }
        tvSubTitle.setText(title);
    }

    /**
     * 显示返回按钮
     */
    protected void enableBack() {
        if (ivBack == null) {
            return;
        }
        if (ivBack.getVisibility() == View.GONE) {
            ivBack.setVisibility(View.VISIBLE);
            ivBack.setImageResource(isNightMode ? R.drawable.ic_back_dark : R.drawable.ic_back);
        }
    }

    /**
     * 获取布局id，由子类实现
     * @return
     */
    protected abstract int getLayoutId();
    /**
     * 初始化布局，由子类实现
     */
    protected abstract void initView();

    /**
     * 初始化数据，由子类实现
     */
    protected abstract void initData();

    /**
     * 显示加载对话框
     */
    protected void showLoading() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity(),R.style.ProgressDialog);
            progressDialog.setMessage("加载中");
        }
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }
    /**
     * 显示加载对话框
     *
     * @param text 提示文本
     */
    protected void showLoading(String text) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity(),R.style.ProgressDialog);
            progressDialog.setMessage(text);
        }
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    /**
     * 隐藏加载中对话框
     */
    protected void hideLoading() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    /**
     * Toast提示
     * @param msg
     */
    protected void showToast(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        }
    }
}
