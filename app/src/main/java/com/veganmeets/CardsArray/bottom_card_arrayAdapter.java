package com.veganmeets.CardsArray;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.veganmeets.R;

import java.util.List;

/**
 * Created by User on 7/25/2018.
 */


public class bottom_card_arrayAdapter extends RecyclerView.Adapter<BottomViewHolder> {

    private List<bottom_view_reference> bottomList;
    private Context context;

    public bottom_card_arrayAdapter(List<bottom_view_reference> bottomList, Context context) {
        this.bottomList = bottomList;
        this.context = context;
    }

    @Override
    public BottomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bottom_view, parent, false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(layoutParams);
        BottomViewHolder recycler = new BottomViewHolder((layoutView));

        return recycler;
    }

    @Override
    public void onBindViewHolder(BottomViewHolder holder, int position) {
        holder.bottomName.setText(bottomList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return bottomList.size();
    }
}

    class BottomViewHolder extends RecyclerView.ViewHolder {

        public TextView bottomID, bottomName;


        public BottomViewHolder(View itemView) {
            super(itemView);

            bottomName = (TextView) itemView.findViewById(R.id.bottom_sheet_text);
        }
    }

