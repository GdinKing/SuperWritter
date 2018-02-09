package cn.com.minstone.novel.frag;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.com.minstone.novel.R;
import cn.com.minstone.novel.base.BaseFragment;
import cn.com.minstone.novel.bean.Novel;
import cn.com.minstone.novel.event.DescUpdateEvent;
import cn.com.minstone.novel.util.DisplayUtil;
import me.yokeyword.eventbusactivityscope.EventBusActivityScope;

/***
 * 编辑作品简介/角色设定/故事大纲界面
 *
 * @since 2018/2/2
 * @author king
 */

public class DescEditFragment extends BaseFragment implements View.OnClickListener {


    public static final int TYPE_INTRODUCTION = 0;
    public static final int TYPE_ROLE = 1;
    public static final int TYPE_OUTLINE = 2;

    private EditText etDesc;

    private Novel novel;
    private int type;

    public static DescEditFragment newInstance(Novel novel, int type) {
        DescEditFragment fragment = new DescEditFragment();
        Bundle b = new Bundle();
        b.putSerializable("novel", novel);
        b.putInt("type", type);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_desc_edit;
    }

    @Override
    protected void initView() {
        enableBack();

        etDesc = rootView.findViewById(R.id.et_desc);
    }

    @Override
    protected void initData() {
        type = getArguments().getInt("type", TYPE_INTRODUCTION);
        novel = (Novel) getArguments().getSerializable("novel");
        if(novel==null){
            showToast("数据出错");
            pop();
            return;
        }
        switch (type) {
            case TYPE_INTRODUCTION:
                setTitle("作品简介");
                if (!TextUtils.isEmpty(novel.getIntroduction())) {
                    etDesc.setText(novel.getIntroduction());
                }
                etDesc.setHint("请输入作品简介");
                break;
            case TYPE_ROLE:
                setTitle("角色设定");
                if (!TextUtils.isEmpty(novel.getRole())) {
                    etDesc.setText(novel.getRole());
                }
                etDesc.setHint("请输入角色设定");
                break;
            case TYPE_OUTLINE:
                setTitle("故事大纲");
                if (!TextUtils.isEmpty(novel.getOutline())) {
                    etDesc.setText(novel.getOutline());
                }
                etDesc.setHint("请输入故事大纲");
                break;
        }

        tvRight.setText("保存");
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setOnClickListener(this);
        ivBack.setOnClickListener(this);
    }

    /**
     * 保存
     *
     * @param desc
     */
    public void updateDesc(String desc) {
        showLoading("保存中");
        if (type == TYPE_INTRODUCTION) {
            novel.setIntroduction(desc);
        } else if (type == TYPE_ROLE) {
            novel.setRole(desc);
        } else if (type == TYPE_OUTLINE) {
            novel.setOutline(desc);
        }
        novel.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                hideLoading();
                if (e != null) {
                    showToast("保存失败");
                    return;
                }
                showToast("保存成功");
                pop();
                EventBusActivityScope.getDefault(getActivity()).post(new DescUpdateEvent(novel));
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                DisplayUtil.hideSoftKeyboard(getActivity(), etDesc);
                pop();
                break;
            case R.id.tv_right:
                String desc = etDesc.getText().toString().trim();
                if (TextUtils.isEmpty(desc)) {
                    showToast("请输入内容");
                    return;
                }
                DisplayUtil.hideSoftKeyboard(getActivity(), etDesc);
                updateDesc(desc);
                break;
        }
    }
}
