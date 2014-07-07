package com.korzh.poehali.map;

import android.location.Location;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.korzh.poehali.R;

import java.sql.Timestamp;

/**
 * Created by vladimir on 7/3/2014.
 */
public class PoliceMarker {
    public Marker marker;

    public PoliceMarker(GoogleMap googleMap, Location location){
        marker = googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(location.getLatitude(),location.getLongitude()))
                        .anchor(0.5f, 0.5f)
                        .alpha(0.7f)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.img_police))
        );
    }

    public PoliceMarker(GoogleMap googleMap, org.json.simple.JSONObject record){
        String loc = null;
        String time = null;
        LatLng pos = null;
        Timestamp timestamp  = null;

        loc = (String) record.get("location");

        loc = loc.replace(")","");
        loc = loc.replace("(","");
        String[] strings = loc.split(",");
        pos = new LatLng(Double.valueOf(strings[0]), Double.valueOf(strings[1]));
        time = (String) record.get("timestamp");
        timestamp = Timestamp.valueOf(time);

        Timestamp now = new Timestamp(System.currentTimeMillis());
        long diff = now.getTime()- timestamp.getTime();
        float hours = diff/1000/60/60;
        if (hours < 1.0f) hours = 1.0f;
        float alpha = (0.5f/24*hours + 0.2f);
        marker = googleMap.addMarker(new MarkerOptions()
                .position(pos)
                .anchor(0.5f, 0.5f)
                .alpha(alpha)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.img_police))
                );

    }

}
