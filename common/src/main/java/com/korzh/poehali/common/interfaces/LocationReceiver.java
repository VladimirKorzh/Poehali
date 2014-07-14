package com.korzh.poehali.common.interfaces;

import com.korzh.poehali.common.map.TaxiDriverMarker;
import com.korzh.poehali.common.network.MessageConsumer;
import com.korzh.poehali.common.network.packets.UserLocationPacket;
import com.korzh.poehali.common.util.G;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by futurewife on 14.07.14.
 */
public class LocationReceiver {
    private GoogleMap googleMap;

    public LocationReceiver(GoogleMap googleMap){
        this.googleMap = googleMap;
        G.getInstance().mqBinding.getMessageConsumer().registerCallback(locationCallback);
        getLatestTaxiDriversInfo();
    }

    private ArrayList<TaxiDriverMarker> otherTaxis = new ArrayList<TaxiDriverMarker>();

    private void ProcessLocationPacket(UserLocationPacket pkt){
        new TaxiDriverMarker(googleMap, pkt);
    }

    private MessageConsumer.OnReceiveMessageHandler locationCallback = new MessageConsumer.OnReceiveMessageHandler() {
        @Override
        public void onReceive(JSONObject obj) {
            UserLocationPacket pkt = new UserLocationPacket(obj);
            ProcessLocationPacket(pkt);
        }

        @Override
        public String getType() {
            return "location";
        }
    };

    private void getLatestTaxiDriversInfo() {
        for (UserLocationPacket pkt : G.getInstance().mqBinding.getMessageConsumer().getReceivedLocations()){
            ProcessLocationPacket(pkt);
        }
    }
}
