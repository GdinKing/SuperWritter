package cn.com.minstone.novel.frag;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.com.minstone.novel.R;
import cn.com.minstone.novel.adapter.DraftAdapter;
import cn.com.minstone.novel.base.BaseFragment;
import cn.com.minstone.novel.bean.Chapter;
import cn.com.minstone.novel.bean.Novel;
import cn.com.minstone.novel.bean.User;
import cn.com.minstone.novel.view.CustomRecyclerView;
import cn.com.minstone.novel.view.ItemDivider;

/***
 * 名称：<br>
 * 描述：
 * 最近修改时间：
 * @since 2018/2/10
 * @author king
 */

public class DraftFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, DraftAdapter.OnItemClickListener {


    private List<Chapter> chapterList;
    private DraftAdapter draftAdapter;
    private SwipeRefreshLayout refreshLayout;
    private CustomRecyclerView listView;

    public static DraftFragment newInstance() {
        DraftFragment fragment = new DraftFragment();

        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_draft_list;
    }

    @Override
    protected void initView() {
        setTitle("草稿箱");
        enableBack();
        ivBack.setOnClickListener(this);
        refreshLayout = rootView.findViewById(R.id.refresh_layout);
        listView = rootView.findViewById(R.id.draft_list);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.blue);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        listView.setLayoutManager(manager);
        listView.addItemDecoration(new ItemDivider(10));
    }

    @Override
    protected void initData() {

        User user = BmobUser.getCurrentUser(User.class);
        if (user == null) {
            showToast("用户未登录");
            return;
        }
        refreshLayout.setRefreshing(true);

        BmobQuery<Chapter> chapterQuery = new BmobQuery<>();
        chapterQuery.addWhereEqualTo("publish", false);//未发布，即为草稿
        chapterQuery.include("novel,novel.author");
        chapterQuery.order("-updatedAt");//更新时间排序，获取最新的一章

        BmobQuery<Novel> innerQuery = new BmobQuery<>();
        innerQuery.addWhereEqualTo("author", user);
        chapterQuery.addWhereMatchesQuery("novel", "Novel", innerQuery);


        chapterQuery.findObjects(new FindListener<Chapter>() {
            @Override
            public void done(List<Chapter> list, BmobException e) {

                refreshLayout.setRefreshing(false);
                if (e != null) {
                    Log.e("king", e.getMessage(), e);
                    showToast("获取数据失败");
                    return;
                }
                bindData(list);
            }
        });


    }


    /**
     * 绑定数据
     *
     * @param list
     */
    private void bindData(List<Chapter> list) {

        if (chapterList == null) {
            chapterList = new ArrayList<>();
        }
        chapterList.clear();
        for (Chapter chapter : list) {
            if (chapter.getNovel() == null) {
                continue;
            }
            if (!TextUtils.isEmpty(chapter.getNovel().getName())) {
                boolean isOk = true;
                for (Chapter c : chapterList) {
                    if (c.getNovel().getObjectId().equals(chapter.getNovel().getObjectId())) {
                        isOk = false;
                    }
                }
                if (isOk) {
                    chapterList.add(chapter);
                }
            }
        }
        if (chapterList.isEmpty()) {
            ViewStub emptyStub = rootView.findViewById(R.id.empty_stub);
            emptyStub.inflate();
            return;
        }
        if (draftAdapter == null) {
            draftAdapter = new DraftAdapter(getActivity(), chapterList);
            listView.setAdapter(draftAdapter);
        } else {
            draftAdapter.refreshData(chapterList);
        }

        draftAdapter.setItemClickListener(this);
    }

    @Override
    public void onRefresh() {
        initData();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_back) {
            pop();
        }
    }

    @Override
    public void onItemClick(View v, int position) {
        Chapter chapter = chapterList.get(position);
        if (chapter != null && chapter.getNovel() != null) {
            start(NovelDetailFragment.newInstance(chapter.getNovel()));
        }
    }

    @Override
    public boolean onBackPressedSupport() {
        pop();
        return true;
    }
}
