package com.korzh.poehali.common.interfaces;

import android.location.Location;
import android.os.Handler;

import com.google.android.gms.maps.GoogleMap;
import com.korzh.poehali.common.network.MessageConsumer;
import com.korzh.poehali.common.network.packets.UserLocationPacket;
import com.korzh.poehali.common.network.packets.frames.LocationJson;
import com.korzh.poehali.common.network.packets.frames.UserJson;
import com.korzh.poehali.common.util.C;
import com.korzh.poehali.common.util.G;
import com.korzh.poehali.common.util.U;

/**
 * Created by vladimir on 7/1/2014.
 */
public class LocationBroadcaster {
    private final String TAG = getClass().getSimpleName();

    private MessageConsumer locationConsumer = null;
    private GoogleMap googleMap = null;

    private Handler updateHandler;

    public LocationBroadcaster(){}

    final Runnable uploadLocation = new Runnable(){
        public void run(){
            Location location = G.getInstance().getLastKnownLocation();

            UserJson user = new UserJson(true);
            LocationJson loc = new LocationJson(location.getLatitude(),location.getLongitude());
            UserLocationPacket pkt = new UserLocationPacket(user,loc);

            G.getInstance().mqBinding.SendMessage(pkt.toString(),"location", location);

            updateHandler.postDelayed(uploadLocation, C.LOCATION_BROADCASTER_UPDATES_MILLIS);
        }
    };


    public void StartBroadcast(){
        U.Log(TAG, "Starting");

        updateHandler = new Handler();
        updateHandler.post(uploadLocation);
    }

    public void StopBroadcast(){
        U.Log(TAG,"Stopped");
        if (updateHandler != null) updateHandler.removeCallbacks(uploadLocation);
    }

//    public void StartListener(){
//        U.Log(TAG, "Location Consumer Started");
//        locationConsumer = new MessageConsumer();
//        locationConsumer.setOnReceiveMessageHandler(new MessageConsumer.OnReceiveMessageHandler() {
//            public void onReceiveMessage(QueueingConsumer.Delivery delivery) {
//                byte[] message = delivery.getBody();
//                JSONObject obj = null;
//                String data = new String(message);
//                try {
//                    obj = new JSONObject(data);
//                } catch (JSONException e) {
//                    U.Log(getClass().getSimpleName(),"Error reading json");
//                }
//                UserLocationPacket userLocationPacket = new UserLocationPacket(obj);
//                U.Log(TAG, "Received location: " + userLocationPacket.toString());
//                if (!G.getInstance().userId.equals(userLocationPacket.getUserFrame().getUserId()))
//                    new TaxiDriverMarker(googleMap, userLocationPacket);
//            }
//        });
//        locationConsumer.Start(C.DEFAULT_LOCATIONS_EXCHANGE, "", G.getInstance().userId+" locations");
//    }
//
//    public void StopListener(){
//        U.Log(TAG,"Location Consumer Stopped");
//        if (locationConsumer != null) locationConsumer.Stop();
//    }
}
