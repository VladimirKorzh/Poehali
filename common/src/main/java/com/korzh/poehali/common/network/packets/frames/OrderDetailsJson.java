package com.korzh.poehali.common.network.packets.frames;

import com.korzh.poehali.common.util.U;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vladimir on 7/1/2014.
 */
public class OrderDetailsJson extends NetworkObjectBase{
    private double price;
    private String distance;
    private String duration;
    private String originAddress;
    private String destAddress;

    public OrderDetailsJson(JSONObject obj){
        super(obj);
        try {
            this.price = obj.getDouble("price");
            this.distance = obj.getString("distance");
            this.duration = obj.getString("duration");
            this.originAddress = obj.getString("origin");
            this.destAddress = obj.getString("destination");
        } catch (JSONException e) {
            U.Log(getClass().getSimpleName(),"Error reading json object");
        }
    }

    public OrderDetailsJson(float price, String duration, String distance, String origin, String destination){
        super();
        this.distance = distance;
        this.price = price;
        try {
            jsonObject.put("price", price);
            jsonObject.put("distance", distance);
            jsonObject.put("duration", duration);
            jsonObject.put("origin", origin);
            jsonObject.put("destination", destination);
        } catch (JSONException e) {
            U.Log(getClass().getSimpleName(),"Error writing json object");
        }

    }

    public String getOriginAddress() {
        return originAddress;
    }

    public String getDestAddress() {
        return destAddress;
    }

    public String getDuration() {
        return duration;
    }

    public double getPrice(){
        return price;
    }
    public String getDistance() {
        return distance;
    }
}
