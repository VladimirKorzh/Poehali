package com.korzh.poehali.common.network.packets.frames;

import com.google.android.gms.maps.model.LatLng;
import com.korzh.poehali.common.util.U;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vladimir on 7/1/2014.
 */
public class LocationJson extends NetworkObjectBase {
    private double lattitude;
    private double longitude;

    public LocationJson(double lattitude, double longitude) {
        super();
        this.lattitude = lattitude;
        this.longitude = longitude;
        jsonObject = new JSONObject();
        try {
            jsonObject.put("lat",this.lattitude);
            jsonObject.put("lon",this.longitude);
        } catch (JSONException e) {
            U.Log(getClass().getSimpleName(),"Error writing json object");
        }

    }

    public LocationJson(JSONObject obj){

        try {
            this.jsonObject = obj;
            this.lattitude = obj.getDouble("lat");
            this.longitude = obj.getDouble("lon");
        } catch (JSONException e) {
            U.Log(getClass().getSimpleName(),"Error reading json object");
        }
    }

    public LatLng getLatLng() {
        return new LatLng(lattitude,longitude);
    }
    public double getLattitude() {
        return lattitude;
    }
    public double getLongitude() {
        return longitude;
    }

}
