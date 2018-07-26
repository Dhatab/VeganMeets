package com.veganmeets;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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
        card_image.setImageResource(R.mipmap.ic_launcher);

        return holder;
    }
}
