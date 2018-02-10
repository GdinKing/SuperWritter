package cn.com.minstone.novel.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.com.minstone.novel.R;
import cn.com.minstone.novel.bean.Chapter;
import cn.com.minstone.novel.bean.Novel;
import cn.com.minstone.novel.util.ImageUtil;
import cn.com.minstone.novel.util.StringUtil;

/***
 * 小说列表适配器
 *
 * @since 2018/1/30
 * @author king
 */

public class DraftAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<Chapter> dataList;
    private Context context;

    private OnItemClickListener clickListener;


    public DraftAdapter(Context context, List<Chapter> dataList) {
        this.context = context;
        this.dataList = dataList;
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


        view = LayoutInflater.from(context).inflate(R.layout.item_draft, parent, false);
        holder = new ViewHolderShow(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {


        final ViewHolderShow holder = (ViewHolderShow) viewHolder;
        final Chapter chapter = dataList.get(position);
        Novel novel = chapter.getNovel();

        holder.tvName.setText("《"+novel.getName()+"》");
        holder.tvStatus.setText(chapter.getName());
        holder.tvTime.setText(StringUtil.formatDate(chapter.getUpdatedAt()));
        if (novel.getCover() != null) {
            holder.ivCover.setVisibility(View.VISIBLE);
            holder.tvCover.setVisibility(View.GONE);
            ImageUtil.showImage(context, novel.getCover().getFileUrl(), holder.ivCover);
        } else {
            holder.ivCover.setVisibility(View.GONE);
            holder.tvCover.setVisibility(View.VISIBLE);
            holder.tvCover.setText(novel.getName());
        }

        holder.itemView.setOnClickListener(view -> {
            if (clickListener != null) {
                clickListener.onItemClick(view, position);
            }
        });


    }

    public void refreshData(List<Chapter> novelList) {
        this.dataList = novelList;
        notifyDataSetChanged();
    }

    class ViewHolderShow extends RecyclerView.ViewHolder {
        TextView tvTime;
        TextView tvName;
        TextView tvCover;
        TextView tvStatus;
        ImageView ivCover;

        public ViewHolderShow(View v) {
            super(v);
            tvTime = v.findViewById(R.id.tv_time);
            tvName = v.findViewById(R.id.tv_name);
            tvCover = v.findViewById(R.id.tv_cover);
            tvStatus = v.findViewById(R.id.tv_status);
            ivCover = v.findViewById(R.id.iv_cover);
        }

    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }
}
