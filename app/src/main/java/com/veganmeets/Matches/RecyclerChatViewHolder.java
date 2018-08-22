package com.veganmeets.Matches;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.veganmeets.Chats.ChatActivity;
import com.veganmeets.R;

public class RecyclerChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView mMatchId2, mMatchName2, mMatchChatID;
    public ImageView mMatchPic2;

    public RecyclerChatViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        mMatchId2 = (TextView) itemView.findViewById(R.id.matches_id2);
        mMatchName2 = (TextView) itemView.findViewById(R.id.matches_name2);
        mMatchChatID = (TextView) itemView.findViewById(R.id.matches_chat2);
        mMatchPic2 = (ImageView) itemView.findViewById(R.id.matches_pic2);

    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("matchID", mMatchId2.getText().toString());
        intent.putExtras(bundle);
        view.getContext().startActivity(intent);
    }
}
