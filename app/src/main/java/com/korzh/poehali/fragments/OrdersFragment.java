package com.korzh.poehali.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.korzh.poehali.R;
import com.korzh.poehali.common.interfaces.GoogleDirectionsApi;
import com.korzh.poehali.common.network.packets.OrderPacket;
import com.korzh.poehali.common.network.packets.frames.LocationJson;
import com.korzh.poehali.common.network.packets.frames.OrderDetailsJson;
import com.korzh.poehali.common.network.packets.frames.UserJson;
import com.korzh.poehali.common.util.C;
import com.korzh.poehali.common.util.G;
import com.korzh.poehali.dialogs.NewOrder;
import com.nhaarman.listviewanimations.itemmanipulation.OnDismissCallback;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by vladimir on 7/4/2014.
 */
public class OrdersFragment extends Fragment implements OnDismissCallback {
    private MyOrdersAdapter mAdapter = null;
    private ListView mListView = null;
    //private OrdersDispatcherInterface ordersDispatcherInterface = null;
    private ArrayList<OrderPacket> arr = new ArrayList<OrderPacket>();

    public OrdersFragment() {}



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_orders, container, false);

//        mListView = (ListView) rootView.findViewById(R.id.listOrders);
//
//        mAdapter = new MyOrdersAdapter(getActivity(), arr);
//
//        SwipeDismissAdapter adapter = new SwipeDismissAdapter(mAdapter, this);
//        adapter.setAbsListView(mListView);
//        mListView.setAdapter(adapter);
//
//        final AnimationAdapter animAdapter = new SwingLeftInAnimationAdapter(mAdapter);
//        animAdapter.setAbsListView(mListView);
//        mListView.setAdapter(animAdapter);

        final TextView txtProgress = (TextView) rootView.findViewById(R.id.txtProgress);
        SeekBar seekBar = (SeekBar) rootView.findViewById(R.id.seekSearchRange);

        seekBar.setMax(10);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                txtProgress.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(getActivity(),"Changing to "+String.valueOf(seekBar.getProgress()), Toast.LENGTH_LONG).show();
                String exchange = G.getInstance().mqBinding.getTargetExchange();
                Location cityCenter = G.getInstance().mqBinding.getCityCenterFromExchange(exchange);
                Location myLoc = G.getInstance().getLastKnownLocation();
                ArrayList<String> binds = G.getInstance().mqBinding.getConsumerBindingKeysList(cityCenter, myLoc,seekBar.getProgress());
                G.getInstance().mqBinding.ChangeBinds(binds);
            }
        });

//
//        ordersDispatcherInterface = new OrdersDispatcherInterface();
//
//        ordersDispatcherInterface.setOnNewOrderReceive(new MessageConsumer.OnReceiveMessageHandler() {
//            @Override
//            public void onReceiveMessage(QueueingConsumer.Delivery delivery) {
//                byte[] message = delivery.getBody();
//                String data = new String(message);
//                JSONObject jsonObject = null;
//                try {
//                    jsonObject = new JSONObject(data);
//                } catch (JSONException e) {
//                    U.Log(getClass().getSimpleName(), "Error reading json");
//                }
//                final OrderPacket order = new OrderPacket(jsonObject);
//                U.Log(getClass().getSimpleName(), "New order received: "+order.toString());
//                arr.add(order);
//
//                Handler h = new Handler();
//                Runnable r = new Runnable() {
//                    @Override
//                    public void run() {
//                        arr.remove(order);
//                        mAdapter.notifyDataSetChanged();
//                    }
//                };
//                h.postDelayed(r, 5000);
//
//                animAdapter.setShouldAnimateFromPosition(arr.size()-1);
//                mAdapter.notifyDataSetChanged();
//            }
//        });
//
//        ordersDispatcherInterface.StartOrdersListener();
//        ordersDispatcherInterface.StartRepliesListener();

//        // send order every so often
//        final Handler h = new Handler();
//        Runnable r = new Runnable() {
//            @Override
//            public void run() {
//                mAdapter.add(1);
//                h.postDelayed(this,5000);
//            }
//        };
//        h.postDelayed(r,5000);


        // rotate picture
