package cn.com.minstone.novel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.com.minstone.novel.R;

/***
 * 小说类型适配器
 *
 * @since 2018/1/30
 * @author king
 */

public class NovelTypeAdapter extends BaseAdapter {
    private List<String> dataList;
    private Context mContext;

    public NovelTypeAdapter(Context pContext, List<String> list) {
        this.mContext = pContext;
        this.dataList = list;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_novel_type, parent, false);
            holder.tvType = convertView.findViewById(R.id.tv_type);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvType.setText(dataList.get(position));
        return convertView;
    }

    public class ViewHolder {
        public TextView tvType;
    }
}
