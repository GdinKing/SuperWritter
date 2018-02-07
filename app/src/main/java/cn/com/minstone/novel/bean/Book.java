package cn.com.minstone.novel.bean;

import cn.bmob.v3.BmobObject;

/***
 * 名称：卷<br>
 * 描述：
 * 最近修改时间：
 * @since 2018/1/31
 * @author king
 */

public class Book extends BmobObject{

    private String name;
    private Novel novel;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Novel getNovel() {
        return novel;
    }

    public void setNovel(Novel novel) {
        this.novel = novel;
    }

}
