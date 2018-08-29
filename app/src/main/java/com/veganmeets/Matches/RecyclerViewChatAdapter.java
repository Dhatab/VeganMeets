package com.veganmeets.Matches;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.veganmeets.Chats.ChatActivity;
import com.veganmeets.MainFragments.Fragment_MatchChats;
import com.veganmeets.MainFragments.MainFragmentActivity;
import com.veganmeets.R;

import java.util.List;

/**
 * Created by User on 7/28/2018.
 */

public class RecyclerViewChatAdapter extends RecyclerView.Adapter<RecyclerViewChatAdapter.RecyclerChatViewHolder> {

    private List<RecyclerViewChatReference> recyclerviewChatList;
    private Context context;

    public RecyclerViewChatAdapter(List<RecyclerViewChatReference> recyclerviewChatList, Context context) {
        this.recyclerviewChatList = recyclerviewChatList;
        this.context = context;
    }

    @Override
    public RecyclerChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerviewchat, parent, false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(layoutParams);
        RecyclerChatViewHolder recycler = new RecyclerChatViewHolder((layoutView));

        return recycler;
    }

    @Override
    public void onBindViewHolder(final RecyclerChatViewHolder holder, final int position) {
        holder.mMatchId2.setText(recyclerviewChatList.get(position).getUserID());
        holder.mMatchName2.setText(recyclerviewChatList.get(position).getUserName());
        holder.mMatchChatID.setText(recyclerviewChatList.get(position).getChatID());

        //checks if user pic is default, if it is default it wont change, if not then it will update to users pic
        if (!recyclerviewChatList.get(position).getUserProfilePic().equals("default")) {
            Glide.with(context).load(recyclerviewChatList.get(position).getUserProfilePic()).into(holder.mMatchPic2);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("matchID", holder.mMatchId2.getText().toString());
                intent.putExtras(bundle);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recyclerviewChatList.size();
    }


    public class RecyclerChatViewHolder extends RecyclerView.ViewHolder {

        public TextView mMatchId2, mMatchName2, mMatchChatID;
        public ImageView mMatchPic2;

        public RecyclerChatViewHolder(View itemView) {
            super(itemView);

            mMatchId2 = (TextView) itemView.findViewById(R.id.matches_id2);
            mMatchName2 = (TextView) itemView.findViewById(R.id.matches_name2);
            mMatchChatID = (TextView) itemView.findViewById(R.id.matches_chat2);
            mMatchPic2 = (ImageView) itemView.findViewById(R.id.matches_pic2);

        }
    }



}

