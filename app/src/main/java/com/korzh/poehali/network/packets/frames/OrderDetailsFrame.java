package com.korzh.poehali.network.packets.frames;

import com.korzh.poehali.util.C;

/**
 * Created by vladimir on 7/1/2014.
 */
public class OrderDetailsFrame {
    private float price;
    private float distance;

    public OrderDetailsFrame(String s){
        String[] strings = s.split(C.ORDER_FRAME_SEPARATOR);
        this.price = Float.valueOf(strings[0]);
    }

    public OrderDetailsFrame(float price){
        this.price = price;
    }

    public String toString(){
        return String.valueOf(this.price);
    }

    public float getPrice(){
        return price;
    }

    public float getDistance() {
        return distance;
    }
}
