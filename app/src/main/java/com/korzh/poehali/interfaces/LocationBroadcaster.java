package com.korzh.poehali.interfaces;

import android.content.Context;
import android.location.Location;
import android.os.Handler;

import com.google.android.gms.maps.GoogleMap;
import com.korzh.poehali.map.TaxiDriverMarker;
import com.korzh.poehali.network.MessageConsumer;
import com.korzh.poehali.network.MessageProducer;
import com.korzh.poehali.network.NetworkMessage;
import com.korzh.poehali.network.packets.UserLocationPacket;
import com.korzh.poehali.network.packets.frames.LocationFrame;
import com.korzh.poehali.network.packets.frames.UserFrame;
import com.korzh.poehali.util.C;
import com.korzh.poehali.util.G;
import com.korzh.poehali.util.U;

/**
 * Created by vladimir on 7/1/2014.
 */
public class LocationBroadcaster {
    private final String TAG = getClass().getName();

    private Context context = null;
    private MessageConsumer locationConsumer = null;
    private GoogleMap googleMap = null;

    private Handler updateHandler;

    public LocationBroadcaster(Context c, GoogleMap googleMap){
        this.context = c;
        this.googleMap = googleMap;
    }

    final Runnable uploadLocation = new Runnable(){
        public void run(){
            Location location = G.getInstance().getLastKnownLocation();

            NetworkMessage msg = new NetworkMessage();
            msg.setExchange(C.DEFAULT_LOCATIONS_EXCHANGE);
            msg.setExchangeType("direct");

            UserFrame user = new UserFrame(true);
            LocationFrame loc = new LocationFrame(location.getLatitude(),location.getLongitude());
            UserLocationPacket pkt = new UserLocationPacket(user,loc);

            msg.setData(pkt.toString());
            MessageProducer messageProducer = new MessageProducer();
            messageProducer.execute(msg);

            updateHandler.postDelayed(uploadLocation, C.LOCATION_BROADCASTER_UPDATES_MILLIS);
        }
    };


    public void StartBroadcast(){
        U.Log(TAG, "Starting");

        updateHandler = new Handler();
        updateHandler.postDelayed(uploadLocation, C.LOCATION_BROADCASTER_UPDATES_MILLIS);
    }

    public void StopBroadcast(){
        U.Log(TAG,"Stopped");
        if (updateHandler != null) updateHandler.removeCallbacks(uploadLocation);
    }

    public void StartListener(){
        U.Log(TAG, "Location Consumer Started");
        locationConsumer = new MessageConsumer();
        locationConsumer.setOnReceiveMessageHandler(new MessageConsumer.OnReceiveMessageHandler() {
            public void onReceiveMessage(byte[] message) {
                String data = new String(message);
                UserLocationPacket userLocationPacket = new UserLocationPacket(data);
                U.Log(TAG, "Received location: " + userLocationPacket.toString());
                if (!G.getInstance().userId.equals(userLocationPacket.getUserFrame().getUserId()))
                    new TaxiDriverMarker(googleMap, userLocationPacket);
            }
        });
        locationConsumer.Start(C.DEFAULT_LOCATIONS_EXCHANGE, "");
    }

    public void StopListener(){
        U.Log(TAG,"Location Consumer Stopped");
        if (locationConsumer != null) locationConsumer.Stop();
    }
}
