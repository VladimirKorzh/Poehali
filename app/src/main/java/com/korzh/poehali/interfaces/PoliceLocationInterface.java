package com.korzh.poehali.interfaces;

import android.location.Location;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.korzh.poehali.map.PoliceMarker;
import com.korzh.poehali.network.MessageProducer;
import com.korzh.poehali.network.NetworkMessage;
import com.korzh.poehali.util.C;
import com.korzh.poehali.util.G;
import com.korzh.poehali.util.U;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by vladimir on 7/3/2014.
 */
public class PoliceLocationInterface {
    private GoogleMap googleMap;
    private ArrayList<PoliceMarker> markers = new ArrayList<PoliceMarker>();

    public PoliceLocationInterface(GoogleMap googleMap){
        this.googleMap = googleMap;
    }

    public void markPolice(){
        NetworkMessage msg = new NetworkMessage();
        msg.setQueue(C.DEFAULT_POLICE_QUEUE);
        msg.setDurable(true);

        Location myLoc = G.getInstance().getLastKnownLocation();

        org.json.simple.JSONObject jsonObject = new org.json.simple.JSONObject();

        jsonObject.put("location",myLoc.getLatitude()+","+myLoc.getLongitude());

        msg.setData(jsonObject.toString());
        MessageProducer messageProducer = new MessageProducer();
        messageProducer.execute(msg);

        markers.add(new PoliceMarker(googleMap,myLoc));
    }

    public void updatePoliceLocations() {
        new UpdatePoliceLocations().execute("test");
    }

    public class UpdatePoliceLocations extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            StringBuilder response = new StringBuilder();
            InputStream in = null;
            try {
                in = new URL(C.POLICE_API_ENDPOINT).openStream();
                if ( in != null) response.append(IOUtils.toString( in ) );
            } catch (IOException e) {
                U.Log(getClass().getName(), "Error reaching Police API: "+e.getMessage());
            } finally {
                IOUtils.closeQuietly(in);
            }
            return response.toString();
        }

        protected void onPostExecute(String response) {
            // delete old
            for (PoliceMarker m : markers){
                m.marker.remove();
            }


            Object obj= JSONValue.parse(response);
            JSONArray array=(JSONArray)obj;
            // add new
            if (array != null) {
                for (Object o : array) {
                    markers.add(new PoliceMarker(googleMap, (org.json.simple.JSONObject) o));
                }
            }
        }
    }
}
