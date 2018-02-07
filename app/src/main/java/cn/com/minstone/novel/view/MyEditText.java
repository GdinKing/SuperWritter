package cn.com.minstone.novel.view;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.text.Layout;
import android.text.Selection;
import android.util.AttributeSet;
import android.view.View;

/***
 * 名称：<br>
 * 描述：
 * 最近修改时间：
 * @since 2018/2/1
 * @author king
 */

public class MyEditText extends AppCompatEditText {

    private OnSoftKeyboardClickListener keyboardClickListener;

    public MyEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyEditText(Context context) {
        super(context);
    }

    /**
     * 设置删除和回车键监听
     * @param listener
     */
    public void setOnSoftKeyboardClickListener(OnSoftKeyboardClickListener listener) {
        this.keyboardClickListener = listener;
    }

    /**
     * 获取光标所在行
     * @return
     */
    public int getCurrentCursorLine() {
        int selectionStart = Selection.getSelectionStart(this.getText());
        Layout layout = this.getLayout();
        if (selectionStart != -1) {
            return layout.getLineForOffset(selectionStart);
        }
        return -1;
    }


    public interface OnSoftKeyboardClickListener {
//        public boolean onDeleteClick(View view);
        public boolean onEnterClick(View view);
    }
}
