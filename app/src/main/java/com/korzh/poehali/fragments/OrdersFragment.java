package com.korzh.poehali.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.korzh.poehali.R;

/**
 * Created by vladimir on 7/4/2014.
 */
public class OrdersFragment extends Fragment{
    public OrdersFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_orders, container, false);

        return rootView;
    }



    @Override
    public void onResume(){
        super.onResume();

    }
}

