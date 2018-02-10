package cn.com.minstone.novel.bean;

import cn.bmob.v3.BmobObject;

/***
 * 名称：<br>
 * 描述：
 * 最近修改时间：
 * @since 2018/1/30
 * @author king
 */

public class Chapter extends BmobObject {

    private Novel novel;
    private String name;
    private Book book;
    private String content;
    private Integer count;
    private Integer words;
    private boolean publish;

    public Novel getNovel() {
        return novel;
    }

    public void setNovel(Novel novel) {
        this.novel = novel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getWords() {
        return words;
    }

    public void setWords(Integer words) {
        this.words = words;
    }

    public boolean isPublish() {
        return publish;
    }

    public void setPublish(boolean publish) {
        this.publish = publish;
    }
}
