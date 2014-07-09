package com.korzh.poehali.fragments;

import android.content.Context;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.korzh.poehali.R;
import com.nhaarman.listviewanimations.itemmanipulation.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.SwipeDismissAdapter;
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingLeftInAnimationAdapter;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by vladimir on 7/4/2014.
 */
public class OrdersFragment extends Fragment implements OnDismissCallback {
    private MyAdapter mAdapter = null;
    private ListView mListView = null;


    public OrdersFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_orders, container, false);

        mListView = (ListView) rootView.findViewById(R.id.listOrders);

        mAdapter = new MyAdapter(getActivity(), getItems());

        final SwipeDismissAdapter adapter = new SwipeDismissAdapter(mAdapter, this);
        adapter.setAbsListView(mListView);
        mListView.setAdapter(adapter);

        AnimationAdapter animAdapter = new SwingLeftInAnimationAdapter(mAdapter);
        animAdapter.setAbsListView(mListView);
        mListView.setAdapter(animAdapter);



        // send order every so often
        final Handler h = new Handler();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                mAdapter.add(1);
                h.postDelayed(this,5000);
            }
        };
        h.postDelayed(r,5000);


        final Handler radarHandler = new Handler();

        final Runnable rotate = new Runnable() {
            float degrees = 0.f;
            @Override
            public void run() {
                ImageView radar = (ImageView) rootView.findViewById(R.id.radarArrow);
                Matrix matrix = new Matrix();
                radar.setScaleType(ImageView.ScaleType.MATRIX);
                matrix.postRotate(degrees, radar.getDrawable().getBounds().width() / 2f, radar.getDrawable().getBounds().height() / 2f);
                radar.setImageMatrix(matrix);
                degrees +=1;
                radarHandler.postDelayed(this,50);
            }
        };
        radarHandler.postDelayed(rotate, 50);
        return rootView;
    }

    @Override
    public void onDismiss(final AbsListView listView, final int[] reverseSortedPositions) {
        for (int position : reverseSortedPositions) {
            mAdapter.remove(position);
        }
        Toast.makeText(getActivity(), "Removed positions: " + Arrays.toString(reverseSortedPositions), Toast.LENGTH_SHORT).show();
    }

    public static ArrayList<Integer> getItems() {
        ArrayList<Integer> items = new ArrayList<Integer>();
        for (int i = 0; i < 4; i++) {
            items.add(i);
        }
        return items;
    }

    public void onButtonClick(View view){

    }


    private static class MyAdapter extends com.nhaarman.listviewanimations.ArrayAdapter<Integer> {

        private final Context mContext;

        public MyAdapter(final Context context, final ArrayList<Integer> items) {
            super(items);
            mContext = context;
        }

        @Override
        public long getItemId(final int position) {
            return getItem(position).hashCode();
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            View tv = convertView;
//            if (tv == null) {
                tv = LayoutInflater.from(mContext).inflate(R.layout.single_order, parent, false);
//            }
            return tv;
        }
    }

}

