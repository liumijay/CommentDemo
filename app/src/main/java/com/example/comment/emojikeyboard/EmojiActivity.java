package com.example.comment.emojikeyboard;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.comment.R;

import java.util.ArrayList;
import java.util.List;

public class EmojiActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emoji);


        final EditText editText = findViewById(R.id.editText);

        //隐藏系统键盘 点击editText的时候可能会会出现闪动情况
        final InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            }
        });

        //底部图标(如不设置则使用默认图标)
        List<Drawable> tips = new ArrayList<>();
        tips.add(ContextCompat.getDrawable(this,R.drawable.icon_emoji_1));
        tips.add(ContextCompat.getDrawable(this,R.drawable.icon_emoji_2));
        tips.add(ContextCompat.getDrawable(this,R.drawable.icon_emoji_3));
        tips.add(ContextCompat.getDrawable(this,R.drawable.icon_emoji_4));
        tips.add(ContextCompat.getDrawable(this,R.drawable.icon_emoji_5));
        tips.add(ContextCompat.getDrawable(this,R.drawable.icon_emoji_6));


        EmojiKeyboard emojiKeyboard = findViewById(R.id.emojiKeyboard);

        //绑定EditText
        emojiKeyboard.setEditText(editText);
        //设置底部图标
        emojiKeyboard.setTips(tips);
        //设置行数 默认为3
        emojiKeyboard.setMaxLines(3);
        //设置列数 默认为7
        emojiKeyboard.setMaxColumns(7);
        //设置图标数据源
        emojiKeyboard.setLists(EmojiSource.getLists());
        //设置指示器距底部边界
        emojiKeyboard.setIndicatorPadding(3);
        //初始化需要
        emojiKeyboard.init();

    }

}
