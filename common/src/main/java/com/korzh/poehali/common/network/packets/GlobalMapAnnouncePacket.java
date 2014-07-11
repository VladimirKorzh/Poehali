package com.korzh.poehali.common.network.packets;

import com.google.android.gms.maps.model.LatLng;
import com.korzh.poehali.common.network.packets.frames.LocationJson;
import com.korzh.poehali.common.network.packets.frames.NetworkObjectBase;
import com.korzh.poehali.common.util.U;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by futurewife on 08.07.14.
 */
public class GlobalMapAnnouncePacket extends NetworkObjectBase {
    private LocationJson locationJson = null;
    private int type = 0;
    private String timestamp = null;

    public GlobalMapAnnouncePacket(LatLng latLng, int type){
        super();
        locationJson = new LocationJson(latLng.latitude, latLng.longitude);
        this.type = type;
        try {
            jsonObject.put("loc",locationJson.getJsonObject());
            jsonObject.put("t",type);
            jsonObject.put("time", String.valueOf(new java.sql.Timestamp(Calendar.getInstance().getTime().getTime())));
            this.timestamp = String.valueOf(new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()));
        } catch (JSONException e) {
            U.Log(getClass().getSimpleName(), "Error writing json object");
        }
    }

    public GlobalMapAnnouncePacket(JSONObject obj){
        super(obj);
        try {
            locationJson = new LocationJson(obj.getJSONObject("loc"));
            type = obj.getInt("t");
            timestamp = obj.getString("time");
        } catch (JSONException e) {
            U.Log(getClass().getSimpleName(), "Error reading json object "+ obj);
        }
    }

    public LocationJson getLocationJson() {
        return locationJson;
    }

    public int getType() {
        return type;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
