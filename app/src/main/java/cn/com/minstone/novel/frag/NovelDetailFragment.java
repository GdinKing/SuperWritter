package cn.com.minstone.novel.frag;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;

import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigItems;
import com.mylhyl.circledialog.callback.ConfigTitle;
import com.mylhyl.circledialog.params.ItemsParams;
import com.mylhyl.circledialog.params.TitleParams;
import com.mylhyl.circledialog.res.values.CircleDimen;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.com.minstone.novel.R;
import cn.com.minstone.novel.adapter.FragmentAdapter;
import cn.com.minstone.novel.base.BaseFragment;
import cn.com.minstone.novel.bean.Book;
import cn.com.minstone.novel.bean.Novel;
import cn.com.minstone.novel.event.ChapterUpdateEvent;
import cn.com.minstone.novel.util.DisplayUtil;
import me.yokeyword.eventbusactivityscope.EventBusActivityScope;
import me.yokeyword.fragmentation.SupportFragment;

/***
 * 小说详情界面，管理两个子Fragment
 *
 * @since 2018/1/30
 * @author king
 */

public class NovelDetailFragment extends BaseFragment implements View.OnClickListener {

    private TabLayout tabLayout;
    private ViewPager vpContainer;

    private Novel currentNovel;
    private List<BaseFragment> fragmentList;
    private FragmentAdapter fragmentAdapter;

    public static NovelDetailFragment newInstance(Novel novel) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("novel", novel);
        NovelDetailFragment fragment = new NovelDetailFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_novel_detail;
    }

    @Override
    protected void initView() {
        enableBack();
        ivRight.setVisibility(View.VISIBLE);
        ivRight.setOnClickListener(this);
        ivRight.setImageResource(isNightMode ? R.drawable.icon_add_white_dark : R.drawable.icon_add_white);
        tabLayout = rootView.findViewById(R.id.tab_layout);
        vpContainer = rootView.findViewById(R.id.vp_container);
        ivBack.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        currentNovel = (Novel) getArguments().getSerializable("novel");
        if (currentNovel == null || BmobUser.getCurrentUser() == null) {
            return;
        }
        setTitle("《" + currentNovel.getName() + "》");
        fragmentList = new ArrayList<>();
        fragmentList.add(ChapterListFragment.newInstance(currentNovel));
        fragmentList.add(NovelDescFragment.newInstance(currentNovel));
        fragmentAdapter = new FragmentAdapter(getChildFragmentManager(), fragmentList);
        vpContainer.setAdapter(fragmentAdapter);
        tabLayout.setupWithViewPager(vpContainer);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                pop();
                break;
            case R.id.iv_right:
                showOperatation();
                break;
        }
    }

    @Override
    public boolean onBackPressedSupport() {
        pop();
        return true;
    }

    /**
     * 显示操作对话框
     */
    public void showOperatation() {
        final String[] items = new String[]{"新增分卷", "新增章节"};/*设置列表的内容*/
        new CircleDialog.Builder(getActivity())
                .setTitle("请选择操作")
                .configTitle(new ConfigTitle() {
                    @Override
                    public void onConfig(TitleParams params) {
                        params.textSize = CircleDimen.CONTENT_TEXT_SIZE;
                    }
                })
                .configItems(new ConfigItems() {
                    @Override
                    public void onConfig(ItemsParams params) {
                        params.textColor = Color.BLACK;
                    }
                })
                .setItems(items, (parent, view, position, id) -> {

                    if (position == 1) {
                        if (currentNovel == null) {
                            return;
                        }
                        start(ChapterAddFragment.newInstance(currentNovel, ""));
                    } else {
                        showAddBook();
                    }
                })
                .setGravity(Gravity.CENTER)
                .show();
    }

    /**
     * 添加分卷对话框
     */
    private void showAddBook() {

        new CircleDialog.Builder(getActivity())
                .setInputHint("请输入分卷名称")
                .setTitle("新增分卷")
                .setInputHeight(DisplayUtil.dip2px(getActivity(), 44))
                .setNegative("取消", null)
                .setPositiveInput("确定", (text, v) -> {
                    if (TextUtils.isEmpty(text)) {
                        showToast("请输入分卷名称");
                        return;
                    }
                    addBook(text);

                }).show();

    }

    /**
     * 添加分卷
     * @param name
     */
    private void addBook(String name) {
        showLoading();
        Book book = new Book();
        book.setName(name);
        book.setNovel(currentNovel);

        book.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                hideLoading();
                if (e != null) {
                    showToast("获取数据失败，请重试");
                    return;
                }
                showToast("添加成功");
                EventBusActivityScope.getDefault(getActivity()).post(new ChapterUpdateEvent());
            }
        });
    }

    public void openFragment(SupportFragment fragment) {
        start(fragment);
    }

}
