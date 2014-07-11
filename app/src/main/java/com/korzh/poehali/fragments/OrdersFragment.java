package com.korzh.poehali.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.korzh.poehali.R;
import com.korzh.poehali.dialogs.NewOrder;
import com.korzh.poehali.common.interfaces.OrdersDispatcherInterface;
import com.korzh.poehali.common.network.MessageConsumer;
import com.korzh.poehali.common.network.packets.OrderPacket;
import com.korzh.poehali.common.util.C;
import com.korzh.poehali.common.util.U;
import com.nhaarman.listviewanimations.itemmanipulation.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.SwipeDismissAdapter;
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingLeftInAnimationAdapter;
import com.rabbitmq.client.QueueingConsumer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by vladimir on 7/4/2014.
 */
public class OrdersFragment extends Fragment implements OnDismissCallback {
    private MyOrdersAdapter mAdapter = null;
    private ListView mListView = null;
    private OrdersDispatcherInterface ordersDispatcherInterface = null;
    private ArrayList<OrderPacket> arr = new ArrayList<OrderPacket>();

    public OrdersFragment() {}



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_orders, container, false);

        mListView = (ListView) rootView.findViewById(R.id.listOrders);

        mAdapter = new MyOrdersAdapter(getActivity(), arr);

        SwipeDismissAdapter adapter = new SwipeDismissAdapter(mAdapter, this);
        adapter.setAbsListView(mListView);
        mListView.setAdapter(adapter);

        final AnimationAdapter animAdapter = new SwingLeftInAnimationAdapter(mAdapter);
        animAdapter.setAbsListView(mListView);
        mListView.setAdapter(animAdapter);

        ordersDispatcherInterface = new OrdersDispatcherInterface();

        ordersDispatcherInterface.setOnNewOrderReceive(new MessageConsumer.OnReceiveMessageHandler() {
            @Override
            public void onReceiveMessage(QueueingConsumer.Delivery delivery) {
                byte[] message = delivery.getBody();
                String data = new String(message);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(data);
                } catch (JSONException e) {
                    U.Log(getClass().getSimpleName(), "Error reading json");
                }
                final OrderPacket order = new OrderPacket(jsonObject);
                U.Log(getClass().getSimpleName(), "New order received: "+order.toString());
                arr.add(order);

                Handler h = new Handler();
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        arr.remove(order);
                        mAdapter.notifyDataSetChanged();
                    }
                };
                h.postDelayed(r, 5000);

                animAdapter.setShouldAnimateFromPosition(arr.size()-1);
                mAdapter.notifyDataSetChanged();
            }
        });

        ordersDispatcherInterface.StartOrdersListener();
        ordersDispatcherInterface.StartRepliesListener();

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
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
//        if (requestCode == C.REQUEST_CODE_NAVIGATOR_NEW_ROUTE) {
//
//            if (resultCode == Activity.RESULT_OK) {
//
//                final GoogleDirectionsApi gd = new GoogleDirectionsApi(getActivity());
//
//                final Document doc = G.getInstance().lastPickedRoute;
//                if (doc != null) {
//                    final Handler h = new Handler();
//                    Runnable r = new Runnable() {
//                        private int extra = 0;
//                        @Override
//                        public void run() {
////                            if (extra <= G.getInstance().getStartingFare(gd.getTotalDistanceText(doc))) {
////                                LatLng from = data.getParcelableExtra("startLatLng");
////                                LatLng to = data.getParcelableExtra("endLatLng");
////
////                                UserJson userFrame = new UserJson(true);
////                                LocationJson userLocationFrameOrigin = new LocationJson(from.latitude, from.longitude);
////                                LocationJson userLocationFrameDestination = new LocationJson(to.latitude, to.longitude);
////                                OrderDetailsJson orderDetailsJson = new OrderDetailsJson(
////                                        G.getInstance().getStartingFare(gd.getTotalDistanceText(doc)) + extra,
////                                        gd.getTotalDistanceText(doc), gd.getTotalDurationText(doc),
////                                        gd.getStartAddress(doc), gd.getEndAddress(doc)
////                                );
////
////                                OrderPacket pkt = new OrderPacket(userFrame, userLocationFrameOrigin, userLocationFrameDestination, orderDetailsJson);
////                                ordersDispatcherInterface.AnnounceOrder(pkt);
////                                extra += 5;
////                                h.postDelayed(this, 5000);
//                            }
//                        }
//                    };
//                    h.post(r);
//                }
//            }
//        }
//    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (ordersDispatcherInterface != null) {
            ordersDispatcherInterface.StopOrdersListener();
            ordersDispatcherInterface.StopRepliesListener();
        }
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

