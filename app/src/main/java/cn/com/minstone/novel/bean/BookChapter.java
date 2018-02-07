package cn.com.minstone.novel.bean;

import java.util.List;

/***
 * 名称：<br>
 * 描述：
 * 最近修改时间：
 * @since 2018/2/1
 * @author king
 */

public class BookChapter {

    Book book;
    List<Chapter> chapterList;

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public List<Chapter> getChapterList() {
        return chapterList;
    }

    public void setChapterList(List<Chapter> chapterList) {
        this.chapterList = chapterList;
    }
}
