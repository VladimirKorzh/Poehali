package com.korzh.poehali.common.network.packets.frames;

import com.korzh.poehali.common.util.G;
import com.korzh.poehali.common.util.U;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vladimir on 7/1/2014.
 */
public class UserJson extends NetworkObjectBase{
    private String userId;
    private int free;

    public UserJson(JSONObject obj){
        super(obj);
        try {
            this.userId = obj.getString("id");
            this.free = obj.getInt("st");
        } catch (JSONException e) {
            U.Log(getClass().getSimpleName(),"Error reading json object");
        }

    }

    public UserJson(Boolean free){
        super();
        this.userId = G.getInstance().userId;
        this.free = free ? 1:0;

        try {
            jsonObject.put("id", userId);
            jsonObject.put("st", free);
        } catch (JSONException e) {
            U.Log(getClass().getSimpleName(), "Error writing json object");
        }

    }

    public boolean isFree() {
        return free == 1;
    }
    public String getUserId() {
        return userId;
    }
}
