package cn.com.minstone.novel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.com.minstone.novel.R;
import cn.com.minstone.novel.bean.BookChapter;
import cn.com.minstone.novel.bean.Chapter;

/***
 * 名称：<br>
 * 描述：
 * 最近修改时间：
 * @since 2018/1/31
 * @author king
 */

public class ChapterAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<BookChapter> bookList;
    private LayoutInflater inflater;

    private OnChildItemClickListener childItemClickListener;
    private OnChildItemLongClickListener childItemLongClickListener;
    private OnGroupItemClickListener groupItemClickListener;
    private OnGroupItemLongClickListener groupItemLongClickListener;

    public ChapterAdapter(Context context, List<BookChapter> bookList) {
        this.context = context;
        this.bookList = bookList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getGroupCount() {
        if (bookList == null) {
            return 0;
        }
        if (bookList.size() > 0 && bookList.get(0).getBook() == null) {
            return 0;
        }
        return bookList.size();
    }

    @Override
    public int getChildrenCount(int i) {
        if (bookList == null || bookList.get(i).getChapterList() == null) {
            return 0;
        }
        return bookList.get(i).getChapterList().size();
    }

    @Override
    public Object getGroup(int i) {
        if (bookList == null) {
            return null;
        }
        if (bookList.size() > 0 && bookList.get(0).getBook() == null) {
            return 0;
        }
        return bookList.get(i);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if (bookList == null || bookList.get(groupPosition).getChapterList() == null) {
            return null;
        }
        return bookList.get(groupPosition).getChapterList().get(childPosition);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean isExpand, View view, ViewGroup viewGroup) {
        GroupViewHolder holder = null;
        if (view == null) {
            holder = new GroupViewHolder();
            view = inflater.inflate(R.layout.item_group, viewGroup, false);
            holder.initView(view);
            view.setTag(holder);
        } else {
            holder = (GroupViewHolder) view.getTag();
        }
        BookChapter book = bookList.get(i);
        holder.tvName.setText(book.getBook().getName());
        if (isExpand) {
            holder.ivRight.setRotation(90);
        } else {
            holder.ivRight.setRotation(0);
        }
        view.setOnClickListener(view1 -> {
            if (groupItemClickListener != null) {
                groupItemClickListener.onGroupClick(i);
            }
        });
        view.setOnLongClickListener(view12 -> {
            if (groupItemLongClickListener != null) {
                return groupItemLongClickListener.onGroupLongClick(i);
            }
            return false;
        });
        return view;
    }

    @Override
    public View getChildView(int position, int childPosition, boolean isExpand, View view, ViewGroup viewGroup) {
        ChapterViewHolder holder = null;
        if (view == null) {
            holder = new ChapterViewHolder();
            view = inflater.inflate(R.layout.item_chapter, viewGroup, false);
            holder.initView(view);
            view.setTag(holder);
        } else {
            holder = (ChapterViewHolder) view.getTag();
        }
        Chapter chapter = bookList.get(position).getChapterList().get(childPosition);
        holder.tvName.setText(chapter.getName());
        view.setOnClickListener(view1 -> {
            if (childItemClickListener != null) {
                childItemClickListener.onChildClick(position, childPosition);
            }
        });
        view.setOnLongClickListener(view12 -> {
            if (childItemLongClickListener != null) {
                return childItemLongClickListener.onChildLongClick(position, childPosition);
            }
            return false;
        });
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    public void refreshData(List<BookChapter> books) {
        this.bookList = books;
        notifyDataSetChanged();
    }

    class GroupViewHolder {
        TextView tvName;
        ImageView ivRight;

        public void initView(View v) {
            tvName = v.findViewById(R.id.tv_name);
            ivRight = v.findViewById(R.id.iv_right);
        }
    }

    class ChapterViewHolder {
        TextView tvName;

        public void initView(View v) {
            tvName = v.findViewById(R.id.tv_name);
        }
    }

    public void setChildItemClickListener(OnChildItemClickListener childItemClickListener) {
        this.childItemClickListener = childItemClickListener;
    }

    public void setChildItemLongClickListener(OnChildItemLongClickListener childItemLongClickListener) {
        this.childItemLongClickListener = childItemLongClickListener;
    }

    public void setGroupItemClickListener(OnGroupItemClickListener groupItemClickListener) {
        this.groupItemClickListener = groupItemClickListener;
    }

    public void setGroupItemLongClickListener(OnGroupItemLongClickListener groupItemLongClickListener) {
        this.groupItemLongClickListener = groupItemLongClickListener;
    }

    public interface OnChildItemClickListener {
        void onChildClick(int groupPos, int childPos);
    }

    public interface OnChildItemLongClickListener {
        boolean onChildLongClick(int groupPos, int childPos);
    }

    public interface OnGroupItemClickListener {
        void onGroupClick(int groupPos);
    }

    public interface OnGroupItemLongClickListener {
        boolean onGroupLongClick(int groupPos);
    }
}
