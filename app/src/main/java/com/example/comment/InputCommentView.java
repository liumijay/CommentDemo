package com.example.comment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.comment.emojikeyboard.EmojiKeyboard;
import com.example.comment.emojikeyboard.EmojiSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 评论输入的 view
 * Author: liumingjie
 * CreateDate: 2021/8/23 16:49
 */
public class InputCommentView extends FrameLayout implements View.OnClickListener {
    public static final int COMMENT_FLAG = 11;
    public static final int REPLY_FLAG = 12;
    private EditText commentEdit;
    private  int screenHeight;
    private EmojiKeyboard emojiKeyboard;
    private InputMethodManager inputMethodManager;
    private CommentSendCallBack commentSendCallBack;
    private RecyclerView friendRecycler;
    private boolean hadInit;
    private boolean activeHideKeyboard;
    private boolean showNotifyView;
    private boolean notifyAnimating;
    private int flag;  //用来区分是评论视频还是回复用户

    private Animator.AnimatorListener notifyAnimListener;

    public InputCommentView(@NonNull  Context context) {
        super(context);
        initView(context);
    }

    public InputCommentView(@NonNull  Context context, @Nullable  AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public InputCommentView(@NonNull  Context context, @Nullable  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context){
        inflate(context,R.layout.dialog_input,this);

        inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        commentEdit = findViewById(R.id.comment_edt);
        emojiKeyboard = findViewById(R.id.emojiKeyboard);
        initEmojiKeyBoard(context);
        initRecyclerView();
    }

    private void initRecyclerView() {
        friendRecycler = findViewById(R.id.comment_friend);
        friendRecycler.setLayoutManager(new LinearLayoutManager(friendRecycler.getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        CommentFriendAdapter friendAdapter = new CommentFriendAdapter();
        friendAdapter.setFriendClickListener(new CommentFriendAdapter.OnFriendClickListener() {
            @Override
            public void onFriendClick(String name) {
                SpannableStringBuilder originalBuilder = (SpannableStringBuilder) commentEdit.getText();
                String friendStr = friendRecycler.getContext().getString(R.string.notify_friend, name);
                SpannableStringBuilder friendBuilder = new SpannableStringBuilder(friendStr);
                friendBuilder.setSpan(new ForegroundColorSpan(Color.YELLOW), 0, friendStr.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                originalBuilder.append(friendBuilder);
                commentEdit.setText(originalBuilder);
                commentEdit.setSelection(originalBuilder.length());
                startNotifyViewOutAnim();
            }
        });
        friendRecycler.setAdapter(friendAdapter);

    }


    private void initEmojiKeyBoard(Context context) {
        List<Drawable> tips = new ArrayList<>();
        tips.add(ContextCompat.getDrawable(context, R.drawable.icon_emoji_1));
        tips.add(ContextCompat.getDrawable(context, R.drawable.icon_emoji_2));
        tips.add(ContextCompat.getDrawable(context, R.drawable.icon_emoji_3));
        tips.add(ContextCompat.getDrawable(context, R.drawable.icon_emoji_4));
        tips.add(ContextCompat.getDrawable(context, R.drawable.icon_emoji_6));
        //绑定EditText
        emojiKeyboard.setEditText(commentEdit);
        //设置行数 默认为3
        emojiKeyboard.setMaxLines(5);
        //设置列数 默认为7
        emojiKeyboard.setMaxColumns(7);
        //设置底部图标
        emojiKeyboard.setTips(tips);
        //设置图标数据源
        emojiKeyboard.setLists(EmojiSource.getLists());
        //设置指示器距底部边界
        emojiKeyboard.setIndicatorPadding(3);
        //初始化需要
        emojiKeyboard.init();
    }

    public void init(final Window window) {
        Rect r = new Rect();
        // getWindowVisibleDisplayFrame()会返回窗口的可见区域高度
        window.getDecorView().getWindowVisibleDisplayFrame(r);
        screenHeight = r.height();
        window.getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                // getWindowVisibleDisplayFrame()会返回窗口的可见区域高度
                window.getDecorView().getWindowVisibleDisplayFrame(r);
                final int heightDiff = screenHeight - r.height();
                if (heightDiff > screenHeight * 0.3) {
                    activeHideKeyboard = false;
                    if (!hadInit) {
                        emojiKeyboard.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ViewGroup.LayoutParams emojiKeyboardLayoutParams = emojiKeyboard.getLayoutParams();
                                emojiKeyboardLayoutParams.height = heightDiff;
                                emojiKeyboard.setLayoutParams(emojiKeyboardLayoutParams);
                            }
                        }, 200);
                        hadInit = true;
                    }
                } else {
                    if (!activeHideKeyboard) {
                        dismiss();
                    }
                }
            }
        });
        TextView notifyIcon = findViewById(R.id.notify_icon);
        notifyIcon.setOnClickListener(this);
        TextView emojiIcon = findViewById(R.id.emoji_icon);
        emojiIcon.setOnClickListener(this);

        TextView sendBtn = findViewById(R.id.send_tv);
        sendBtn.setOnClickListener(this);

        setOnClickListener(this);
        notifyAnimListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                notifyAnimating = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    private void startNotifyViewInAnim() {
        notifyAnimating = true;
        showNotifyView = true;
        ObjectAnimator notifyInAnim =  ObjectAnimator.ofFloat(friendRecycler, "translationY",
                0, -friendRecycler.getHeight())
                .setDuration(200);
        notifyInAnim.addListener(notifyAnimListener);
        notifyInAnim.start();
    }

    private void startNotifyViewOutAnim() {
        notifyAnimating = true;
        showNotifyView = false;
        ObjectAnimator notifyOutAnim = ObjectAnimator.ofFloat(friendRecycler, "translationY",
                -friendRecycler.getHeight(),0)
                .setDuration(200);
        notifyOutAnim.addListener(notifyAnimListener);
        notifyOutAnim.start();
    }


    private void showSoftInput() {
        emojiKeyboard.setVisibility(View.INVISIBLE);
        commentEdit.requestFocus();
        commentEdit.postDelayed(new Runnable() {
            @Override
            public void run() {
                inputMethodManager.showSoftInput(commentEdit, 0);
            }
        }, 100);
    }

    private void hideSoftInput() {
        activeHideKeyboard = true;
        inputMethodManager.hideSoftInputFromWindow(commentEdit.getWindowToken(), 0);
    }

    public void setCommentSendCallBack(CommentSendCallBack commentSendCallBack) {
        this.commentSendCallBack = commentSendCallBack;
    }

    public void setCommentHint(String text) {
        commentEdit.setHint(commentEdit.getContext().getString(R.string.reply_hint, text));
    }

    @Override
    public void onClick(View v) {
        //android studio升级到4.0以上后不建议用switch
        if (v.getId()==R.id.notify_icon){
            if (!notifyAnimating){
                if (showNotifyView){
                    startNotifyViewOutAnim();
                }else {
                    startNotifyViewInAnim();
                }
            }
        }else  if (v.getId()==R.id.emoji_icon){
            if (emojiKeyboard.isShown()) {
                showSoftInput();
            } else {
                emojiKeyboard.setVisibility(VISIBLE);
                hideSoftInput();
            }
        }else if (v.getId()==R.id.send_tv){
            if (commentSendCallBack != null) {
                commentSendCallBack.commentText(commentEdit.getText().toString(),flag);
            }
        }else {
            dismiss();

        }
    }

    public void dismiss(){
        emojiKeyboard.setVisibility(INVISIBLE);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(this, "translationY", 0,getHeight());
        translationY.setDuration(500);
        translationY.start();
        hideSoftInput();
    }

    public void showInputPopupWindow() {
        emojiKeyboard.setVisibility(VISIBLE);
        setVisibility(VISIBLE);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(this, "translationY", getHeight(),0);
        translationY.setDuration(300);
        translationY.start();
        showSoftInput();
    }

    interface CommentSendCallBack {
        void commentText(String comment,int flag);
    }
}
