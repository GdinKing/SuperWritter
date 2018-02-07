package cn.com.minstone.novel.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/***
 * 名称：<br>
 * 描述：
 * 最近修改时间：
 * @since 2018/1/30
 * @author king
 */

public class Novel extends BmobObject {

    private String name;
    private String introduction;
    private BmobFile cover;
    private User author;
    private String type;
    private String role;
    private String outline;
    private boolean delete;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public BmobFile getCover() {
        return cover;
    }

    public void setCover(BmobFile cover) {
        this.cover = cover;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getOutline() {
        return outline;
    }

    public void setOutline(String outline) {
        this.outline = outline;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }
}
