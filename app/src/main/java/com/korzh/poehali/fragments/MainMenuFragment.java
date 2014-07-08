package com.korzh.poehali.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.korzh.poehali.R;
import com.korzh.poehali.activities.MapView;
import com.korzh.poehali.activities.Profile;

/**
 * Created by vladimir on 7/7/2014.
 */
public class MainMenuFragment extends Fragment{
    public void MainMenuFragment(){};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_menu, container, false);

        rootView.findViewById(R.id.relMainMenuMap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), MapView.class);
                startActivity(i);
            }
        });

        rootView.findViewById(R.id.relMainMenuOrders).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        rootView.findViewById(R.id.relMainMenuProfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), Profile.class);
                startActivity(i);
            }
        });

        rootView.findViewById(R.id.relMainMenuSettings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        return rootView;
    }
}
