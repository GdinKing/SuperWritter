package cn.com.minstone.novel.frag;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.com.minstone.novel.R;
import cn.com.minstone.novel.base.BaseFragment;
import cn.com.minstone.novel.bean.Chapter;
import cn.com.minstone.novel.bean.Novel;
import cn.com.minstone.novel.event.ChapterUpdateEvent;
import cn.com.minstone.novel.event.DescUpdateEvent;
import cn.com.minstone.novel.util.ImageUtil;
import me.yokeyword.eventbusactivityscope.EventBusActivityScope;

/***
 * 名称：<br>
 * 描述：
 * 最近修改时间：
 * @since 2018/1/31
 * @author king
 */

public class NovelDescFragment extends BaseFragment implements View.OnClickListener {

    public static NovelDescFragment newInstance(Novel novel) {
        NovelDescFragment fragment = new NovelDescFragment();
        Bundle b = new Bundle();
        b.putSerializable("novel", novel);
        fragment.setArguments(b);
        return fragment;
    }

    private TextView btnIntro;
    private TextView btnRole;
    private TextView btnOutline;
    private TextView tvIntro;
    private TextView tvRole;
    private TextView tvOutline;
    private TextView tvCover;
    private TextView tvAuthor;
    private TextView tvType;
    private TextView tvCount;
    private ImageView ivCover;

    private Novel novel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_desc;
    }

    @Override
    protected void initView() {

        novel = (Novel) getArguments().getSerializable("novel");

        btnIntro = rootView.findViewById(R.id.btn_intro);
        btnRole = rootView.findViewById(R.id.btn_role);
        btnOutline = rootView.findViewById(R.id.btn_outline);
        tvIntro = rootView.findViewById(R.id.tv_intro);
        tvRole = rootView.findViewById(R.id.tv_role);
        tvOutline = rootView.findViewById(R.id.tv_outline);
        tvCover = rootView.findViewById(R.id.tv_cover);
        tvType = rootView.findViewById(R.id.tv_type);
        tvAuthor = rootView.findViewById(R.id.tv_author);
        tvCount = rootView.findViewById(R.id.tv_count);
        ivCover = rootView.findViewById(R.id.iv_cover);

        btnIntro.setOnClickListener(this);
        btnRole.setOnClickListener(this);
        btnOutline.setOnClickListener(this);
        tvIntro.setOnClickListener(this);
        tvRole.setOnClickListener(this);
        tvOutline.setOnClickListener(this);
        EventBusActivityScope.getDefault(getActivity()).register(this);
    }

    @Override
    protected void initData() {
        if (!TextUtils.isEmpty(novel.getIntroduction())) {
            tvIntro.setText(novel.getIntroduction());
        }
        if (!TextUtils.isEmpty(novel.getRole())) {
            tvRole.setText(novel.getRole());
        }
        if (!TextUtils.isEmpty(novel.getOutline())) {
            tvOutline.setText(novel.getOutline());
        }
        if (novel.getCover() != null) {
            ivCover.setVisibility(View.VISIBLE);
            tvCover.setVisibility(View.GONE);
            ImageUtil.showImage(getActivity(), novel.getCover().getFileUrl(), ivCover);
        } else {
            ivCover.setVisibility(View.GONE);
            tvCover.setVisibility(View.VISIBLE);
            tvCover.setText(novel.getName());
        }
        tvAuthor.setText(novel.getAuthor().getNick());
        tvType.setText(novel.getType());
        loadCount();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_intro:
                int introVisible = tvIntro.getVisibility();
                if (introVisible == View.GONE) {
                    btnIntro.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.icon_down_grey, 0);
                } else {
                    btnIntro.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.icon_right_grey, 0);
                }
                tvIntro.setVisibility(introVisible == View.GONE ? View.VISIBLE : View.GONE);
                break;
            case R.id.btn_role:
                int roleVisible = tvRole.getVisibility();
                if (roleVisible == View.GONE) {
                    btnRole.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.icon_down_grey, 0);
                } else {
                    btnRole.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.icon_right_grey, 0);
                }
                tvRole.setVisibility(roleVisible == View.GONE ? View.VISIBLE : View.GONE);
                break;
            case R.id.btn_outline:
                int outlineVisible = tvOutline.getVisibility();
                if (outlineVisible == View.GONE) {
                    btnOutline.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.icon_down_grey, 0);
                } else {
                    btnOutline.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.icon_right_grey, 0);
                }
                tvOutline.setVisibility(outlineVisible == View.GONE ? View.VISIBLE : View.GONE);
                break;
            case R.id.tv_intro:
                ((NovelDetailFragment) getParentFragment()).openFragment(DescEditFragment.newInstance(novel, DescEditFragment.TYPE_INTRODUCTION));
                break;
            case R.id.tv_role:
                ((NovelDetailFragment) getParentFragment()).openFragment(DescEditFragment.newInstance(novel, DescEditFragment.TYPE_ROLE));
                break;
            case R.id.tv_outline:
                ((NovelDetailFragment) getParentFragment()).openFragment(DescEditFragment.newInstance(novel, DescEditFragment.TYPE_OUTLINE));
                break;
        }
    }


    //TODO BmobSDk有bug，统计不了字数
    private void loadCount() {
        BmobQuery<Chapter> query = new BmobQuery<>();
        query.sum(new String[]{"count"});
        query.addWhereEqualTo("novel", new BmobPointer(novel));
        query.findStatistics(Chapter.class, new QueryListener<JSONArray>() {

            @Override
            public void done(JSONArray ary, BmobException e) {
                if (e != null) {
                    Log.e("king", "失败：" + e.getMessage() + "," + e.getErrorCode());
                    showToast("获取字数失败");
                    return;
                }
                if (ary != null) {//
                    JSONObject obj = ary.optJSONObject(0);
                    if (obj != null) {
                        int sum = obj.optInt("_sumCount");
                        tvCount.setText(sum + "字");
                    }
                } else {
                    tvCount.setText("0字");
                }

            }

        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DescUpdateEvent event) {
        if (event != null) {
            novel = event.novel;
            initData();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ChapterUpdateEvent event) {
        if (event != null) {
            loadCount();
        }
    }

    @Override
    public void onDestroyView() {
        EventBusActivityScope.getDefault(getActivity()).unregister(this);
        super.onDestroyView();
    }
}
