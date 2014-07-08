package com.korzh.poehali.map;

import android.location.Location;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.korzh.poehali.R;
import com.korzh.poehali.network.packets.GlobalMapAnnouncePacket;
import com.korzh.poehali.util.C;

import java.sql.Timestamp;

/**
 * Created by vladimir on 7/3/2014.
 */
public class AnnounceMarker extends MapMarkerBase{

    private BitmapDescriptor getIcon(int type){
        switch (type){
            case C.ANNOUNCE_PACKET_POLICE:
                return BitmapDescriptorFactory.fromResource(R.drawable.img_police);
                break;
        }
    }

    public AnnounceMarker(GoogleMap googleMap, Location location, int type){
        placeMarker(new LatLng(location.getLatitude(),location.getLongitude()), 0.7f, type)
    }

    public AnnounceMarker(GoogleMap googleMap, GlobalMapAnnouncePacket pkt){
        Timestamp timestamp = Timestamp.valueOf(pkt.getTimestamp());
        Timestamp now = new Timestamp(System.currentTimeMillis());
        long diff = now.getTime()- timestamp.getTime();
        float hours = diff/1000/60/60;
        if (hours < 1.0f) hours = 1.0f;
        float alpha = (0.5f/24*hours + 0.2f);
        placeMarker(pkt.getLocationJson().getLatLng(), alpha, pkt.getType());
    }

    private void placeMarker(LatLng latLng, float alpha, int type){
        marker = googleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .alpha(alpha)
                        .icon(getIcon(type))
        );
    }

}
