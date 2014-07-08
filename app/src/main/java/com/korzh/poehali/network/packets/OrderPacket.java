package com.korzh.poehali.network.packets;

import android.location.Location;

import com.korzh.poehali.network.packets.frames.LocationJson;
import com.korzh.poehali.network.packets.frames.NetworkObjectBase;
import com.korzh.poehali.network.packets.frames.OrderDetailsJson;
import com.korzh.poehali.network.packets.frames.UserJson;
import com.korzh.poehali.util.C;
import com.korzh.poehali.util.U;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vladimir on 7/1/2014.
 */
public class OrderPacket extends NetworkObjectBase{
    private UserJson userFrame;
    private LocationJson userLocationFrameOrigin;
    private LocationJson userLocationFrameDestination;
    private OrderDetailsJson orderDetailsJson;

    public OrderPacket(JSONObject obj){
        super(obj);
        this.userFrame = new UserJson(obj.get("user"));
        this.userLocationFrameOrigin = new LocationJson(obj.get("origin"));
        this.userLocationFrameDestination = new LocationJson(obj.get("dest"));
        this.orderDetailsJson = new OrderDetailsJson(obj.get("details"));
    }

    public OrderPacket(UserJson user, LocationJson origin, LocationJson dest, OrderDetailsJson order){
        super();
        this.userFrame = user;
        this.userLocationFrameOrigin = origin;
        this.userLocationFrameDestination = dest;
        this.orderDetailsJson = order;
        try {
            jsonObject.put("user",user);
            jsonObject.put("origin", origin);
            jsonObject.put("dest", dest);
            jsonObject.put("details", order);
        } catch (JSONException e) {
            U.Log(getClass().getName(),"Error writing json");
        }
    }

    public boolean isWithinRange(Location myLocation){
        float [] dist = new float[1];
        Location.distanceBetween(myLocation.getLatitude(),myLocation.getLongitude(),
                userLocationFrameOrigin.getLattitude(), userLocationFrameOrigin.getLongitude(), dist);

        return dist[0] <= C.ORDER_SEARCH_RADIUS;
    }

    public UserJson getUserFrame(){
        return userFrame;
    }
    public LocationJson getUserLocationFrameOrigin() {
        return userLocationFrameOrigin;
    }
    public LocationJson getUserLocationFrameDestination() {
        return userLocationFrameDestination;
    }
    public OrderDetailsJson getOrderDetailsJson() {
        return orderDetailsJson;
    }
}
