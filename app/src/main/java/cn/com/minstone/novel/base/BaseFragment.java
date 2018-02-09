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
 * 名称：<br>
 * 描述：
 * 最近修改时间：
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


    protected void initTitle() {
        tvLeft = rootView.findViewById(R.id.tv_left);
        tvTitle = rootView.findViewById(R.id.tv_title);
        ivBack = rootView.findViewById(R.id.iv_back);
        ivRight = rootView.findViewById(R.id.iv_right);
        tvRightSecond = rootView.findViewById(R.id.tv_right_second);
        tvRight = rootView.findViewById(R.id.tv_right);
        tvSubTitle = rootView.findViewById(R.id.tv_sub_title);
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

    protected void setSubTitle(String title) {
        if (tvSubTitle == null) {
            return;
        }
        if (tvSubTitle.getVisibility() == View.GONE) {
            tvSubTitle.setVisibility(View.VISIBLE);
        }
        tvSubTitle.setText(title);
    }

    protected void enableBack() {
        if (ivBack == null) {
            return;
        }
        if (ivBack.getVisibility() == View.GONE) {
            ivBack.setVisibility(View.VISIBLE);
            ivBack.setImageResource(isNightMode ? R.drawable.ic_back_dark : R.drawable.ic_back);
        }
    }

    protected abstract int getLayoutId();

    protected abstract void initView();

    protected abstract void initData();

    protected void showLoading() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity(),R.style.ProgressDialog);
            progressDialog.setMessage("加载中");
        }
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    protected void showLoading(String text) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity(),R.style.ProgressDialog);
            progressDialog.setMessage(text);
        }
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    protected void hideLoading() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    protected void showToast(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        }
    }
}