//        final Handler radarHandler = new Handler();
//        final Runnable rotate = new Runnable() {
//            float degrees = 0.f;
//            @Override
//            public void run() {
//                ImageView radar = (ImageView) rootView.findViewById(R.id.radarArrow);
//                Matrix matrix = new Matrix();
//                radar.setScaleType(ImageView.ScaleType.MATRIX);
//                matrix.postRotate(degrees, radar.getDrawable().getBounds().width() / 2f, radar.getDrawable().getBounds().height() / 2f);
//                radar.setImageMatrix(matrix);
//                degrees +=1;
//                radarHandler.postDelayed(this,50);
//            }
//        };
//        radarHandler.postDelayed(rotate, 50);

        return rootView;
    }

    public void onButtonClick(View view){
        if (view.getId() == R.id.btnSendTestOrder){
            Intent myIntent = new Intent(getActivity(), NewOrder.class);
            startActivityForResult(myIntent, C.REQUEST_CODE_NAVIGATOR_NEW_ROUTE);
        }
    }









    LocationJson userLocationFrameOrigin, userLocationFrameDestination;
    UserJson userFrame;

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == C.REQUEST_CODE_NAVIGATOR_NEW_ROUTE) {

            if (resultCode == Activity.RESULT_OK) {

                final GoogleDirectionsApi gd = new GoogleDirectionsApi(getActivity());

                LatLng from = data.getParcelableExtra("startLatLng");
                LatLng to = data.getParcelableExtra("endLatLng");

                userLocationFrameOrigin = new LocationJson(from.latitude, from.longitude);
                userLocationFrameDestination = new LocationJson(to.latitude, to.longitude);
                userFrame = new UserJson(true);

                gd.setOnDirectionResponseListener(new GoogleDirectionsApi.OnDirectionResponseListener() {
                    @Override
                    public void onResponse(String status, Document doc, GoogleDirectionsApi gd) {
                        publishOrder(status, doc, gd);
                    }
                });

                gd.request(from, to, "driving");
            }
        }
    }


    public void publishOrder(String status, Document doc, GoogleDirectionsApi gd){
        if (doc != null) {
            HashMap<String, NodeList> routes = gd.getRoutes(doc);

            if (!routes.isEmpty()){
                String key = (String) routes.keySet().toArray()[0];
                NodeList r = routes.get(key);

                OrderDetailsJson orderDetailsJson = new OrderDetailsJson(
                        G.getInstance().getStartingFare(gd.getTotalDistanceText(r)),
                        gd.getTotalDistanceText(r), gd.getTotalDurationText(r),
                        gd.getStartAddress(r), gd.getEndAddress(r)
                );

                OrderPacket pkt = new OrderPacket(userFrame, userLocationFrameOrigin, userLocationFrameDestination, orderDetailsJson);

                Location temp = new Location("test");
                temp.setLatitude(userLocationFrameOrigin.getLattitude());
                temp.setLongitude(userLocationFrameOrigin.getLongitude());

                G.getInstance().mqBinding.SendMessage(pkt.toString(),"order", temp);

            }
        }
    }










    @Override
    public void onDestroy() {
        super.onDestroy();
//        if (ordersDispatcherInterface != null) {
//            ordersDispatcherInterface.StopOrdersListener();
//            ordersDispatcherInterface.StopRepliesListener();
//        }
    }

    @Override
    public void onDismiss(final AbsListView listView, final int[] reverseSortedPositions) {
        for (int position : reverseSortedPositions) {
            mAdapter.remove(position);
            mAdapter.notifyDataSetChanged();
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




    private class MyOrdersAdapter extends com.nhaarman.listviewanimations.ArrayAdapter<OrderPacket> {

        private final Context mContext;

        public MyOrdersAdapter(final Context context, final ArrayList<OrderPacket> items) {
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
            if (tv == null) {
                tv = LayoutInflater.from(mContext).inflate(R.layout.single_order, parent, false);
            }
            TextView txtOrigin = (TextView) tv.findViewById(R.id.txtOrderOrigin);
            TextView txtDestination = (TextView) tv.findViewById(R.id.txtOrderDestination);
            TextView txtFare = (TextView) tv.findViewById(R.id.txtOrderFare);
            TextView txtDistance = (TextView) tv.findViewById(R.id.txtOrderDistance);
            TextView txtDuration = (TextView) tv.findViewById(R.id.txtDuration);

            txtOrigin.setText(String.valueOf(mItems.get(position).getOrderDetailsJson().getOriginAddress()));
            txtDestination.setText(String.valueOf(mItems.get(position).getOrderDetailsJson().getDestAddress()));
            txtFare.setText(String.valueOf(mItems.get(position).getOrderDetailsJson().getPrice()));
            txtDistance.setText(String.valueOf(mItems.get(position).getOrderDetailsJson().getDistance()));
            txtDuration.setText(String.valueOf(mItems.get(position).getOrderDetailsJson().getDuration()));

            return tv;
        }
    }

}

