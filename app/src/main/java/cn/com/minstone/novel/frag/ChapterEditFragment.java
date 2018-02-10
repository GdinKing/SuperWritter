package cn.com.minstone.novel.frag;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.mylhyl.circledialog.CircleDialog;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.com.minstone.novel.R;
import cn.com.minstone.novel.base.BaseFragment;
import cn.com.minstone.novel.bean.Chapter;
import cn.com.minstone.novel.event.ChapterUpdateEvent;
import cn.com.minstone.novel.util.DisplayUtil;
import me.yokeyword.eventbusactivityscope.EventBusActivityScope;

/***
 * 编辑章节界面
 *
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

    private EditText etContent;
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
        tvRightSecond.setVisibility(View.VISIBLE);
        tvRightSecond.setOnClickListener(this);
        tvRight.setVisibility(View.VISIBLE);
        tvRightSecond.setText("保存");
        tvRight.setText("发布");
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
                showPublishDialog();
                break;
            case R.id.tv_right_second:
                DisplayUtil.hideSoftKeyboard(getActivity(), etContent);
                saveOrPublish(false);
                break;

        }
    }

    /**
     * 弹出发布提示
     */
    private void showPublishDialog() {
        new CircleDialog.Builder(getActivity())
                .setTitle("提示")
                .setTitleColor(ContextCompat.getColor(getActivity(), R.color.blue))
                .setText("是否发布该章节?")
                .setNegative("取消", null)
                .setPositive("确定", view -> {
                    saveOrPublish(true);
                }).show();

    }

    /**
     * 保存/发布
     */
    private void saveOrPublish(boolean isPublish) {
        showLoading("保存中");
        Chapter c = new Chapter();
        c.setContent(editContent);
        c.setPublish(isPublish);
        c.setWords(editContent.replaceAll("\\s*", "").length());
        c.update(chapter.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                hideLoading();
                if (e != null) {
                    showToast(isPublish ? "发布失败" : "保存失败");
                    return;
                }
                EventBusActivityScope.getDefault(getActivity()).post(new ChapterUpdateEvent());
                pop();
                showToast(isPublish ? "发布成功" : "保存成功");
            }
        });

    }

    @Override
    public boolean onBackPressedSupport() {
        pop();
        return true;
    }
}
