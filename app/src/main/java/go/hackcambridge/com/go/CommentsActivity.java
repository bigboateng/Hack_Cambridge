package go.hackcambridge.com.go;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


import java.util.ArrayList;

public class CommentsActivity extends AppCompatActivity {
    private final String TAG = CLogTag.makeLogTag(CommentsActivity.class);

    RecyclerView mRecyclerView;
    ImageView mSend;
    private EditText mMessage;
    CommentsAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        final String channeId = getIntent().getStringExtra("comment-id");
        Log.d(TAG, "ID = " + channeId);
        mRecyclerView = (RecyclerView)findViewById(R.id.chat_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new CommentsAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mSend = (ImageView)findViewById(R.id.btn_send);
        mMessage = (EditText)findViewById(R.id.message_text);
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mMessage.getText())) {
                    mAdapter.addComment(mMessage.getText().toString());
                }
            }
        });
    }
}
