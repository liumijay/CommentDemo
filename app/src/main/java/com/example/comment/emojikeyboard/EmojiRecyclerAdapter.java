package com.example.comment.emojikeyboard;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.example.comment.R;

import java.util.List;

public class EmojiRecyclerAdapter extends RecyclerView.Adapter<EmojiRecyclerAdapter.EmojiViewHolder> {

    private List<String> emojiItems;
    private EmojiKeyboardClickCallBack emojiCallBack;

    public EmojiRecyclerAdapter(List<String> emojiItems) {
        this.emojiItems = emojiItems;
    }

    @NonNull
    @Override
    public EmojiViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        EmojiTextView textView = new EmojiTextView(viewGroup.getContext());
        textView.setGravity(Gravity.CENTER);
        textView.setBackground(ContextCompat.getDrawable(viewGroup.getContext(), R.drawable.user_select_bg));
        textView.setTextSize(26);
        textView.setGravity(Gravity.CENTER);
        return new EmojiViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(@NonNull EmojiRecyclerAdapter.EmojiViewHolder emojiViewHolder, int i) {
        final String emoji = emojiItems.get(i);
        emojiViewHolder.bindData(emojiItems.get(i), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emojiCallBack!=null){
                    emojiCallBack.OnClick(emoji);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return emojiItems.size();
    }

    public void setEmojiCallBack(EmojiKeyboardClickCallBack emojiCallBack) {
        this.emojiCallBack = emojiCallBack;
    }

    protected static class EmojiViewHolder extends RecyclerView.ViewHolder {
        private EmojiTextView emojiTextView;

        public EmojiViewHolder(@NonNull View itemView) {
            super(itemView);
            emojiTextView = (EmojiTextView) itemView;
        }

        public void bindData(String emoji, View.OnClickListener listener) {
            emojiTextView.setText(emoji);
            emojiTextView.setOnClickListener(listener);
        }
    }
}
