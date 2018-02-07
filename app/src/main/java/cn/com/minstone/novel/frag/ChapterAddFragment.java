package cn.com.minstone.novel.frag;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.com.minstone.novel.R;
import cn.com.minstone.novel.adapter.BookListAdapter;
import cn.com.minstone.novel.base.BaseFragment;
import cn.com.minstone.novel.bean.Book;
import cn.com.minstone.novel.bean.Chapter;
import cn.com.minstone.novel.bean.Novel;
import cn.com.minstone.novel.event.ChapterUpdateEvent;
import cn.com.minstone.novel.util.DisplayUtil;
import me.yokeyword.eventbusactivityscope.EventBusActivityScope;

/***
 * 名称：<br>
 * 描述：
 * 最近修改时间：
 * @since 2018/1/31
 * @author king
 */

public class ChapterAddFragment extends BaseFragment implements View.OnClickListener {


    private Novel novel;
    private String bookId;

    public static ChapterAddFragment newInstance(Novel novel, String bookId) {

        Bundle b = new Bundle();
        b.putSerializable("novel", novel);
        b.putString("bookId", bookId);
        ChapterAddFragment fragment = new ChapterAddFragment();
        fragment.setArguments(b);
        return fragment;
    }

    private EditText etName;
    private TextView tvNovel;
    private TextView tvEmpty;
    private Spinner spBook;
    private Book selectedBook;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_add_chapter;
    }

    @Override
    protected void initView() {
        setTitle("新增章节");
        enableBack();
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText("完成");
        tvRight.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        etName = rootView.findViewById(R.id.et_chapter);
        spBook = rootView.findViewById(R.id.sp_book);
        tvEmpty = rootView.findViewById(R.id.empty_view);
        tvNovel = rootView.findViewById(R.id.tv_novel);
    }

    @Override
    protected void initData() {
        novel = (Novel) getArguments().getSerializable("novel");
        bookId = getArguments().getString("bookId");
        if (novel == null) {
            return;
        }
        tvNovel.setText("《" + novel.getName() + "》");
        BmobQuery<Book> book = new BmobQuery<>();
        book.addWhereEqualTo("novel", novel);
        book.findObjects(new FindListener<Book>() {
            @Override
            public void done(List<Book> list, BmobException e) {
                if (e != null) {
                    showToast("获取分卷失败，请返回重试");
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
    private void bindData(final List<Book> list) {

        BookListAdapter adapter = new BookListAdapter(getActivity(), list);
        spBook.setAdapter(adapter);
        spBook.setEmptyView(tvEmpty);
        spBook.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                selectedBook = list.get(pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if (TextUtils.isEmpty(bookId)) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getObjectId().equals(bookId)) {
                spBook.setSelection(i);
                break;
            }
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                DisplayUtil.hideSoftKeyboard(getActivity(),etName);
                pop();
                break;
            case R.id.tv_right:
                String name = etName.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    showToast("章节名不能为空");
                    return;
                }
                if (selectedBook == null) {
                    showToast("请选择分卷");
                    return;
                }
                DisplayUtil.hideSoftKeyboard(getActivity(),etName);
                addChapter(name);
                break;
        }
    }

    /**
     * 添加章节
     * @param name
     */
    public void addChapter(String name) {
        showLoading();
        Chapter chapter = new Chapter();
        chapter.setNovel(novel);
        chapter.setBook(selectedBook);
        chapter.setName(name);
        chapter.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                hideLoading();
                if (e != null) {
                    showToast("获取分卷失败，请返回重试");
                    return;
                }
                showToast("新增章节成功");
                EventBusActivityScope.getDefault(getActivity()).post(new ChapterUpdateEvent());
                pop();
            }
        });

    }
}
