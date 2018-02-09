package cn.com.minstone.novel.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.wujingchao.android.view.SimpleTagImageView;

import java.util.ArrayList;
import java.util.List;

import cn.com.minstone.novel.R;
import cn.com.minstone.novel.bean.Novel;
import cn.com.minstone.novel.util.DisplayUtil;
import cn.com.minstone.novel.util.ImageUtil;

/***
 * 小说列表适配器
 *
 * @since 2018/1/30
 * @author king
 */

public class NovelAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /**
     * 视图类型：添加
     */
    public static final int VIEW_ADD = 0;
    /**
     * 视图类型：展示
     */
    public static final int VIEW_SHOW = 1;

    /**
     * 删除模式
     */
    public static final int MODE_DELETE = 0;
    /**
     * 普通模式
     */
    public static final int MODE_NORMAL = 1;

    private List<Novel> dataList;
    private Context context;
    private List<Novel> selectList;

    private OnItemClickListener clickListener;

    private int mode = MODE_NORMAL;

    public NovelAdapter(Context context, List<Novel> dataList) {
        this.context = context;
        this.dataList = dataList;
        this.selectList = new ArrayList<>();
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
        notifyDataSetChanged();
    }


    public void setItemClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        if (dataList == null) {
            return 0;
        }
        return dataList.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        RecyclerView.ViewHolder holder = null;
        int width = (DisplayUtil.getScreenWidth(context) - DisplayUtil.dip2px(context,70)) / 3;
        int height = width * 4 / 3;
        switch (viewType) {
            case VIEW_SHOW://展示
                view = LayoutInflater.from(context).inflate(R.layout.item_novel_list, parent, false);
                view.getLayoutParams().width = width;
                view.getLayoutParams().height = height;

                holder = new ViewHolderShow(view);
                break;
            case VIEW_ADD://添加按钮
                view = LayoutInflater.from(context).inflate(R.layout.item_novel_add, parent, false);
                view.getLayoutParams().width = width;
                view.getLayoutParams().height = height;
                holder = new ViewHolderAdd(view);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (getItemViewType(position)) {

            case VIEW_SHOW:
                final ViewHolderShow holder = (ViewHolderShow) viewHolder;
                final Novel novel = dataList.get(position);
                holder.tvName.setVisibility(View.VISIBLE);
                holder.tvName.setText(novel.getName());

                if (novel.getCover() != null) {
                    holder.ivCover.setVisibility(View.VISIBLE);
                    holder.tvCover.setVisibility(View.GONE);
                    ImageUtil.showImage(context, novel.getCover().getFileUrl(), holder.ivCover);
                } else {
                    holder.ivCover.setVisibility(View.GONE);
                    holder.tvCover.setVisibility(View.VISIBLE);
                    holder.tvCover.setText(novel.getName());
                }
                if (mode == MODE_DELETE) {
                    holder.rlSelect.setVisibility(View.VISIBLE);
                    holder.cbSelect.setChecked(selectList.contains(novel));
                } else {
                    if (selectList != null) {
                        selectList.clear();
                    }
                    holder.rlSelect.setVisibility(View.GONE);
                }
                if (position == 0) {
                    holder.ivCover.setTagEnable(true);
                    holder.ivCover.setTagText("最近");
                } else {
                    holder.ivCover.setTagEnable(false);
                }
                holder.itemView.setOnClickListener(view -> {
                    if (clickListener != null) {
                        clickListener.onItemClick(view, position);
                    }
                });
                break;

            case VIEW_ADD:
                final ViewHolderAdd holder1 = (ViewHolderAdd) viewHolder;
                holder1.itemView.setOnClickListener(view -> {
                    if (clickListener != null) {
                        clickListener.onItemClick(view, position);
                    }
                });
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == dataList.size() - 1) {
            return VIEW_ADD;
        }
        return VIEW_SHOW;
    }


    public void toggleSelect(Novel novel) {
        if (!this.selectList.contains(novel)) {
            this.selectList.add(novel);
        } else {
            this.selectList.remove(novel);
        }

        notifyDataSetChanged();
    }


    public List<Novel> getSelectList() {
        return selectList;
    }

    public void addData(Novel novel) {
        if (this.dataList != null) {
            this.dataList.add(novel);
            notifyDataSetChanged();
        }
    }

    public void refreshData(List<Novel> novelList) {
        this.dataList = novelList;
        notifyDataSetChanged();
    }

    class ViewHolderShow extends RecyclerView.ViewHolder {
        RelativeLayout rlSelect;
        AppCompatCheckBox cbSelect;
        TextView tvName;
        TextView tvCover;
        SimpleTagImageView ivCover;

        public ViewHolderShow(View v) {
            super(v);
            rlSelect = v.findViewById(R.id.rl_select);
            cbSelect = v.findViewById(R.id.cb_select);
            tvName = v.findViewById(R.id.tv_name);
            tvCover = v.findViewById(R.id.tv_cover);
            ivCover = v.findViewById(R.id.iv_cover);
        }

    }

    class ViewHolderAdd extends RecyclerView.ViewHolder {

        ImageView ivAdd;

        public ViewHolderAdd(View v) {
            super(v);
            ivAdd = v.findViewById(R.id.iv_add);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }
}
