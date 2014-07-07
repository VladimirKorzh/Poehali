package com.korzh.poehali.network.packets;

import android.location.Location;

import com.korzh.poehali.network.packets.frames.LocationFrame;
import com.korzh.poehali.network.packets.frames.OrderDetailsFrame;
import com.korzh.poehali.network.packets.frames.UserFrame;
import com.korzh.poehali.util.C;

/**
 * Created by vladimir on 7/1/2014.
 */
public class OrderPacket {
    private UserFrame userFrame;
    private LocationFrame userLocationFrameOrigin;
    private LocationFrame userLocationFrameDestination;
    private OrderDetailsFrame orderDetailsFrame;

    public OrderPacket(String str){
        String[] strings = str.split(C.PACKET_FRAME_SEPARATOR);
        this.userFrame = new UserFrame(strings[0]);
        this.userLocationFrameOrigin = new LocationFrame(strings[1]);
        this.userLocationFrameDestination = new LocationFrame(strings[2]);
        this.orderDetailsFrame = new OrderDetailsFrame(strings[3]);
        CalculateRouteDistance();
    }

    public OrderPacket(UserFrame user, LocationFrame origin, LocationFrame dest, OrderDetailsFrame order){
        this.userFrame = user;
        this.userLocationFrameOrigin = origin;
        this.userLocationFrameDestination = dest;
        this.orderDetailsFrame = order;
        CalculateRouteDistance();
    }

    public boolean isWithinRange(Location myLocation){
        float [] dist = new float[1];
        Location.distanceBetween(myLocation.getLatitude(),myLocation.getLongitude(),
                userLocationFrameOrigin.getLattitude(), userLocationFrameOrigin.getLongitude(), dist);

        return dist[0] <= C.ORDER_SEARCH_RADIUS;
    }

    private void CalculateRouteDistance() {
        //TODO Implement this
    }



    public String toString(){
        return userFrame.toString()+C.PACKET_FRAME_SEPARATOR+userLocationFrameOrigin.toString()+
                C.PACKET_FRAME_SEPARATOR+userLocationFrameDestination.toString()+C.PACKET_FRAME_SEPARATOR+
                orderDetailsFrame.toString();
    }

    public UserFrame getUserFrame(){
        return userFrame;
    }

    public LocationFrame getUserLocationFrameOrigin() {
        return userLocationFrameOrigin;
    }

    public LocationFrame getUserLocationFrameDestination() {
        return userLocationFrameDestination;
    }

    public OrderDetailsFrame getOrderDetailsFrame() {
        return orderDetailsFrame;
    }
}
