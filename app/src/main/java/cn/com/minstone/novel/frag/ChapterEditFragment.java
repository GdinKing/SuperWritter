package cn.com.minstone.novel.frag;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.com.minstone.novel.R;
import cn.com.minstone.novel.base.BaseFragment;
import cn.com.minstone.novel.bean.Chapter;
import cn.com.minstone.novel.event.ChapterUpdateEvent;
import cn.com.minstone.novel.util.DisplayUtil;
import cn.com.minstone.novel.view.MyEditText;
import me.yokeyword.eventbusactivityscope.EventBusActivityScope;

/***
 * 名称：<br>
 * 描述：
 * 最近修改时间：
 * @since 2018/2/1
 * @author king
 */

public class ChapterEditFragment extends BaseFragment implements View.OnClickListener {

    public static ChapterEditFragment newInstance(Chapter chapter) {
        ChapterEditFragment fragment = new ChapterEditFragment();
        Bundle b = new Bundle();
        b.putSerializable("chapter", chapter);
        fragment.setArguments(b);
        return fragment;
    }

    private MyEditText etContent;
    private Chapter chapter;
    private String editContent;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_edit;
    }

    @Override
    protected void initView() {
        chapter = (Chapter) getArguments().getSerializable("chapter");
        if (chapter == null) {
            pop();
            return;
        }
        setTitle(chapter.getName());
        enableBack();
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText("保存");
        tvRight.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        etContent = rootView.findViewById(R.id.et_content);
        if (TextUtils.isEmpty(chapter.getContent())) {
            etContent.setText("\t\t\t\t\t");
            editContent = "\\t\\t\\t\t\\t";
        } else {
            editContent = chapter.getContent();
            etContent.setText(chapter.getContent());
        }
        etContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                editContent = editable.toString();
                setSubTitle(editable.toString().replaceAll("\\s*", "").length() + "字");
            }
        });
        etContent.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                String content = "\n\t\t\t\t\t";
                editContent += "\\n\\t\\t\\t\\t\\t";
                etContent.append(content);
                return true;
            }
            return false;
        });

        int length = etContent.getText().toString().replaceAll("\\s*", "").length();
        setSubTitle(length + "字");
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                DisplayUtil.hideSoftKeyboard(getActivity(), etContent);
                pop();
                break;
            case R.id.tv_right:
                String content = etContent.getText().toString().replaceAll("\\s*", "");
                if (TextUtils.isEmpty(content)) {
                    showToast("请输入正文内容");
                    return;
                }
                DisplayUtil.hideSoftKeyboard(getActivity(), etContent);
                saveContent();
                break;

        }
    }


    /**
     * 保存正文
     */
    private void saveContent() {
        showLoading("保存中");
        Chapter c = new Chapter();
        c.setContent(editContent);
        c.setCount(editContent.replaceAll("\\s*", "").length());
        c.update(chapter.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                hideLoading();
                if (e != null) {
                    showToast("保存失败");
                    return;
                }
                EventBusActivityScope.getDefault(getActivity()).post(new ChapterUpdateEvent());
                pop();
                showToast("保存成功");
            }
        });

    }

    @Override
    public boolean onBackPressedSupport() {
        pop();
        return true;
    }
}
