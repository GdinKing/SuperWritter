package cn.com.minstone.novel.frag;

import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.mylhyl.circledialog.CircleDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;
import cn.com.minstone.novel.R;
import cn.com.minstone.novel.adapter.NovelAdapter;
import cn.com.minstone.novel.base.BaseFragment;
import cn.com.minstone.novel.bean.Novel;
import cn.com.minstone.novel.bean.User;
import cn.com.minstone.novel.event.LoginEvent;
import cn.com.minstone.novel.event.NovelUpdateEvent;
import cn.com.minstone.novel.util.DisplayUtil;
import cn.com.minstone.novel.view.ItemDivider;
import me.yokeyword.eventbusactivityscope.EventBusActivityScope;

/***
 * 名称：<br>
 * 描述：
 * 最近修改时间：
 * @since 2018/1/29
 * @author king
 */

public class NovelListFragment extends BaseFragment implements NovelAdapter.OnItemClickListener, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {


    private List<Novel> novelList;
    private NovelAdapter novelAdapter;

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView gridView;

    public static NovelListFragment newInstance() {

        return new NovelListFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_novel_list;
    }

    @Override
    protected void initView() {
        setTitle("书架");
        refreshLayout = rootView.findViewById(R.id.refresh_layout);
        gridView = rootView.findViewById(R.id.gv_list);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.blue);

    }

    @Override
    protected void initData() {
        EventBusActivityScope.getDefault(getActivity()).register(this);
        if (novelList == null) {
            novelList = new ArrayList<>();
        }

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        gridView.addItemDecoration(new ItemDivider(DisplayUtil.dip2px(getContext(), 10)));
        gridView.setLayoutManager(layoutManager);
        novelAdapter = new NovelAdapter(getActivity(), novelList);
        gridView.setAdapter(novelAdapter);
        novelAdapter.setItemClickListener(this);
        User user = BmobUser.getCurrentUser(User.class);
        ivRight.setVisibility(user != null ? View.VISIBLE : View.GONE);
        ivRight.setImageResource(isNightMode ? R.drawable.ic_delete_dark : R.drawable.ic_delete);
        ivRight.setOnClickListener(this);

        loadData();
    }

    private void loadData() {
        User user = BmobUser.getCurrentUser(User.class);
        if (user == null) {
            resetData();
            return;
        }
        refreshLayout.setRefreshing(true);
        BmobQuery<Novel> query = new BmobQuery<>();
        query.addWhereEqualTo("author", user);
        query.addWhereEqualTo("delete", false);
        query.include("author");
        query.order("-updatedAt");
        query.findObjects(new FindListener<Novel>() {
            @Override
            public void done(List<Novel> list, BmobException e) {
                refreshLayout.setRefreshing(false);
                if (e != null) {
                    Log.e("king", e.getMessage(), e);
                    Toast.makeText(getActivity(), "获取小说列表失败", Toast.LENGTH_SHORT).show();
                    resetData();
                    return;
                }

                if (list != null) {
                    novelList = list;
                    novelList.add(new Novel());
                    novelAdapter.refreshData(novelList);
                }
            }
        });
    }


    @Override
    public void onRefresh() {
        loadData();
    }

    private void resetData() {
        refreshLayout.setRefreshing(false);
        novelList = new ArrayList<>();
        novelList.add(new Novel());
        novelAdapter.refreshData(novelList);
    }

    @Override
    public void onDestroy() {
        EventBusActivityScope.getDefault(getActivity()).unregister(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LoginEvent event) {
        if (event != null && event.isLogin) {
            loadData();
        } else {
            resetData();
        }
        ivRight.setVisibility(event.isLogin ? View.VISIBLE : View.GONE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NovelUpdateEvent event) {
        if (event != null) {
            loadData();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_right:
                delectClick();
                break;

            case R.id.tv_left:
                if (novelAdapter != null) {
                    novelAdapter.setMode(NovelAdapter.MODE_NORMAL);
                }
                tvRight.setVisibility(View.GONE);
                tvLeft.setVisibility(View.GONE);
                ivRight.setVisibility(View.VISIBLE);
                novelAdapter.setMode(NovelAdapter.MODE_NORMAL);
                break;
            case R.id.tv_right:
                if (novelAdapter.getSelectList() == null || novelAdapter.getSelectList().size() == 0) {
                    showToast("请选择要删除的作品");
                    return;
                }
                showNotifyDialog();
                break;
        }
    }

    private void delectClick() {
        if (novelAdapter != null) {
            novelAdapter.setMode(NovelAdapter.MODE_DELETE);
        }
        tvRight.setText("删除");
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setOnClickListener(this);
        tvLeft.setVisibility(View.VISIBLE);
        tvLeft.setText("取消");
        tvLeft.setOnClickListener(this);
        ivRight.setVisibility(View.GONE);
    }

    /**
     * 删除选中小说
     */
    private void deleteNovel() {
        if (novelAdapter == null) {
            return;
        }
        List<BmobObject> updateList = new ArrayList<>();
        List<Novel> selectList = novelAdapter.getSelectList();
        for (Novel novel : selectList) {
            novel.setDelete(true);
            updateList.add(novel);
        }
        new BmobBatch().updateBatch(updateList).doBatch(new QueryListListener<BatchResult>() {

            @Override
            public void done(List<BatchResult> o, BmobException e) {
                if (e == null) {
                    boolean isOk = true;
                    for (int i = 0; i < o.size(); i++) {
                        BatchResult result = o.get(i);
                        BmobException ex = result.getError();
                        if (ex != null) {
                            isOk = false;
                        }
                    }
                    if (!isOk) {
                        showToast("部分数据删除失败");
                    } else {
                        showToast("删除成功");
                    }
                    tvRight.setVisibility(View.GONE);
                    tvLeft.setVisibility(View.GONE);
                    ivRight.setVisibility(View.VISIBLE);
                    novelAdapter.setMode(NovelAdapter.MODE_NORMAL);
                    loadData();
                } else {
                    showToast("删除失败");
                    Log.e("king", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }


    /**
     * 通知对话框
     */
    public void showNotifyDialog() {

        new CircleDialog.Builder(getActivity())
                .setTitle("警告")
                .setTitleColor(ContextCompat.getColor(getActivity(), R.color.blue))
                .setText("是否删除选中作品?")
                .setCancelable(true)
                .setNegative("取消", null)
                .setPositive("确定", view -> deleteNovel())
                .show();
    }

//    @Override
//    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
//        if (novelAdapter.getMode() == NovelAdapter.MODE_NORMAL) {
//            delectClick();
//        }
//        return true;
//    }

    @Override
    public void onItemClick(View v, int i) {
        Novel novel = novelList.get(i);
        if (novelAdapter.getItemViewType(i) == NovelAdapter.VIEW_ADD) {
            if (BmobUser.getCurrentUser() == null) {
                showToast("请登录后再试");
                return;
            }
            ((MainFragment) getParentFragment()).openFragment(NovelAddFragment.newInstance());
        } else {
            if (novelAdapter.getMode() == NovelAdapter.MODE_DELETE) {
                novelAdapter.toggleSelect(novel);
                int size = novelAdapter.getSelectList().size();
                if (size == 0) {
                    tvRight.setText("删除");
                } else {
                    tvRight.setText("删除(" + novelAdapter.getSelectList().size() + ")");
                }
            } else {
                ((MainFragment) getParentFragment()).openFragment(NovelDetailFragment.newInstance(novel));
            }
        }
    }
}
