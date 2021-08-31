package com.example.comment.emojikeyboard;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.LinearLayout;


import com.example.comment.R;
import com.example.comment.util.EmojiRegexUtil;
import com.example.comment.util.ScreenUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Emoji输入选择器
 */
public class EmojiKeyboard extends LinearLayout {

    private Context context;

    private LinearLayout linearLayout_emoji;
    private ViewPager viewpager_emojikeyboard;
    private RecyclerView recycleview_emoji_class;
    private BottomClassAdapter bottomClassAdapter;
    private EditText editText;
    private EmojiPagerAdapter emojiAdapter;

    private List<List<String>> lists;           //数据源

    private List<Drawable> tips = new ArrayList<>();    //底部图标信息
    private List<Integer> listInfo = new ArrayList<>(); //输入器分页情况

    int maxLinex = 3;       //行数
    int maxColumns = 7;    //列数
    private int emojiSize = 26; //字体大小
    private int indicatorPadding = 10;  //底部指示器距离
    private int itemIndex = 0;          //当前选择页面
    private int minItemIndex;           //当前条目页面最小位置
    private int maxItemIndex;           //当前条目页面最大位置
    private int maxViewWidth;           //页面宽度


    public EmojiKeyboard(Context context) {
        super(context);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.emoji_keyobard, this);

    }

    public EmojiKeyboard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.emoji_keyobard, this);

    }

    public void setLists(List<List<String>> lists) {
        this.lists = lists;
    }

    public void setTips(List<Drawable> tips) {
        this.tips = tips;
    }

    public void setEditText(EditText editText) {
        this.editText = editText;
    }

    boolean init = true;

    public void init() {
        initView();
    }


    public void setEmojiSize(int emojiSize) {
        this.emojiSize = emojiSize;
    }

    public void setIndicatorPadding(int indicatorPadding) {
        this.indicatorPadding = indicatorPadding;
    }

    public void setMaxLines(int maxLinex) {
        this.maxLinex = maxLinex;
    }

    public void setMaxColumns(int maxColumns) {
        this.maxColumns = maxColumns;
    }

    public void initView() {
        //获取根布局对象
        linearLayout_emoji = (LinearLayout) findViewById(R.id.linearLayout_emoji);
        viewpager_emojikeyboard = (ViewPager) findViewById(R.id.viewpager_emojikeyboard);
        recycleview_emoji_class = (RecyclerView) findViewById(R.id.recycleview_emoji_class);

        viewpager_emojikeyboard.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {

                if (init) {
                    init = false;
                    maxViewWidth = linearLayout_emoji.getWidth();
                    emojiAdapter = new EmojiPagerAdapter(context, lists, maxViewWidth, maxLinex, maxColumns, emojiSize);
                    //通过构建后的EmojiAdapter获取底部指示器的范围

                    listInfo = emojiAdapter.getListInfo();
                    initEmojiOnClick();

                    viewpager_emojikeyboard.setAdapter(emojiAdapter);

                    minItemIndex = 0;
                    maxItemIndex = listInfo.get(itemIndex);

                    //初始化底部icon
                    initBottomClass();
                    //为ViewPager添加滑动监听
                    initViewPageChangeListener();
                    //设置emoji点击的回调
                }

                viewpager_emojikeyboard.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });

    }

    private void initBottomClass() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recycleview_emoji_class.setLayoutManager(linearLayoutManager);

        if (tips.size() != 0 && listInfo.size() < tips.size()) {
            tips = tips.subList(0, listInfo.size());
        }

        bottomClassAdapter = new BottomClassAdapter(context, tips, listInfo.size());
        bottomClassAdapter.setItemOnClick(new BottomClassAdapter.ItemOnClick() {
            @Override
            public void itemOnClick(int position) {
                clickChangeBottomClass(position);
            }
        });
        recycleview_emoji_class.setAdapter(bottomClassAdapter);
    }

    private void initViewPageChangeListener() {
        viewpager_emojikeyboard.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                //滑动后更新底部指示器
                touchChangeBottomClass(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    /**
     * @param clickItemIndex 点击后更新指示器
     */
    private void clickChangeBottomClass(int clickItemIndex) {
        itemIndex = clickItemIndex;
        maxItemIndex = 0;
        minItemIndex = 0;
        for (int i = 0; i <= itemIndex; i++) {
            maxItemIndex += listInfo.get(i);
        }

        for (int i = 0; i < itemIndex; i++) {
            minItemIndex += listInfo.get(i);
        }


        viewpager_emojikeyboard.setCurrentItem(minItemIndex);
        changeBottomClassIcon();


    }

    /**
     * @param position 滑动后更新底部指示器
     */
    private void touchChangeBottomClass(int position) {
        //判断滑动是否越界
        if (position >= maxItemIndex) {
            itemIndex++;

            maxItemIndex = 0;
            minItemIndex = 0;
            for (int i = 0; i <= itemIndex; i++) {
                maxItemIndex += listInfo.get(i);
            }

            for (int i = 0; i < itemIndex; i++) {
                minItemIndex += listInfo.get(i);
            }

        } else if (position < minItemIndex) {
            itemIndex--;

            maxItemIndex = 0;
            minItemIndex = 0;
            for (int i = 0; i <= itemIndex; i++) {
                maxItemIndex += listInfo.get(i);
            }

            for (int i = 0; i < itemIndex; i++) {
                minItemIndex += listInfo.get(i);
            }

        } else {
            position -= minItemIndex;
        }

        changeBottomClassIcon();
    }

    private void changeBottomClassIcon() {
        bottomClassAdapter.changeBottomItem(itemIndex);


        int firstItem = recycleview_emoji_class.getChildLayoutPosition(recycleview_emoji_class.getChildAt(0));
        int lastItem = recycleview_emoji_class.getChildLayoutPosition(recycleview_emoji_class.getChildAt(recycleview_emoji_class.getChildCount() - 1));


        if (itemIndex <= firstItem || itemIndex >= lastItem) {
            recycleview_emoji_class.smoothScrollToPosition(itemIndex);
        }
    }


    private void initEmojiOnClick() {
        emojiAdapter.setEmojiCallBack(new EmojiKeyboardClickCallBack() {
            @Override
            public void OnClick(String text) {
                int index = editText.getSelectionStart();
                Editable edit = editText.getEditableText();
                //插入你内容
                if (index < 0 || index >= edit.length()) {
                    edit.append(text);
                } else {
                    edit.insert(index, text);
                }
            }
        });
        findViewById(R.id.emoji_delete).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //获取坐标位置及文本内容
                int index = editText.getSelectionStart();
                Editable edit = editText.getEditableText();
                String str = editText.getText().toString();
                if (!str.equals("")) {
                    //只有一个字符
                    if (str.length() < 2) {
                        editText.getText().delete(index - 1, index);
                    } else if (index > 0) {
                        String lastText = str.substring(index - 2, index);
                        //检测最后两个字符是否为一个emoji(emoji可能存在一个字符的情况 需要进行正则校验)
                        if (EmojiRegexUtil.checkEmoji(lastText)) {
                            editText.getText().delete(index - 2, index);
                        } else {
                            editText.getText().delete(index - 1, index);
                        }
                    }

                }
            }
        });
    }

}
