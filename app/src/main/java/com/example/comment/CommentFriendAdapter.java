package com.example.comment;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CommentFriendAdapter extends RecyclerView.Adapter<CommentFriendAdapter.FriendViewHolder> {
    List<String> items;
    private OnFriendClickListener friendClickListener;

    public CommentFriendAdapter() {
        items = new ArrayList<>();
        items.add("用户1");
        items.add("用户2");
        items.add("用户3");
        items.add("用户4");
        items.add("用户5");
        items.add("用户6");
        items.add("用户7");
        items.add("用户8");
        items.add("用户9");
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_comment_friend, viewGroup,false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder viewHolder, int i) {
        viewHolder.bindData(i);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setFriendClickListener(OnFriendClickListener friendClickListener) {
        this.friendClickListener = friendClickListener;
    }

     class FriendViewHolder extends RecyclerView.ViewHolder {
        TextView nameTv;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.friend_name);
        }

        void bindData(int pos) {
            final String friendName = items.get(pos);
            nameTv.setText(friendName);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (friendClickListener!=null){
                        friendClickListener.onFriendClick(friendName);
                    }
                }
            });
        }
    }

    interface OnFriendClickListener{
        void onFriendClick(String name);
    }


}
