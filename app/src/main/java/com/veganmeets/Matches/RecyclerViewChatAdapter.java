package com.veganmeets.Matches;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.veganmeets.R;

import java.util.List;

/**
 * Created by User on 7/28/2018.
 */

public class RecyclerViewChatAdapter extends RecyclerView.Adapter<RecyclerChatViewHolder> {

    private List<RecyclerViewChatReference> recyclerviewChatList;
    private Context context;

    public RecyclerViewChatAdapter(List<RecyclerViewChatReference> recyclerviewChatList, Context context){
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
    public void onBindViewHolder(RecyclerChatViewHolder holder, int position) {
            holder.mMatchId2.setText(recyclerviewChatList.get(position).getUserID());
            holder.mMatchName2.setText(recyclerviewChatList.get(position).getUserName());
            holder.mMatchChatID.setText(recyclerviewChatList.get(position).getChatID());

            //checks if user pic is default, if it is default it wont change, if not then it will update to users pic
            if(!recyclerviewChatList.get(position).getUserProfilePic().equals("default")) {
                Glide.with(context).load(recyclerviewChatList.get(position).getUserProfilePic()).into(holder.mMatchPic2);
            }
    }

    @Override
    public int getItemCount() {
        return recyclerviewChatList.size();
    }
}

