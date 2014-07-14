package com.korzh.poehali.common.network.packets;

import android.location.Location;

import com.korzh.poehali.common.network.packets.frames.LocationJson;
import com.korzh.poehali.common.network.packets.frames.NetworkObjectBase;
import com.korzh.poehali.common.network.packets.frames.OrderDetailsJson;
import com.korzh.poehali.common.network.packets.frames.UserJson;
import com.korzh.poehali.common.util.C;
import com.korzh.poehali.common.util.U;

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
    private int action;

    public final static int UPDATE = 1;
    public final static int HOLD   = 2;
    public final static int CANCEL = 3;


    public OrderPacket(JSONObject obj){
        super(obj);
        try {
            this.userFrame = new UserJson(obj.getJSONObject("user"));
            this.userLocationFrameOrigin = new LocationJson(obj.getJSONObject("origin"));
            this.userLocationFrameDestination = new LocationJson(obj.getJSONObject("dest"));
            this.orderDetailsJson = new OrderDetailsJson(obj.getJSONObject("details"));
            this.action = obj.getInt("action");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public OrderPacket(UserJson user, LocationJson origin, LocationJson dest, OrderDetailsJson order, int action){
        super();
        this.userFrame = user;
        this.userLocationFrameOrigin = origin;
        this.userLocationFrameDestination = dest;
        this.orderDetailsJson = order;
        this.action = action;
        try {
            jsonObject.put("user", user.getJsonObject());
            jsonObject.put("origin", origin.getJsonObject());
            jsonObject.put("dest", dest.getJsonObject());
            jsonObject.put("details", order.getJsonObject());
        } catch (JSONException e) {
            U.Log(getClass().getSimpleName(),"Error writing json");
        }
    }

    public boolean isWithinRange(Location myLocation){
        float [] dist = new float[1];
        Location.distanceBetween(myLocation.getLatitude(),myLocation.getLongitude(),
                userLocationFrameOrigin.getLattitude(), userLocationFrameOrigin.getLongitude(), dist);

        return dist[0] <= C.ORDER_SEARCH_RADIUS;
    }


    public void UpdateOrder(OrderPacket updated){
        this.orderDetailsJson = updated.getOrderDetailsJson();
//        this.userFrame = updated.getUserFrame();
//        this.userLocationFrameOrigin = updated.getUserLocationFrameOrigin();
//        this.userLocationFrameDestination = updated.getUserLocationFrameDestination();
    }

    public int getAction(){ return action; }
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
