package com.korzh.poehali.network.packets.frames;

import com.google.android.gms.maps.model.LatLng;
import com.korzh.poehali.util.C;

/**
 * Created by vladimir on 7/1/2014.
 */
public class LocationFrame {

    private double lattitude;
    private double longitude;

    public LocationFrame(double lattitude, double longitude) {
        this.lattitude = lattitude;
        this.longitude = longitude;
    }

    public LocationFrame(String s){
        String[] strings = s.split(C.LOCATION_FRAME_SEPARATOR);
        this.lattitude = Double.valueOf(strings[0]);
        this.longitude = Double.valueOf(strings[1]);
    }

    public String toString(){
        return this.lattitude+C.LOCATION_FRAME_SEPARATOR+this.longitude;
    }

    public LatLng getLatLng() {
        return new LatLng(lattitude,longitude);
    }

    public double getLattitude() {
        return lattitude;
    }

    public void setLattitude(double lattitude) {
        this.lattitude = lattitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

//    public String getAddress(){
//        List<Address> addresses;
//        String fullAddress = null;
//        try {
//            addresses = G.getInstance().geocoder.getFromLocation(getLattitude(), getLongitude(), 1);
//            fullAddress = addresses.get(0).getAddressLine(0);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return fullAddress;
//    }
}
