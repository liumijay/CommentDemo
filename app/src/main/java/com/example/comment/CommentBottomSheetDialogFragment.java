package com.example.comment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.example.comment.util.ScreenUtil;

import java.util.ArrayList;
import java.util.List;


public class CommentBottomSheetDialogFragment extends BottomSheetDialogFragment implements CommentAdapter.OnCommentItemClickListener,
        InputCommentView.CommentSendCallBack {
    private CommentAdapter commentAdapter;
    private RecyclerView commentRecyclerView;
    private InputCommentView commentView;
    private int replyPosition;
    private Activity mActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //给dialog设置主题为透明背景 不然会有默认的白色背景
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.CustomBottomSheetDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 在这里将view的高度设置为精确高度，即可屏蔽向上滑动不占全屏的手势。
        //如果不设置高度的话 会默认向上滑动时dialog覆盖全屏
        final View contentView = inflater.inflate(R.layout.dialog_comment, container, false);
        contentView.post(new Runnable() {
            @Override
            public void run() {
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(getDialog().findViewById(R.id.design_bottom_sheet));
                int height = (int) (ScreenUtil.getScreenHeight(mActivity) * 0.8);
                behavior.setPeekHeight(height);
            }
        });
        contentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) ((ScreenUtil.getScreenHeight(mActivity) * 0.8))));
        contentView.findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCommentPopupWindow();
            }
        });
        return contentView;
    }

    private void initView() {

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        commentRecyclerView = view.findViewById(R.id.rv);
        commentView = view.findViewById(R.id.comment_view);
        commentView.init(mActivity.getWindow());
        commentView.setCommentSendCallBack(this);
        commentAdapter = new CommentAdapter(getContext(), getData());
        commentAdapter.setCommentItemClickListener(this);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        commentRecyclerView.setAdapter(commentAdapter);
        initView();
    }

    private List<CommentBean> getData() {
        List<CommentBean> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            List<CommentBean> childList = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                childList.add(new CommentBean("child-" + i + "--" + j, 1, false, null));
            }
            list.add(new CommentBean("parent-" + i, 0, true, childList));
        }
        for (int i = 0; i < 5; i++) {

            list.add(new CommentBean("parent-" + i, 0, false, null));
        }
        for (int i = 0; i < 5; i++) {
            List<CommentBean> childList = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                childList.add(new CommentBean("child-" + i + "--" + j, 1, false, null));
            }
            list.add(new CommentBean("parent-" + i, 0, false, childList));
        }
        return list;
    }


    private void showReplyPopupWindow(CommentBean commentBean) {
        commentView.setCommentHint(commentBean.getTitle());
        commentView.setFlag(InputCommentView.REPLY_FLAG);
        commentView.showInputPopupWindow();
    }

    private void showCommentPopupWindow() {
        commentView.setFlag(InputCommentView.COMMENT_FLAG);
        commentView.showInputPopupWindow();
    }


    /**
     * 如果想要点击外部消失的话 重写此方法
     */
    @Override
    public @NonNull
    Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        //设置点击外部可消失
        dialog.setCanceledOnTouchOutside(true);
        //设置使软键盘弹出的时候dialog不会被顶起
        Window win = dialog.getWindow();
        if (win!=null){
            win.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
            //这里设置dialog的进出动画
            win.setWindowAnimations(R.style.DialogBottomAnim);
        }

        return dialog;
    }


    @Override
    public void onItemClick(CommentAdapter adapter, CommentBean bean, int pos) {
        switch (bean.getType()) {
            case CommentAdapter.CHILD_TYPE:
            case CommentAdapter.PARENT_TYPE:
                replyPosition = pos;
                showReplyPopupWindow(bean);
                break;
            case CommentAdapter.CHILD_LOADMORE_TYPE:
                List<CommentBean> childList = new ArrayList<>();
                for (int j = 0; j < 3; j++) {
                    childList.add(new CommentBean("more -- child" + "--" + j, 1, false, null));
                }
                commentAdapter.addMoreChildItems(pos, childList);
                break;
            case CommentAdapter.PARENT_LOADMORE_TYPE:
                List<CommentBean> parentList = new ArrayList<>();
                List<CommentBean> withChildList = new ArrayList<>();
                for (int i = 0; i < 2; i++) {
                    for (int j = 0; j < 3; j++) {
                        withChildList.add(new CommentBean("child-" + i + "--" + j, 1, false, null));
                    }
                    parentList.add(new CommentBean("more--parent-" + i, 0, false, withChildList));
                }
                commentAdapter.addMoreParentItems(parentList);
                break;

        }
    }

    @Override
    public void commentText(String comment, int flag) {
        if (flag == InputCommentView.COMMENT_FLAG) {
            CommentBean bean = new CommentBean("用户A", CommentAdapter.PARENT_TYPE, false, null);
            bean.setContent(comment);
            commentAdapter.addMoreParentItem(bean);
            commentView.dismiss();
            commentRecyclerView.scrollToPosition(commentAdapter.getItemCount() - 1);
        } else if (flag == InputCommentView.REPLY_FLAG) {
            CommentBean bean = new CommentBean("用户A", CommentAdapter.CHILD_TYPE, false, null);
            bean.setContent(comment);
            commentAdapter.addMoreChildItem(replyPosition, bean);
            commentView.dismiss();
        }
    }
}