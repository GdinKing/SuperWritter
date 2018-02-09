package cn.com.minstone.novel.view;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/***
 * RecyclerView分割线
 *
 * @since 2018/2/2
 * @author king
 */

public class ItemDivider extends RecyclerView.ItemDecoration {
    int mSpace;

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.left = mSpace;
        outRect.right = mSpace;
        outRect.bottom = mSpace;
        outRect.top = mSpace;

    }

    public ItemDivider(int space) {
        this.mSpace = space;
    }
}
