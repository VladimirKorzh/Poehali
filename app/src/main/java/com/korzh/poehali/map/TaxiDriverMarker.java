package com.korzh.poehali.map;

import android.os.Handler;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.korzh.poehali.R;
import com.korzh.poehali.network.packets.UserLocationPacket;
import com.korzh.poehali.util.C;

/**
 * Created by vladimir on 7/1/2014.
 */
public class TaxiDriverMarker extends MapMarkerBase {
    public TaxiDriverMarker(GoogleMap googleMap, UserLocationPacket userLocationPacket){

        BitmapDescriptor img = null;
        if (userLocationPacket.getUserFrame().isFree()){
            img = BitmapDescriptorFactory.fromResource(R.drawable.img_taxifree);
        }
        else {
            img = BitmapDescriptorFactory.fromResource(R.drawable.img_taxibusy);
        }

        marker = googleMap.addMarker(new MarkerOptions()
                .position(userLocationPacket.getLocationFrame().getLatLng())
                .icon(img);

        Handler handler = new Handler();
        Runnable removeMarker = new Runnable(){
            public void run(){
                m.remove();
            }
        };
        handler.postDelayed(removeMarker, C.LOCATION_BROADCASTER_UPDATES_MILLIS);
    }

}
