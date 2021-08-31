package com.example.comment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.BaseCommentViewHolder> {
    public static final int PARENT_TYPE = 0;
    public static final int CHILD_TYPE = 1;
    public static final int CHILD_LOADMORE_TYPE = 2;
    public static final int PARENT_LOADMORE_TYPE = 3;

    private List<CommentBean> items;
    private OnCommentItemClickListener commentItemClickListener;
    private Context context;

    public CommentAdapter(Context context, List<CommentBean> items) {
        this.context = context;
        this.items = new ArrayList<>();
        for (CommentBean bean : items) {
            this.items.add(bean);
            if (bean.getChilds() != null) {
                this.items.addAll(bean.getChilds());
                if (bean.isHasMore()) {
                    // 添加子评论的加载更多
                    this.items.add(new CommentBean("", CHILD_LOADMORE_TYPE, false, null));
                }
            }
        }
        // 添加父评论的更多加载
        this.items.add(new CommentBean("", PARENT_LOADMORE_TYPE, false, null));
    }

    public void addMoreChildItem(int position, CommentBean bean) {
        CommentBean replyBean = items.get(position);
        if (replyBean.getType() == CHILD_TYPE) {
            bean.setReplySuffix(context.getString(R.string.reply_user_suffix, replyBean.getTitle()));
        }
        this.items.add(position + 1, bean);
        notifyItemRangeInserted(position + 1, 1);
        notifyDataSetChanged();
    }

    public void addMoreChildItems(int position, List<CommentBean> list) {
        this.items.addAll(position, list);
        notifyItemRangeInserted(position, list.size());
        notifyItemRangeChanged(position, list.size() + 1);
    }

    public void addMoreParentItem(CommentBean bean) {
        int position = getItemCount() - 1;
        items.remove(position);
        // 移除项目的时候也添加一次notify，防止IndexOutSize的问题
        notifyItemRemoved(position);
        this.items.add(bean);
        if (bean.getChilds() != null) {
            this.items.addAll(bean.getChilds());
            if (bean.isHasMore()) {
                // 添加子评论的加载更多
                this.items.add(new CommentBean("", CHILD_LOADMORE_TYPE, false, null));
            }
        }
        // 添加父评论的更多加载
        items.add(new CommentBean("", PARENT_LOADMORE_TYPE, false, null));
        int itemCount = items.size() - position;
        notifyItemRangeInserted(position, itemCount);
        notifyItemRangeChanged(position, itemCount);
    }

    public void addMoreParentItems(List<CommentBean> list) {
        int position = getItemCount() - 1;
        items.remove(position);
        notifyItemRemoved(position);
        for (CommentBean bean : list) {
            this.items.add(bean);
            if (bean.getChilds() != null) {
                this.items.addAll(bean.getChilds());
                if (bean.isHasMore()) {
                    // 添加子评论的加载更多
                    this.items.add(new CommentBean("", CHILD_LOADMORE_TYPE, false, null));
                }
            }
        }
        // 添加父评论的更多加载
        items.add(new CommentBean("", PARENT_LOADMORE_TYPE, false, null));
        int itemCount = items.size() - position;
        notifyItemRangeInserted(position, itemCount);
        notifyItemRangeChanged(position, itemCount + 1);
    }

    public void setCommentItemClickListener(OnCommentItemClickListener commentItemClickListener) {
        this.commentItemClickListener = commentItemClickListener;
    }

    @NonNull
    @Override
    public BaseCommentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case PARENT_TYPE:
                return new ParentViewHolder(LayoutInflater.from(viewGroup.getContext()), viewGroup);
            case CHILD_TYPE:
                return new ChildViewHolder(LayoutInflater.from(viewGroup.getContext()), viewGroup);
            case CHILD_LOADMORE_TYPE:
                return new ChildLoadMoreHolder(LayoutInflater.from(viewGroup.getContext()), viewGroup);
            default:
                return new ParentLoadMoreHolder(LayoutInflater.from(viewGroup.getContext()), viewGroup);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseCommentViewHolder viewHolder, int pos) {
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.BaseCommentViewHolder holder, int position, @NonNull  List<Object> payloads) {
      if (payloads.isEmpty()){
          holder.bindData(position);
      }else {
          int type= (int) payloads.get(0);// 刷新哪个部分 标志位
          if (type == R.id.comment_love){
              holder.updateLoveIcon(position);
          }
      }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position != items.size() && position < items.size()) {
            return items.get(position).getType();
        }
        return PARENT_LOADMORE_TYPE;
    }

    private class ParentViewHolder extends BaseCommentViewHolder {

        ParentViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_comment_parent, parent, false));
        }
    }

    private class ParentLoadMoreHolder extends BaseCommentViewHolder {

        ParentLoadMoreHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_comment_parent_loadmore, parent, false));
        }
    }

    private class ChildViewHolder extends BaseCommentViewHolder {

        ChildViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_comment_child, parent, false));
        }
    }

    private class ChildLoadMoreHolder extends BaseCommentViewHolder {

        ChildLoadMoreHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_comment_child_loadmore, parent, false));
        }
    }

    class BaseCommentViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView content;
        TextView replySuffix;
        ImageView commentLove;

        BaseCommentViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.comment_name);
            content = itemView.findViewById(R.id.comment_content);
            replySuffix = itemView.findViewById(R.id.comment_reply_suffix);
            commentLove = itemView.findViewById(R.id.comment_love);
        }

        void bindData(final int pos) {
            final CommentBean commentBean = items.get(pos);

            if (name != null) {
                name.setText(commentBean.getTitle());
            }
            if (content != null) {
                content.setText(commentBean.getContent());
            }
            if (replySuffix != null) {
                replySuffix.setText(commentBean.getReplySuffix());
            }
            if (commentLove != null) {
                commentLove.setImageResource(commentBean.isCommentLove()?R.drawable.ic_love_select:R.drawable.ic_love_normal);
                commentLove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        commentBean.toggleCommentLove();
                        notifyItemChanged(pos,R.id.comment_love);
                    }
                });
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (commentItemClickListener != null) {
                        commentItemClickListener.onItemClick(CommentAdapter.this, items.get(pos), pos);
                    }
                }
            });
        }

        void updateLoveIcon(int pos){
            CommentBean commentBean = items.get(pos);
            commentLove.setImageResource(commentBean.isCommentLove()?R.drawable.ic_love_select:R.drawable.ic_love_normal);
        }
    }

    interface OnCommentItemClickListener {
        void onItemClick(CommentAdapter adapter, CommentBean bean, int pos);
    }

}
