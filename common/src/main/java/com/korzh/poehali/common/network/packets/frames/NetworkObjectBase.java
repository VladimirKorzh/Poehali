package com.korzh.poehali.common.network.packets.frames;

import org.json.JSONObject;

/**
 * Created by futurewife on 08.07.14.
 */
public abstract class NetworkObjectBase {
    protected JSONObject jsonObject;
    protected long recvdMillis;

    public NetworkObjectBase(){
        jsonObject = new JSONObject();
    }

    public NetworkObjectBase(JSONObject obj) {
        jsonObject = obj;
        recvdMillis = System.currentTimeMillis();
    }

    @Override
    public String toString(){ return jsonObject.toString();}
    public long getRecvdMillis(){ return recvdMillis; }
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
