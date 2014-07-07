package com.korzh.poehali.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.korzh.poehali.R;

import java.util.ArrayList;

/**
 * Created by vladimir on 7/4/2014.
 */
    public class MenuDrawerAdapter extends ArrayAdapter<MenuItem> {
        Context context;
        private ArrayList<MenuItem> values;

        public MenuDrawerAdapter(Context context, int textViewResourceId, ArrayList<MenuItem> objects) {
            super(context, textViewResourceId, objects);
            this.values = objects;

        }


    public View getView(int position, View convertView, ViewGroup parent){

        // assign the view we are converting to a local variable
        View v = convertView;

        // first check to see if the view is null. if so, we have to inflate it.
        // to inflate it basically means to render, or show, the view.
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.row_of_drawer, null);
        }

        MenuItem i = values.get(position);

        if (i != null) {
            TextView title = (TextView) v.findViewById(R.id.txtLabel);
            ImageView icon = (ImageView) v.findViewById(R.id.imgIcon);

            if (title != null){
                title.setText(i.getTitle());
            }
            if (icon != null){
                icon.setImageResource(i.getIcon());
            }
        }

        return v;
    }
}
