package com.korzh.poehali.network.packets;

import com.korzh.poehali.network.packets.frames.LocationJson;
import com.korzh.poehali.network.packets.frames.NetworkObjectBase;
import com.korzh.poehali.network.packets.frames.UserJson;
import com.korzh.poehali.util.U;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vladimir on 7/1/2014.
 */
public class UserLocationPacket extends NetworkObjectBase {
    private UserJson userFrame;
    private LocationJson locationFrame;

    public UserLocationPacket(UserJson u, LocationJson l){
        super();

        this.userFrame = u;
        this.locationFrame = l;
        try {
            jsonObject.put("user", userFrame);
            jsonObject.put("loc", locationFrame);
        } catch (JSONException e) {
            U.Log(getClass().getName(), "Error writing json object");
        }

    }

    public UserLocationPacket(JSONObject obj){
        super(obj);
        try {
            this.userFrame = new UserJson(obj.getJSONObject("user"));
            this.locationFrame = new LocationJson(obj.getJSONObject("loc"));
        } catch (JSONException e) {
            U.Log(getClass().getName(), "Error reading json object");
        }
    }

    public LocationJson getLocationFrame() {
        return locationFrame;
    }
    public UserJson getUserFrame() {
        return userFrame;
    }
}
