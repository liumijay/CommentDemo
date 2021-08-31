package com.example.comment.emojikeyboard;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.example.comment.util.ScreenUtil;

import java.util.ArrayList;
import java.util.List;

public class EmojiPagerAdapter extends PagerAdapter {
    private Context context;
    private List<List<String>> listSource;
    private List<Integer> listInfo = new ArrayList<>();
    private List<List<String>> lists = new ArrayList<>();
    private EmojiKeyboardClickCallBack emojiCallBack;


    private int maxIndex = 0;       //展示的页数
    int showMaxLines;               //行数
    int showMaxColumns;             //列数
    private int pageMaxCount;       //每个页面最多展示的emoji数量 此处不包括最后一个预留的删除
    private int maxViewWidth;       //页面宽度
    private int emojiSize;          //字体大小

    public List<Integer> getListInfo() {
        return listInfo;
    }

    public EmojiPagerAdapter(Context context, List<List<String>> listSource, int maxViewWidth, int showMaxLines, int showMaxColumns, int emojiSize) {
        this.context = context;
        this.listSource = listSource;
        this.maxViewWidth = maxViewWidth;
        this.emojiSize = emojiSize;
        this.showMaxLines = showMaxLines;
        this.showMaxColumns = showMaxColumns;
        this.pageMaxCount = showMaxLines * showMaxColumns - 1;
        initList();
    }

    public void setEmojiCallBack(EmojiKeyboardClickCallBack emojiCallBack) {
        this.emojiCallBack = emojiCallBack;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    /**
     * 根据每个页面展示效果,序列化lists
     */
    private void initList() {
        for (List<String> ignored : listSource) {
            listInfo.add(1);
        }

        lists = listSource;

    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void setEmojiOnClick(EmojiKeyboardClickCallBack emojiOnClick) {
        this.emojiCallBack = emojiOnClick;
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        List<String> list = lists.get(position);

        RecyclerView emojiRecyclerView = new RecyclerView(context);
        emojiRecyclerView.setPadding(0,0,0, ScreenUtil.dip2px(context,40));
        emojiRecyclerView.setClipToPadding(false);
        EmojiRecyclerAdapter emojiRecyclerAdapter = new EmojiRecyclerAdapter(list);
        emojiRecyclerView.setLayoutManager(new GridLayoutManager(context,7));
        emojiRecyclerAdapter.setEmojiCallBack(emojiCallBack);
        emojiRecyclerView.setAdapter(emojiRecyclerAdapter);
        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(emojiRecyclerView);

        return emojiRecyclerView;
    }

}
