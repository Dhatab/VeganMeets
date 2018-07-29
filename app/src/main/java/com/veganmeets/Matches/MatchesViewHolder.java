package com.veganmeets.Matches;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.veganmeets.Chats.ChatActivity;
import com.veganmeets.R;

public class MatchesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView mMatchId, mMatchName;
    public ImageView mMatchPic;

    public MatchesViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        mMatchId = (TextView) itemView.findViewById(R.id.matches_id);
        mMatchName = (TextView) itemView.findViewById(R.id.matches_name);
        mMatchPic = (ImageView) itemView.findViewById(R.id.matches_pic);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("matchID", mMatchId.getText().toString());
        intent.putExtras(bundle);
        view.getContext().startActivity(intent);
    }
}
