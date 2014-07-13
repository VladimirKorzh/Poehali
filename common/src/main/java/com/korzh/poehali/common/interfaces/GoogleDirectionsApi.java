package com.korzh.poehali.common.interfaces;

import android.content.Context;
import android.util.DisplayMetrics;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.korzh.poehali.common.R;
import com.korzh.poehali.common.util.AsyncTaskWithProgress;
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
import java.util.HashMap;

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
                + "&sensor=false&units=metric&mode=" + mode+"&language=ru&alternatives=true";


        U.Log("GoogleDirection", "URL : " + url);
        new RequestTask(mContext).execute(url);
        return url;
    }

    private class RequestTask extends AsyncTaskWithProgress<String, Void, Document> {

        public RequestTask(Context activity) {
            super(mContext);
        }

        @Override
        protected void onPreExecute(){
            dialog.setMessage(mContext.getString(R.string.dialog_title_calculating_route));
            dialog.show();
        }

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




    public HashMap<String, NodeList> getRoutes(Document doc){
        HashMap<String, NodeList> result = new HashMap<String, NodeList>();
        NodeList nodes = doc.getElementsByTagName("route");
        for (int i = 0; i<nodes.getLength(); ++i){
            NodeList childNodes = nodes.item(i).getChildNodes();

            Node summary = childNodes.item(getNodeIndex(childNodes, "summary"));
            if ("".equals(summary.getTextContent()))
                result.put(getEndAddress(childNodes), childNodes);
            else
                result.put(summary.getTextContent(), childNodes);

        }
        return result;
    }

    public PolylineOptions getPolyline(NodeList route, int width, int color) {
        ArrayList<LatLng> arr_pos = getDirection(route);
        PolylineOptions rectLine = new PolylineOptions().width(dpToPx(width)).color(color);
        for (LatLng arr_po : arr_pos) rectLine.add(arr_po);
        return rectLine;
    }

    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public ArrayList<LatLng> getDirection(NodeList route) {
        NodeList steps, singleStepNodes, internalNodes;
        Node latNode,lngNode, locationNode, polylineNode, pointsNode;

        ArrayList<LatLng> listGeopoints = new ArrayList<LatLng>();

        steps = route.item(getNodeIndex(route,"leg")).getChildNodes();

        // we found steps
        if (steps.getLength() > 0) {

            // iterate over each one
            for (int i = 0; i < steps.getLength(); i++) {

                // get particular step node
                Node step = steps.item(i);

                // only process nodes named step
                if ("step".equalsIgnoreCase(step.getNodeName())) {

                    // find whats inside this step
                    singleStepNodes = step.getChildNodes();

                    // extract start location
                    locationNode = singleStepNodes.item(getNodeIndex(singleStepNodes, "start_location"));
                    internalNodes = locationNode.getChildNodes();
                    latNode = internalNodes.item(getNodeIndex(internalNodes, "lat"));
                    lngNode = internalNodes.item(getNodeIndex(internalNodes, "lng"));
                    listGeopoints.add(new LatLng(Double.parseDouble(latNode.getTextContent()),
                            Double.parseDouble(lngNode.getTextContent())));

                    // extract polyline between points
                    polylineNode = singleStepNodes.item(getNodeIndex(singleStepNodes, "polyline"));
                    internalNodes = polylineNode.getChildNodes();
                    pointsNode = internalNodes.item(getNodeIndex(internalNodes, "points"));
                    ArrayList<LatLng> points = decodePoly(pointsNode.getTextContent());
                    for (LatLng p : points) listGeopoints.add(new LatLng(p.latitude, p.longitude));

                    // extract end location
                    locationNode = singleStepNodes.item(getNodeIndex(singleStepNodes, "end_location"));
                    internalNodes = locationNode.getChildNodes();
                    latNode = internalNodes.item(getNodeIndex(internalNodes, "lat"));
                    lngNode = internalNodes.item(getNodeIndex(internalNodes, "lng"));
                    listGeopoints.add(new LatLng(Double.parseDouble(latNode.getTextContent()),
                            Double.parseDouble(lngNode.getTextContent())));
                }
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

    public String getTotalDurationText(NodeList route) {
        NodeList steps = route.item(getNodeIndex(route,"leg")).getChildNodes();
        Node duration = steps.item(getNodeIndex(steps,"duration"));
        NodeList durationChildNodes = duration.getChildNodes();
        Node text = durationChildNodes.item(getNodeIndex(durationChildNodes, "text"));
        return text.getTextContent();
    }

    public String getTotalDistanceText(NodeList route) {
        NodeList steps = route.item(getNodeIndex(route,"leg")).getChildNodes();
        Node duration = steps.item(getNodeIndex(steps,"distance"));
        NodeList durationChildNodes = duration.getChildNodes();
        Node text = durationChildNodes.item(getNodeIndex(durationChildNodes, "text"));
        return text.getTextContent();
    }

    public String getStartAddress(NodeList route) {
        NodeList steps = route.item(getNodeIndex(route,"leg")).getChildNodes();
        Node start_address = steps.item(getNodeIndex(steps,"start_address"));
        return start_address.getTextContent();
    }

    public String getEndAddress(NodeList route) {
        NodeList steps = route.item(getNodeIndex(route,"leg")).getChildNodes();
        Node end_address = steps.item(getNodeIndex(steps,"end_address"));
        return end_address.getTextContent();
    }

}
