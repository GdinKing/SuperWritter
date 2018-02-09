package cn.com.minstone.novel.event;

import cn.com.minstone.novel.bean.Novel;

/***
 * 作品简介/大纲/角色设定 更新事件，EventBus专用
 *
 * @since 2018/2/2
 * @author king
 */

public class DescUpdateEvent {

    public Novel novel;

    public DescUpdateEvent(Novel novel) {
        this.novel = novel;
    }
}
