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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by vladimir on 7/1/2014.
 */
public class TaxiDriverMarker {
    public TaxiDriverMarker(GoogleMap googleMap, UserLocationPacket userLocationPacket){

        List<BitmapDescriptor> imgs = new ArrayList<BitmapDescriptor>();
        imgs.add(BitmapDescriptorFactory.fromResource(R.drawable.img_police));
        imgs.add(BitmapDescriptorFactory.fromResource(R.drawable.img_passenger));
        imgs.add(BitmapDescriptorFactory.fromResource(R.drawable.img_taxibusy));
        imgs.add(BitmapDescriptorFactory.fromResource(R.drawable.img_taxifree));

        Random rnd = new Random();

        final Marker m = googleMap.addMarker(new MarkerOptions()
                .position(userLocationPacket.getLocationFrame().getLatLng())
                .icon(imgs.get(rnd.nextInt(imgs.size()))));

        Handler handler = new Handler();
        Runnable removeMarker = new Runnable(){
            public void run(){
                m.remove();
            }
        };
        handler.postDelayed(removeMarker, C.LOCATION_BROADCASTER_UPDATES_MILLIS);
    }

}
