package com.korzh.poehali.interfaces;

import android.location.Location;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.korzh.poehali.map.AnnounceMarker;
import com.korzh.poehali.network.MessageProducer;
import com.korzh.poehali.network.NetworkMessage;
import com.korzh.poehali.network.packets.GlobalMapAnnouncePacket;
import com.korzh.poehali.util.C;
import com.korzh.poehali.util.G;
import com.korzh.poehali.util.U;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by vladimir on 7/3/2014.
 */
public class GlobalAnnounceInterface {
    private GoogleMap googleMap;
    private ArrayList<AnnounceMarker> announceMarkers = new ArrayList<AnnounceMarker>();

    public GlobalAnnounceInterface(GoogleMap googleMap){
        this.googleMap = googleMap;
    }

    public void announcePolice(){
        NetworkMessage msg = new NetworkMessage();
        msg.setQueue(C.DEFAULT_POLICE_QUEUE);
        msg.setDurable(true);

        Location myLoc = G.getInstance().getLastKnownLocation();
        LatLng latLng = new LatLng(myLoc.getLatitude(),myLoc.getLongitude());

        GlobalMapAnnouncePacket pkt = new GlobalMapAnnouncePacket(latLng, C.ANNOUNCE_PACKET_POLICE);

        msg.setData(pkt.toString());
        MessageProducer messageProducer = new MessageProducer();
        messageProducer.execute(msg);

        announceMarkers.add(new AnnounceMarker(googleMap, myLoc));
    }

    public void getWhatsAnnouncedGlobally() {
        new taskUpdateGlobalAnnounce().execute("test");
    }

    public class taskUpdateGlobalAnnounce extends AsyncTask<String, Void, String> {
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
            for (AnnounceMarker m : announceMarkers){
                m.marker.remove();
            }


            JSONObject obj;
            JSONArray array = null;
            try {
                obj = new JSONObject(response);
                array = obj.getJSONArray("announce");
            } catch (JSONException e) {
                U.Log(getClass().getName(), "Error reading json");
            }

            if (array != null) {
                for (int i=0; i<array.length(); i++) {
                    GlobalMapAnnouncePacket g = null;
                    try {
                        g = new GlobalMapAnnouncePacket((JSONObject) array.get(i));
                        announceMarkers.add(new AnnounceMarker(googleMap, g));
                    } catch (JSONException e) {
                        U.Log(getClass().getName(), "Error reading json");
                    }

                }
            }
        }
    }
}
