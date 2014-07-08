package com.korzh.poehali.network.packets.frames;

import com.korzh.poehali.util.G;
import com.korzh.poehali.util.U;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vladimir on 7/1/2014.
 */
public class UserJson extends NetworkObjectBase{
    private String userId;
    private boolean free;

    public UserJson(JSONObject obj){
        super(obj);
        try {
            this.userId = obj.getString("id");
            this.free = obj.getBoolean("st");
        } catch (JSONException e) {
            U.Log(getClass().getName(),"Error reading json object");
        }

    }

    public UserJson(Boolean free){
        super();
        this.userId = G.getInstance().userId;
        this.free = free;

        try {
            jsonObject.put("id", userId);
            jsonObject.put("st", free);
        } catch (JSONException e) {
            U.Log(getClass().getName(), "Error writing json object");
        }

    }

    public boolean isFree() {
        return free;
    }
    public String getUserId() {
        return userId;
    }
}
