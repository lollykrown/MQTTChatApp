package com.lollykrown.mqttchatapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    // ... constructor and member variables
    Context mContext;
    List<ChatMessage> mMessage;

    public MessageAdapter(Context context, List<ChatMessage> message) {
        mContext = context;
        mMessage = message;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.item_message;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int i) {

        ChatMessage m = mMessage.get(i);
        holder.messageTv.setText(m.getMessage());

    }

    @Override
    public int getItemCount() {
        return mMessage.size();
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {

        //member variable
        TextView messageTv;

        public MessageViewHolder(final View itemView) {
            super(itemView);

            messageTv = itemView.findViewById(R.id.messageTextView);
        }
    }

}
