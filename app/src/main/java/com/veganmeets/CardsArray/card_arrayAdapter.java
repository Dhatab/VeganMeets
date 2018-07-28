package com.veganmeets.CardsArray;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.veganmeets.R;

import java.util.List;

/**
 * Created by User on 7/25/2018.
 */

public class card_arrayAdapter extends ArrayAdapter<cards_reference>{
    Context context;

    public card_arrayAdapter(Context context, int resourceId, List<cards_reference> items){
        super(context, resourceId, items);
    }

    public View getView(int position, View holder, ViewGroup parent) {
        cards_reference card_item = getItem(position);

        if (holder == null) {
            holder = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }

        TextView card_name = (TextView) holder.findViewById(R.id.card_name);
        ImageView card_image = (ImageView) holder.findViewById(R.id.card_image);

        card_name.setText(card_item.getName());
        switch (card_item.getProfilePicURL()) {
            case "default":
                Glide.with(holder.getContext()).load(R.mipmap.ic_default_profile).into(card_image);
                break;
            default:
                Glide.clear(card_image);
                Glide.with(holder.getContext()).load(card_item.getProfilePicURL()).into(card_image);
                break;
        }
        return holder;
    }
}
