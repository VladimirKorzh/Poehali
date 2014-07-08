package com.korzh.poehali.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.korzh.poehali.R;
import com.korzh.poehali.activities.MapView;
import com.korzh.poehali.activities.NavigationRoutePicker;
import com.korzh.poehali.common.interfaces.GoogleDirectionsApi;
import com.korzh.poehali.common.interfaces.LocationBroadcaster;
import com.korzh.poehali.common.interfaces.GlobalAnnounceInterface;
import com.korzh.poehali.common.util.C;
import com.korzh.poehali.common.util.G;
import com.korzh.poehali.common.util.U;

import org.w3c.dom.Document;

public class MapViewFragment extends Fragment {

    public int MAP_MODE = C.MAP_MODE_BIRDVIEW;

    private GoogleMap googleMap = null;

    private Marker myLocationMarker = null;
    private Marker myDrivingMarker = null;
    private Circle mySearchRadiusCircle = null;

    private Location lastKnownLocation = null;

    private GlobalAnnounceInterface globalAnnounceInterface = null;
    private LocationBroadcaster locationBroadcaster = null;

    private RelativeLayout sendMarkPolice = null;
    private GoogleDirectionsApi gd = null;


    private LatLng getMapCenterForDriving(LatLng latLng, double bearing, float distance){
        double dist = distance/6371.0;
        double brng = Math.toRadians(bearing);
        double lat1 = Math.toRadians(latLng.latitude);
        double lon1 = Math.toRadians(latLng.longitude);

        double lat2 = Math.asin( Math.sin(lat1)*Math.cos(dist) + Math.cos(lat1)*Math.sin(dist)*Math.cos(brng) );
        double a = Math.atan2(Math.sin(brng)*Math.sin(dist)*Math.cos(lat1), Math.cos(dist)-Math.sin(lat1)*Math.sin(lat2));
        System.out.println("a = " +  a);
        double lon2 = lon1 + a;

        lon2 = (lon2+ 3*Math.PI) % (2*Math.PI) - Math.PI;

        System.out.println("Latitude = "+Math.toDegrees(lat2)+"\nLongitude = "+Math.toDegrees(lon2));

        return new LatLng(Math.toDegrees(lat2),Math.toDegrees(lon2));
    }


    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (googleMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            googleMap = ((SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            // Check if we were successful in obtaining the map.
            if (googleMap != null) {
                globalAnnounceInterface = new GlobalAnnounceInterface(googleMap);
                globalAnnounceInterface.getWhatsAnnouncedGlobally();
                locationBroadcaster = new LocationBroadcaster(getActivity(), googleMap);
                locationBroadcaster.StartBroadcast();
                locationBroadcaster.StartListener();
                setUpMap();
            }
        }

    }

    LocationListener mapGPSupdater = new LocationListener() {
        public void onLocationChanged(Location location) {
            U.Log("Location changed", lastKnownLocation + " " + location);

            if (lastKnownLocation != null && lastKnownLocation != location){
                // if our location has changed
                LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

                if (MAP_MODE == C.MAP_MODE_NAVIGATION) {
                    // play the animation in navigation move
                    float bearing = lastKnownLocation.bearingTo(location);

                    LatLng cameraDrivingPos = getMapCenterForDriving(latLng,bearing,1);

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(cameraDrivingPos)
                            .bearing(bearing)
                            .zoom(C.NAVIGATION_MAP_ZOOM)
                            .tilt(C.NAVIGATION_MAP_TILT)
                            .build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
                if (MAP_MODE == C.MAP_MODE_BIRDVIEW){
                    // play the animation in birdview
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(latLng)
                            .zoom(C.DEFAULT_MAP_ZOOM)
                            .build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }

                myLocationMarker.setPosition(latLng);
                myDrivingMarker.setPosition(latLng);
                mySearchRadiusCircle.setCenter(latLng);
            }
            if (lastKnownLocation == null) lastKnownLocation = location;
            if (lastKnownLocation.distanceTo(location) > C.MIN_BEARING_CHANGE_DISTANCE){
                lastKnownLocation = location;
            }
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {}

        public void onProviderEnabled(String provider) {}

        public void onProviderDisabled(String provider) {}
    };








    public void switchMode(int mode){
        lastKnownLocation = G.getInstance().getLastKnownLocation();

        MAP_MODE = mode;

        if (lastKnownLocation == null) {
            lastKnownLocation = new Location("default");
            lastKnownLocation.setLatitude(C.LOCATION_KIEV.latitude);
            lastKnownLocation.setLongitude(C.LOCATION_KIEV.longitude);
        }

        LatLng latLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

        if (MAP_MODE == C.MAP_MODE_BIRDVIEW) {
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setAllGesturesEnabled(true);
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)
                    .zoom(C.DEFAULT_MAP_ZOOM)
                    .build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            mySearchRadiusCircle.setVisible(true);
            sendMarkPolice.setVisibility(View.INVISIBLE);

            myLocationMarker.setVisible(true);
            myDrivingMarker.setVisible(false);
        }

        if (MAP_MODE == C.MAP_MODE_NAVIGATION) {
            googleMap.getUiSettings().setZoomControlsEnabled(false);
            googleMap.getUiSettings().setAllGesturesEnabled(false);

            LatLng cameraDrivingPos = getMapCenterForDriving(latLng,0,0.1f);

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(cameraDrivingPos)
                    .bearing(0)
                    .zoom(C.NAVIGATION_MAP_ZOOM)
                    .tilt(C.NAVIGATION_MAP_TILT)
                    .build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            mySearchRadiusCircle.setVisible(false);
            sendMarkPolice.setVisibility(View.VISIBLE);

            myLocationMarker.setVisible(false);
            myDrivingMarker.setVisible(true);
        }

        myLocationMarker.setPosition(latLng);
        myDrivingMarker.setPosition(latLng);
        mySearchRadiusCircle.setCenter(latLng);
    }

    private void setUpMap() {

        googleMap.getUiSettings().setCompassEnabled(false);
        googleMap.setTrafficEnabled(true);

        myLocationMarker = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
//                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.img_me))
                .visible(false)
                .flat(false));

        myDrivingMarker = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                        //              .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.img_driving_me))
                .visible(false)
                .flat(true));

        mySearchRadiusCircle = googleMap.addCircle(new CircleOptions()
                .center(new LatLng(0,0))
                .radius(C.ORDER_SEARCH_RADIUS)
                .strokeWidth(0)
                .strokeColor(Color.LTGRAY)
                .fillColor(C.SEARCH_CIRCLE_COLOR)
                .visible(false));

        switchMode(C.MAP_MODE_BIRDVIEW);
    }

