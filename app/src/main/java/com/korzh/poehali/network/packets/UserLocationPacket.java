package com.korzh.poehali.network.packets;

import com.korzh.poehali.network.packets.frames.LocationFrame;
import com.korzh.poehali.network.packets.frames.UserFrame;
import com.korzh.poehali.util.C;

/**
 * Created by vladimir on 7/1/2014.
 */
public class UserLocationPacket {
    private UserFrame userFrame;
    private LocationFrame locationFrame;

    public UserLocationPacket(UserFrame u, LocationFrame l){
        this.userFrame = u;
        this.locationFrame = l;
    }

    public UserLocationPacket(String s){
        String[] strings = s.split(C.PACKET_FRAME_SEPARATOR);
        this.userFrame = new UserFrame(strings[0]);
        this.locationFrame = new LocationFrame(strings[1]);
    }

    public String toString(){
        return userFrame.toString() + C.PACKET_FRAME_SEPARATOR + locationFrame.toString();
    }

    public LocationFrame getLocationFrame() {
        return locationFrame;
    }

    public UserFrame getUserFrame() {
        return userFrame;
    }
}
