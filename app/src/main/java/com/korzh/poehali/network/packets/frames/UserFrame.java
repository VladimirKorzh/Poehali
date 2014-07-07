package com.korzh.poehali.network.packets.frames;

import com.korzh.poehali.util.C;
import com.korzh.poehali.util.G;

/**
 * Created by vladimir on 7/1/2014.
 */
public class UserFrame {
    private String userId;
    private boolean free;

    public UserFrame(String s){
        String[] strings = s.split(C.USER_FRAME_SEPARATOR);
        this.userId = strings[0];
        this.free = Boolean.valueOf(strings[1]);
    }

    public UserFrame(Boolean free){
        this.userId = G.getInstance().userId;
        this.free = free;
    }

    public String toString(){
        return this.userId + C.USER_FRAME_SEPARATOR + this.free;
    }

    public boolean isFree() {
        return free;
    }

    public String getUserId() {
        return userId;
    }
}
