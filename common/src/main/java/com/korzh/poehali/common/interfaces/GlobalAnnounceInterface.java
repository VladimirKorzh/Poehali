package com.korzh.poehali.common.interfaces;

import android.location.Location;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.korzh.poehali.common.map.AnnounceMarker;
import com.korzh.poehali.common.network.packets.GlobalMapAnnouncePacket;
import com.korzh.poehali.common.util.C;
import com.korzh.poehali.common.util.G;
import com.korzh.poehali.common.util.U;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        Location myLoc = G.getInstance().getLastKnownLocation();
        LatLng latLng = new LatLng(myLoc.getLatitude(),myLoc.getLongitude());

        GlobalMapAnnouncePacket pkt = new GlobalMapAnnouncePacket(latLng, C.ANNOUNCE_PACKET_POLICE);
        G.getInstance().mqBinding.SendMessage(pkt.toString(),"announce",myLoc);

        announceMarkers.add(new AnnounceMarker(googleMap, pkt));
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
                U.Log(getClass().getSimpleName(), "Error reaching Police API: "+e.getMessage());
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
                U.Log(getClass().getSimpleName(), "Error reading json: "+response);
            }

            if (array != null) {
                GlobalMapAnnouncePacket g;
                for (int i=0; i<array.length(); i++) {
                    try {
                        g = new GlobalMapAnnouncePacket((JSONObject) array.get(i));
                        announceMarkers.add(new AnnounceMarker(googleMap, g));
                    } catch (JSONException e) {
                        U.Log(getClass().getSimpleName(), "Error reading json");
                    }
                }
            }
        }
    }
}
