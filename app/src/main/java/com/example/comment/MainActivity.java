package com.example.comment;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {
private View comment;
    private CommentBottomSheetDialogFragment commentBottomSheetDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        comment =findViewById(R.id.comment_btn);
        commentBottomSheetDialogFragment = new CommentBottomSheetDialogFragment();
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showComment();
            }
        });
    }

    private void showComment(){
        commentBottomSheetDialogFragment.show(getSupportFragmentManager(), "");
    }

}
