package go.hackcambridge.com.go;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;



import java.util.ArrayList;

import go.hackcambridge.com.go.models.ChatMessage;

/**
 * Created by boatengyeboah on 29/01/2017.
 */

public class CommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<String> mComments;

    public CommentsAdapter(){
        mComments = new ArrayList<>();
        mComments.add("Thanks for the tour!");
        mComments.add("Very entertaining tour, thank you! - Tony");
    }


    public void addComment(String comment) {
        mComments.add(comment);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_item, parent, false);
        return new ChatViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String c =  mComments.get(position);
        ChatViewHolder vh = (ChatViewHolder)holder;
        vh.mBody.setText(c);
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    private class ChatViewHolder extends RecyclerView.ViewHolder {
        private TextView mBody;
        public ChatViewHolder(View itemView) {
            super(itemView);
            mBody = (TextView)itemView.findViewById(R.id.message);
        }
    }
}
