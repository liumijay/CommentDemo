package com.example.comment;

import android.app.Application;
import android.support.text.emoji.EmojiCompat;
import android.support.text.emoji.bundled.BundledEmojiCompatConfig;

public class CommentApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        EmojiCompat.Config config = new BundledEmojiCompatConfig(this);
        EmojiCompat.init(config);
    }
}
