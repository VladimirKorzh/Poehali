package com.korzh.poehali.network.packets;

import com.korzh.poehali.network.packets.frames.LocationJson;
import com.korzh.poehali.network.packets.frames.NetworkObjectBase;
import com.korzh.poehali.network.packets.frames.UserJson;
import com.korzh.poehali.util.U;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by futurewife on 08.07.14.
 */
public class GlobalMapAnnouncePacket extends NetworkObjectBase {
    private LocationJson locationJson = null;
    private int type = 0;
    private String timestamp = null;

    public GlobalMapAnnouncePacket(LatLng latLng, int type){
        super();
        locationJson = new LocationJson(latLng.lattitude, latLng.longitude);
        this.type = type;
        try {
            jsonObject.put("loc",locationJson);
            jsonObject.put("t",type);
            jsonObject.put("time","0");
        } catch (JSONException e) {
            U.Log(getClass().getName(), "Error writing json object");
        }
    }

    public GlobalMapAnnouncePacket(JSONObject obj){
        super(obj);
        try {
            locationJson = new LocationJson(obj.getJSONObject("loc"));
            type = obj.getInt("t");
            timestamp = obj.getString("time");
        } catch (JSONException e) {
            U.Log(getClass().getName(), "Error reading json object");
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
