package com.korzh.poehali.common.interfaces;

import android.content.Context;
import android.os.AsyncTask;
import android.util.DisplayMetrics;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.korzh.poehali.common.util.U;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by vladimir on 7/4/2014.
 */
public class GoogleDirectionsApi {

    private Context mContext = null;
    public GoogleDirectionsApi(Context context) {
        mContext = context;
    }


    private OnDirectionResponseListener mDirectionListener = null;
    public interface OnDirectionResponseListener {
        public void onResponse(String status, Document doc, GoogleDirectionsApi gd);
    }
    public void setOnDirectionResponseListener(OnDirectionResponseListener listener) {
        mDirectionListener = listener;
    }





    public String request(LatLng start, LatLng end, String mode) {
        final String url = "http://maps.googleapis.com/maps/api/directions/xml?"
                + "origin=" + start.latitude + "," + start.longitude
                + "&destination=" + end.latitude + "," + end.longitude
                + "&sensor=false&units=metric&mode=" + mode;


        U.Log("GoogleDirection", "URL : " + url);
        new RequestTask().execute(url);
        return url;
    }

    private class RequestTask extends AsyncTask<String, Void, Document> {
        protected Document doInBackground(String... url) {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpContext localContext = new BasicHttpContext();
                HttpPost httpPost = new HttpPost(url[0]);
                HttpResponse response = httpClient.execute(httpPost, localContext);
                InputStream in = response.getEntity().getContent();
                DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                return builder.parse(in);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Document doc) {
            super.onPostExecute(doc);
            if(mDirectionListener != null)
                mDirectionListener.onResponse(getStatus(doc), doc, GoogleDirectionsApi.this);
        }

        private String getStatus(Document doc) {
            NodeList nl1 = doc.getElementsByTagName("status");
            Node node1 = nl1.item(0);
            U.Log("GoogleDirection", "Status : " + node1.getTextContent());
            return node1.getTextContent();
        }
    }


    public PolylineOptions getPolyline(Document doc, int width, int color) {
        ArrayList<LatLng> arr_pos = getDirection(doc);
        PolylineOptions rectLine = new PolylineOptions().width(dpToPx(width)).color(color);
        for (LatLng arr_po : arr_pos) rectLine.add(arr_po);
        return rectLine;
    }

    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public ArrayList<LatLng> getDirection(Document doc) {
        NodeList nl1, nl2, nl3;
        ArrayList<LatLng> listGeopoints = new ArrayList<LatLng>();
        nl1 = doc.getElementsByTagName("step");
        if (nl1.getLength() > 0) {
            for (int i = 0; i < nl1.getLength(); i++) {
                Node node1 = nl1.item(i);
                nl2 = node1.getChildNodes();

                Node locationNode = nl2.item(getNodeIndex(nl2, "start_location"));
                nl3 = locationNode.getChildNodes();
                Node latNode = nl3.item(getNodeIndex(nl3, "lat"));
                double lat = Double.parseDouble(latNode.getTextContent());
                Node lngNode = nl3.item(getNodeIndex(nl3, "lng"));
                double lng = Double.parseDouble(lngNode.getTextContent());
                listGeopoints.add(new LatLng(lat, lng));

                locationNode = nl2.item(getNodeIndex(nl2, "polyline"));
                nl3 = locationNode.getChildNodes();
                latNode = nl3.item(getNodeIndex(nl3, "points"));
                ArrayList<LatLng> arr = decodePoly(latNode.getTextContent());
                for (LatLng anArr : arr) {
                    listGeopoints.add(new LatLng(anArr.latitude
                            , anArr.longitude));
                }

                locationNode = nl2.item(getNodeIndex(nl2, "end_location"));
                nl3 = locationNode.getChildNodes();
                latNode = nl3.item(getNodeIndex(nl3, "lat"));
                lat = Double.parseDouble(latNode.getTextContent());
                lngNode = nl3.item(getNodeIndex(nl3, "lng"));
                lng = Double.parseDouble(lngNode.getTextContent());
                listGeopoints.add(new LatLng(lat, lng));
            }
        }

        return listGeopoints;
    }

    private int getNodeIndex(NodeList nl, String nodename) {
        for(int i = 0 ; i < nl.getLength() ; i++) {
            if(nl.item(i).getNodeName().equals(nodename))
                return i;
        }
        return -1;
    }


    private ArrayList<LatLng> decodePoly(String encoded) {
        ArrayList<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng position = new LatLng((double)lat / 1E5, (double)lng / 1E5);
            poly.add(position);
        }
        return poly;
    }








    public String getTotalDurationText(Document doc) {
        NodeList nl1 = doc.getElementsByTagName("duration");
        Node node1 = nl1.item(nl1.getLength() - 1);
        NodeList nl2 = node1.getChildNodes();
        Node node2 = nl2.item(getNodeIndex(nl2, "text"));
        U.Log("GoogleDirection", "TotalDuration : " + node2.getTextContent());
        return node2.getTextContent();
    }

    public String getTotalDistanceText(Document doc) {
        NodeList nl1 = doc.getElementsByTagName("distance");
        Node node1 = nl1.item(nl1.getLength() - 1);
        NodeList nl2 = node1.getChildNodes();
        Node node2 = nl2.item(getNodeIndex(nl2, "text"));
        U.Log("GoogleDirection", "TotalDuration : " + node2.getTextContent());
        return node2.getTextContent();
    }

    public String getStartAddress(Document doc) {
        NodeList nl1 = doc.getElementsByTagName("start_address");
        Node node1 = nl1.item(0);
        U.Log("GoogleDirection", "StartAddress : " + node1.getTextContent());
        return node1.getTextContent();
    }

    public String getEndAddress(Document doc) {
        NodeList nl1 = doc.getElementsByTagName("end_address");
        Node node1 = nl1.item(0);
        U.Log("GoogleDirection", "StartAddress : " + node1.getTextContent());
        return node1.getTextContent();
    }

}
