package com.example.comment;

import android.text.TextUtils;
import android.widget.TextView;

import java.util.List;

public class CommentBean {
    private String title;

    private String content;

    private String replySuffix;

    private int type;

    private boolean hasMore;

    private boolean commentLove;

    private List<CommentBean> childs;

    public CommentBean(String title, int type, boolean hasMore, List<CommentBean> childs) {
        this.title = title;
        this.type = type;
        this.hasMore = hasMore;
        this.childs = childs;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public List<CommentBean> getChilds() {
        return childs;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public String getContent() {
        if (TextUtils.isEmpty(content)){
            return "content";
        }
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReplySuffix() {
        return replySuffix;
    }

    public void setReplySuffix(String replySuffix) {
        this.replySuffix = replySuffix;
    }

    public boolean isCommentLove() {
        return commentLove;
    }

    public void setCommentLove(boolean commentLove) {
        this.commentLove = commentLove;
    }

    public void toggleCommentLove(){
        this.commentLove = !this.commentLove;
    }
}
