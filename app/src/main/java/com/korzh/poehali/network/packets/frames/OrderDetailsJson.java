package com.korzh.poehali.network.packets.frames;

import com.korzh.poehali.util.C;
import com.korzh.poehali.util.U;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vladimir on 7/1/2014.
 */
public class OrderDetailsJson extends NetworkObjectBase{
    private double price;
    private double distance;

    public OrderDetailsJson(JSONObject obj){
        super(obj);
        try {
            this.price = obj.getDouble("price");
            this.distance = obj.getDouble("distance");
        } catch (JSONException e) {
            U.Log(getClass().getName(),"Error reading json object");
        }
    }

    public OrderDetailsJson(float price, float distance){
        super();
        this.distance = distance;
        this.price = price;
        try {
            jsonObject.put("price", price);
            jsonObject.put("distance", distance);
        } catch (JSONException e) {
            U.Log(getClass().getName(),"Error writing json object");
        }

    }


    public double getPrice(){
        return price;
    }
    public double getDistance() {
        return distance;
    }
}
