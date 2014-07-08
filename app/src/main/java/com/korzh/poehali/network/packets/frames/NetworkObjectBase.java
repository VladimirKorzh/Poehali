package com.korzh.poehali.network.packets.frames;

import com.korzh.poehali.util.U;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by futurewife on 08.07.14.
 */
public abstract class NetworkObjectBase {
    protected JSONObject jsonObject;

    public NetworkObjectBase(){
        jsonObject = new JSONObject();
    }

    public NetworkObjectBase(JSONObject obj) {
        jsonObject = obj;
    }

    @Override
    public String toString(){ return jsonObject.toString();}
    public byte[] getBytes(){
        return jsonObject.toString().getBytes();
    }
    public JSONObject getJsonObject() {
        return jsonObject;
    }
    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }
}