    public MapViewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            SupportMapFragment fragment = (SupportMapFragment) getActivity()
                    .getSupportFragmentManager().findFragmentById(R.id.map);
            if (fragment != null) {
                getFragmentManager().beginTransaction().remove(fragment).commit();
                googleMap = null;
            }

        } catch (IllegalStateException e) {
            //handle this situation because you are necessary will get
            //an exception here :-(
            U.Log("FUCKING MAP","");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map_view, container, false);

        G.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, C.MAP_POSITION_UPD8_MILLIS, C.MAP_MIN_DISTANCE_UPD8, mapGPSupdater);

        sendMarkPolice = (RelativeLayout) rootView.findViewById(R.id.btnMarkPolice);
        sendMarkPolice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userActionMarkPolice(view);
            }
        });

        G.getInstance().checkGPSEnabled(getActivity());

        setUpMapIfNeeded();
        setHasOptionsMenu(true);





        return rootView;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        try {
            SupportMapFragment fragment = (SupportMapFragment) getActivity()
                    .getSupportFragmentManager().findFragmentById(R.id.map);
            if (fragment != null) {
                getFragmentManager().beginTransaction().remove(fragment).commit();
                googleMap = null;
            }

        } catch (IllegalStateException e) {
            //handle this situation because you are necessary will get
            //an exception here :-(
            U.Log("FUCKING MAP","");
        }

        locationBroadcaster.StopListener();
        locationBroadcaster.StopBroadcast();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_switch_map_view) {
            Toast.makeText(getActivity(), "Map view switched.", Toast.LENGTH_SHORT).show();
            MapViewFragment mv = ( (MapView) getActivity()).mapViewFragment;

            mv.switchMode(mv.MAP_MODE==C.MAP_MODE_BIRDVIEW?C.MAP_MODE_NAVIGATION:C.MAP_MODE_BIRDVIEW);
            return true;
        }

//        if (item.getItemId() == R.id.action_UpdatePoliceLocations){
//            MapViewFragment mv = ( (MapView) getActivity()).mapViewFragment;
//            mv.btnUpdatePoliceLocations(null);
//            return true;
//        }

        if (item.getItemId() == R.id.action_setNewRouteManually){
            Intent myIntent = new Intent(getActivity(), NavigationRoutePicker.class);
            startActivityForResult(myIntent, C.REQUEST_CODE_NEW_ROUTE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.map_view, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void userActionMarkPolice(View v){
        globalAnnounceInterface.announcePolice();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == C.REQUEST_CODE_NEW_ROUTE) {

            if (resultCode == Activity.RESULT_OK) {

                gd = new GoogleDirectionsApi(getActivity());

                Document doc = G.getInstance().currentRoute;
                LatLng start = data.getParcelableExtra("startLatLng");
                LatLng end = data.getParcelableExtra("endLatLng");
                googleMap.addPolyline(gd.getPolyline(doc, 3, Color.RED));

                googleMap.addMarker(new MarkerOptions().position(start)
                        .icon(BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_GREEN)));

                googleMap.addMarker(new MarkerOptions().position(end)
                        .icon(BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_GREEN)));
            }
        }
    }

}
