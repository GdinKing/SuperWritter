package cn.com.minstone.novel.event;

import cn.com.minstone.novel.bean.Novel;

/***
 * 名称：<br>
 * 描述：
 * 最近修改时间：
 * @since 2018/2/2
 * @author king
 */

public class DescUpdateEvent {

    public Novel novel;

    public DescUpdateEvent(Novel novel) {
        this.novel = novel;
    }
}
