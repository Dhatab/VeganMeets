package com.veganmeets.Matches;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.veganmeets.R;

import java.util.List;

/**
 * Created by User on 7/28/2018.
 */

public class MatchesAdapter extends RecyclerView.Adapter<MatchesViewHolder> {

    private List<MatchesReference> matchesList;
    private Context context;

    public MatchesAdapter(List<MatchesReference> matchesList, Context context){
        this.matchesList = matchesList;
        this.context = context;
    }

    @Override
    public MatchesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_matches, null, false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(layoutParams);
        MatchesViewHolder recycler = new MatchesViewHolder((layoutView));

        return recycler;
    }

    @Override
    public void onBindViewHolder(MatchesViewHolder holder, int position) {
        holder.mMatchId.setText(matchesList.get(position).getUserID());
        holder.mMatchName.setText(matchesList.get(position).getUserName());

        //checks if user pic is default, if it is default it wont change, if not then it will update to users pic
        if(!matchesList.get(position).getUserProfilePic().equals("default")) {
            Glide.with(context).load(matchesList.get(position).getUserProfilePic()).into(holder.mMatchPic);
        }
    }

    @Override
    public int getItemCount() {
        return matchesList.size();
    }
}

