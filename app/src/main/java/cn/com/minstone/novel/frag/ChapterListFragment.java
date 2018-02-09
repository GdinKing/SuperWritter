package cn.com.minstone.novel.frag;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigButton;
import com.mylhyl.circledialog.callback.ConfigInput;
import com.mylhyl.circledialog.callback.ConfigItems;
import com.mylhyl.circledialog.callback.ConfigTitle;
import com.mylhyl.circledialog.params.ButtonParams;
import com.mylhyl.circledialog.params.InputParams;
import com.mylhyl.circledialog.params.ItemsParams;
import com.mylhyl.circledialog.params.TitleParams;
import com.mylhyl.circledialog.res.values.CircleDimen;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.com.minstone.novel.R;
import cn.com.minstone.novel.adapter.ChapterAdapter;
import cn.com.minstone.novel.base.BaseFragment;
import cn.com.minstone.novel.bean.Book;
import cn.com.minstone.novel.bean.BookChapter;
import cn.com.minstone.novel.bean.Chapter;
import cn.com.minstone.novel.bean.Novel;
import cn.com.minstone.novel.event.ChapterUpdateEvent;
import cn.com.minstone.novel.util.DisplayUtil;
import me.yokeyword.eventbusactivityscope.EventBusActivityScope;

/***
 * 章节列表界面
 *
 * @since 2018/1/31
 * @author king
 */

public class ChapterListFragment extends BaseFragment implements ChapterAdapter.OnGroupItemClickListener, ChapterAdapter.OnChildItemLongClickListener, ChapterAdapter.OnChildItemClickListener, ChapterAdapter.OnGroupItemLongClickListener {

    public static ChapterListFragment newInstance(Novel novel) {
        ChapterListFragment fragment = new ChapterListFragment();
        Bundle b = new Bundle();
        b.putSerializable("novel", novel);
        fragment.setArguments(b);
        return fragment;
    }

