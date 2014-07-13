package com.korzh.poehali.dialogs;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.korzh.poehali.R;
import com.korzh.poehali.activities.ActivityBase;
import com.korzh.poehali.common.interfaces.GoogleDirectionsApi;
import com.korzh.poehali.common.util.C;
import com.korzh.poehali.common.util.G;
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingLeftInAnimationAdapter;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;

public class NewNavigationRoute extends ActivityBase {

    private GoogleDirectionsApi gd;
    private ListView mListView = null;
    private MyPossibleRoutesAdapter mAdapter = null;
    private GoogleMap googleMap = null;


    private ArrayList<PossibleRoute> arr = new ArrayList<PossibleRoute>();
    private ArrayList<Polyline> polys = new ArrayList<Polyline>();
    private int lastSelectedRoutePosition = 0;

    private LatLng pointA, pointB;

    private int INACTIVE, ACTIVE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_route_picker);

        INACTIVE  = getResources().getColor(R.color.INACTIVE_ROUTE);
        ACTIVE    = getResources().getColor(R.color.ACTIVE_ROUTE);

        mListView = (ListView) findViewById(R.id.listPossibleRoutes);

        mAdapter = new MyPossibleRoutesAdapter(this, arr);

        final AnimationAdapter animAdapter = new SwingLeftInAnimationAdapter(mAdapter);
        animAdapter.setAbsListView(mListView);
        mListView.setAdapter(animAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // set all polys to gray
                for(Polyline p : polys){
                    p.setColor(INACTIVE);
                    p.setZIndex(0);
                }

                polys.get(position).setColor(ACTIVE);
                polys.get(position).setZIndex(100);

                lastSelectedRoutePosition = position;
            }
        });


        googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        googleMap.getUiSettings().setZoomControlsEnabled(false);


        Intent i = new Intent(this, PointPicker.class);
        i.putExtra("requestCode", C.REQUEST_CODE_NAVIGATOR_SELECT_POINT);
        startActivityForResult(i, C.REQUEST_CODE_NAVIGATOR_SELECT_POINT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == C.REQUEST_CODE_NAVIGATOR_SELECT_POINT) {
            if(resultCode == RESULT_OK) {
                Location myLoc = G.getInstance().getLastKnownLocation();
                pointA = new LatLng(myLoc.getLatitude(),myLoc.getLongitude());
                pointB = data.getParcelableExtra("latlng");

                gd = new GoogleDirectionsApi(this);
                gd.setOnDirectionResponseListener(listener);
                gd.request(pointA, pointB, "driving");
            }
            else {
                finish();
            }
        }
    }

    GoogleDirectionsApi.OnDirectionResponseListener listener = new GoogleDirectionsApi.OnDirectionResponseListener() {
        @Override
        public void onResponse(String status, Document doc, GoogleDirectionsApi gd) {
            if (status.equals("OK")){
                // place start and end markers
                googleMap.addMarker(new MarkerOptions().position(pointA)
                        .icon(BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_GREEN)));

                googleMap.addMarker(new MarkerOptions().position(pointB)
                        .icon(BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_RED)));

                // set the boundaries for the map to zoom in on the route
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(pointA);
                builder.include(pointB);
                LatLngBounds bounds = builder.build();

                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));

                HashMap<String, NodeList> routes = gd.getRoutes(doc);

                // add item to possible routes
                int i = 0;
                for (String summary : routes.keySet()){
                    PossibleRoute r = new PossibleRoute();
                    r.setSummary(summary);
                    r.setDuration(gd.getTotalDurationText(routes.get(summary)));
                    r.setLength(gd.getTotalDistanceText(routes.get(summary)));
                    r.setRoute(routes.get(summary));

                    // draw the line for this route and store it in array
                    polys.add(googleMap.addPolyline(gd.getPolyline(routes.get(summary), C.MAP_ROUTE_WIDTH_DP, INACTIVE)));
                    arr.add(r);
                    i++;
                }

                // tell the adapter that we are done
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dialog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (item.getItemId() == R.id.action_continue) {
            if (pointA != null && pointB != null){
                Intent returnIntent = new Intent();
                Bundle b = new Bundle();
                b.putParcelable("pointA", pointA);
                b.putParcelable("pointB", pointB);
                G.getInstance().currentNavigationRoute = arr.get(lastSelectedRoutePosition).getRoute();

                returnIntent.putExtras(b);
                setResult(RESULT_OK, returnIntent);
            }
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private class PossibleRoute{
        private String summary;
        private String length;
        private String duration;
        private NodeList route;

        public NodeList getRoute() {
            return route;
        }

        public void setRoute(NodeList route) {
            this.route = route;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public String getLength() {
            return length;
        }

        public void setLength(String length) {
            this.length = length;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }
    }

    private class MyPossibleRoutesAdapter extends com.nhaarman.listviewanimations.ArrayAdapter<PossibleRoute> {

        private final Context mContext;

        public MyPossibleRoutesAdapter(final Context context, final ArrayList<PossibleRoute> items) {
            super(items);
            mContext = context;
        }

        @Override
        public long getItemId(final int position) {
            return getItem(position).hashCode();
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            View tv = convertView;
            if (tv == null) {
                tv = LayoutInflater.from(mContext).inflate(R.layout.single_route, parent, false);
            }
            TextView txtSummary = (TextView) tv.findViewById(R.id.txtSummary);
            TextView txtDuration = (TextView) tv.findViewById(R.id.txtDuration);
            TextView txtLength = (TextView) tv.findViewById(R.id.txtLength);

            txtSummary.setText(mItems.get(position).getSummary());
            txtDuration.setText(mItems.get(position).getDuration());
            txtLength.setText(mItems.get(position).getLength());

            return tv;
        }
    }


}