    private ExpandableListView listView;
    private TextView tvEmpty;
    private ChapterAdapter chapterAdapter;
    private List<Book> bookList;
    private List<Chapter> chapterList;
    private List<BookChapter> dataList;
    private Novel currentNovel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_chapter;
    }

    @Override
    protected void initView() {
        listView = rootView.findViewById(R.id.list_chapter);
        tvEmpty = rootView.findViewById(R.id.tv_empty);
    }

    @Override
    protected void initData() {
        EventBusActivityScope.getDefault(getActivity()).register(this);
        currentNovel = (Novel) getArguments().getSerializable("novel");
        dataList = new ArrayList<>();
        chapterAdapter = new ChapterAdapter(getActivity(), dataList);
        listView.setAdapter(chapterAdapter);
        listView.setEmptyView(tvEmpty);
        chapterAdapter.setChildItemClickListener(this);
        chapterAdapter.setChildItemLongClickListener(this);
        chapterAdapter.setGroupItemLongClickListener(this);
        chapterAdapter.setGroupItemClickListener(this);
        if (currentNovel == null || BmobUser.getCurrentUser() == null) {
            return;
        }
        showLoading();
        loadBook(currentNovel);
    }


    /**
     * 加载分卷数据
     *
     * @param novel
     */
    private void loadBook(final Novel novel) {

        BmobQuery<Book> query = new BmobQuery<>();
        query.addWhereEqualTo("novel", new BmobPointer(novel));
        query.order("createdAt");
        query.findObjects(new FindListener<Book>() {
            @Override
            public void done(List<Book> list, BmobException e) {
                if (e != null) {
                    hideLoading();
                    showToast("获取章节列表失败，请重试");
                    return;
                }
                bookList = list;
                loadChapter(novel);
            }
        });

    }

    /**
     * 加载章节
     */
    private void loadChapter(Novel novel) {
        BmobQuery<Chapter> query = new BmobQuery<>();
        query.addWhereEqualTo("novel", new BmobPointer(novel));
        query.include("book");
        query.order("createdAt");
        query.findObjects(new FindListener<Chapter>() {
            @Override
            public void done(List<Chapter> list, BmobException e) {

                if (e != null) {
                    hideLoading();
                    showToast("获取数据失败，请重试");
                    Log.e("king", e.getMessage(), e);
                    return;
                }
                chapterList = list;
                if (dataList == null) {
                    dataList = new ArrayList<>();
                }
                dataList.clear();
                if (bookList != null && bookList.size() > 0) {
                    for (int i = 0; i < bookList.size(); i++) {
                        Book book = bookList.get(i);
                        List<Chapter> chapters = new ArrayList<>();
                        BookChapter bookChapter = new BookChapter();
                        bookChapter.setBook(book);
                        for (Chapter chapter : chapterList) {
                            if (book.getObjectId().equals(chapter.getBook().getObjectId())) {
                                chapters.add(chapter);
                            }
                        }
                        bookChapter.setChapterList(chapters);
                        dataList.add(bookChapter);
                    }
                } else {
                    BookChapter bookChapter = new BookChapter();
                    bookChapter.setChapterList(chapterList);
                    dataList.add(bookChapter);
                }
                chapterAdapter.refreshData(dataList);
                hideLoading();
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ChapterUpdateEvent event) {
        if (event != null && currentNovel != null) {
            loadBook(currentNovel);
        }
    }

    @Override
    public void onDestroy() {
        EventBusActivityScope.getDefault(getActivity()).unregister(this);
        super.onDestroy();
    }


    @Override
    public void onChildClick(int groupPos, int childPos) {
        Chapter chapter = dataList.get(groupPos).getChapterList().get(childPos);
        if (chapter == null) {
            showToast("数据出错");
            return;
        }
        ((NovelDetailFragment) getParentFragment()).openFragment(ChapterEditFragment.newInstance(chapter));
    }

    @Override
    public boolean onChildLongClick(int groupPos, int childPos) {
        Chapter chapter = dataList.get(groupPos).getChapterList().get(childPos);
        String[] items = new String[]{"修改章节名", "删除章节"};
        new CircleDialog.Builder(getActivity())
                .setGravity(Gravity.CENTER)
                .configItems(new ConfigItems() {
                    @Override
                    public void onConfig(ItemsParams params) {
                        params.textColor = Color.BLACK;
                    }
                })
                .setItems(items, (adapterView, view, i, l) -> {
                    if (i == 0) {
                        showUpdateChapter(chapter);
                    } else {
                        showDeleteChapterDialog(chapter);
                    }
                }).show();

        return true;
    }


    @Override
    public boolean onGroupLongClick(int groupPos) {
        Book book = dataList.get(groupPos).getBook();
        String[] items = new String[]{"新增章节", "修改分卷名", "删除分卷"};
        new CircleDialog.Builder(getActivity())
                .setGravity(Gravity.CENTER)
                .configItems(new ConfigItems() {
                    @Override
                    public void onConfig(ItemsParams params) {
                        params.textColor = Color.BLACK;
                    }
                })
                .setItems(items, (adapterView, view, i, l) -> {
                    if (i == 0) {
                        ((NovelDetailFragment) getParentFragment()).openFragment(ChapterAddFragment.newInstance(currentNovel, book.getObjectId()));
                    } else if (i == 1) {
                        showUpdateGroup(book);
                    } else {
                        showDeleteBookDialog(book);
                    }
                }).show();

        return true;
    }

    @Override
    public void onGroupClick(int groupPos) {
        boolean isExpand = listView.isGroupExpanded(groupPos);
        if (!isExpand) {
            listView.expandGroup(groupPos);
        } else {
            listView.collapseGroup(groupPos);
        }
    }

    /**
     * 显示修改分卷对话框
     *
     * @param book
     */
    private void showUpdateGroup(Book book) {
        new CircleDialog.Builder(getActivity())
                .setInputHint("请输入分卷名称")
                .setTitle("修改分卷：" + book.getName())
                .setTitleColor(Color.BLACK)
                .configTitle(new ConfigTitle() {
                    @Override
                    public void onConfig(TitleParams params) {
                        params.textSize = CircleDimen.CONTENT_TEXT_SIZE;
                    }
                })
                .setInputHeight(DisplayUtil.dip2px(getActivity(), 45))
                .configNegative(new ConfigButton() {
                    @Override
                    public void onConfig(ButtonParams params) {
                        params.textSize = CircleDimen.CONTENT_TEXT_SIZE;
                    }
                })
                .configInput(new ConfigInput() {
                    @Override
                    public void onConfig(InputParams params) {
                        params.textColor = Color.BLACK;
                        params.inputBackgroundResourceId = R.drawable.bg_input;
                    }
                })
                .configPositive(new ConfigButton() {
                    @Override
                    public void onConfig(ButtonParams params) {
                        params.textSize = CircleDimen.CONTENT_TEXT_SIZE;
                    }
                })
                .setNegative("取消", null)
                .setPositiveInput("确定", (text, v) -> {
                    if (TextUtils.isEmpty(text)) {
                        showToast("请输入分卷名称");
                        return;
                    }
                    book.setName(text);
                    updateBook(book);
                }).show();

    }

    /**
     * 弹出删除分卷对话框
     *
     * @param book
     */
    private void showDeleteBookDialog(Book book) {

        new CircleDialog.Builder(getActivity())
                .setTitle("警告")
                .setText(Html.fromHtml("是否删除分卷 <font color='red'>" + book.getName() + "</font> 及其章节?"))

                .setNegative("取消", null)
                .setPositive("确定", view -> deleteBook(book)).show();

    }


    private void updateBook(Book book) {
        showLoading("修改中");
        book.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                hideLoading();
                if (e != null) {
                    showToast("修改失败");
                    return;
                }
                loadBook(currentNovel);
                showToast("修改成功");
            }
        });

    }

    private void deleteBook(Book book) {
        showLoading("删除中");
        book.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                hideLoading();
                if (e != null) {
                    showToast("删除失败");
                    return;
                }
                loadBook(currentNovel);
                showToast("删除成功");
            }
        });

    }

    /**
     * 显示修改章节对话框
     *
     * @param chapter
     */
    private void showUpdateChapter(Chapter chapter) {
        new CircleDialog.Builder(getActivity())
                .setInputHint("请输入章节名称")
                .setTitle("修改章节：" + chapter.getName())
                .configTitle(new ConfigTitle() {
                    @Override
                    public void onConfig(TitleParams params) {
                        params.textSize = CircleDimen.CONTENT_TEXT_SIZE;
                    }
                })
                .setInputHeight(DisplayUtil.dip2px(getActivity(), 45))
                .configNegative(new ConfigButton() {
                    @Override
                    public void onConfig(ButtonParams params) {
                        params.textSize = CircleDimen.CONTENT_TEXT_SIZE;
                    }
                })
                .configInput(new ConfigInput() {
                    @Override
                    public void onConfig(InputParams params) {
                        params.textColor = Color.BLACK;
                        params.inputBackgroundResourceId = R.drawable.bg_input;
                    }
                })
                .configPositive(new ConfigButton() {
                    @Override
                    public void onConfig(ButtonParams params) {
                        params.textSize = CircleDimen.CONTENT_TEXT_SIZE;
                    }
                })
                .setNegative("取消", null)
                .setPositiveInput("确定", (text, v) -> {
                    if (TextUtils.isEmpty(text)) {
                        showToast("请输入章节名称");
                        return;
                    }
                    chapter.setName(text);
                    updateChapter(chapter);
                }).show();

    }

    /**
     * 弹出删除分卷对话框
     *
     * @param chapter
     */
    private void showDeleteChapterDialog(Chapter chapter) {

        new CircleDialog.Builder(getActivity())
                .setTitle("警告")
                .setText(Html.fromHtml("是否删除章节 <font color='red'>" + chapter.getName() + "</font>?"))
                .setTextColor(Color.BLACK)
                .setNegative("取消", null)
                .setPositive("确定", view -> deleteChapter(chapter)).show();

    }

    /**
     * 修改章节名
     *
     * @param chapter
     */
    private void updateChapter(Chapter chapter) {
        showLoading("修改中");
        chapter.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                hideLoading();
                if (e != null) {
                    showToast("修改失败");
                    return;
                }
                loadBook(currentNovel);
                showToast("修改成功");
            }
        });

    }

    /**
     * 删除章节
     *
     * @param chapter
     */
    private void deleteChapter(Chapter chapter) {
        showLoading("删除中");
        chapter.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                hideLoading();
                if (e != null) {
                    showToast("删除失败");
                    return;
                }
                loadBook(currentNovel);
                showToast("删除成功");
            }
        });

    }
}
