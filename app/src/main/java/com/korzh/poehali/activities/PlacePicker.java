package com.korzh.poehali.activities;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.korzh.poehali.R;
import com.korzh.poehali.common.interfaces.GoogleGeocodeApi;
import com.korzh.poehali.common.util.C;
import com.korzh.poehali.common.util.G;

public class PlacePicker extends ActionBarActivity implements GoogleMap.OnMapClickListener {

    private GoogleMap googleMap = null;
    private Marker selected_position_marker = null;
    private AutoCompleteTextView txtAddress = null;
    private GoogleGeocodeApi googleGeocodeApi = null;
    private LatLng latLngLastKnown = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_place_picker);
//        /getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        googleGeocodeApi = new GoogleGeocodeApi(this);
        googleGeocodeApi.setLocationResponseListener(responseListener);

        txtAddress = (AutoCompleteTextView) findViewById(R.id.txtAddress);

        txtAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    if (event != null && !event.isShiftPressed()){
                        action_search_address(v);
                        handled = true;
                    }
                }
                return handled;
            }
        });

        String[] countries = getResources().getStringArray(R.array.streets);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, countries);
        txtAddress.setAdapter(adapter);

        googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        googleMap.setOnMapClickListener(this);

        selected_position_marker = googleMap.addMarker(new MarkerOptions()
                .position(C.LOCATION_KIEV)
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.img_me))
                .visible(false));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(C.LOCATION_KIEV)
                .zoom(C.DEFAULT_MAP_ZOOM)
                .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


    GoogleGeocodeApi.onLocationResponseListener responseListener = new GoogleGeocodeApi.onLocationResponseListener() {
        @Override
        public void onResponse(LatLng latLng) {

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)
                    .zoom(C.SEARCH_ADDRESS_MAP_ZOOM)
                    .build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            selected_position_marker.setPosition(latLng);
            selected_position_marker.setVisible(true);
        }

        @Override
        public void onResponse(GoogleGeocodeApi.GeocodeApiReponse response){
            txtAddress.setText(response.Place);
            onResponse(latLngLastKnown);
        }
    };


    public void action_use_current_location(View v){
        Location lastKnown = G.getInstance().getLastKnownLocation();
        latLngLastKnown = new LatLng(lastKnown.getLatitude(),lastKnown.getLongitude());
        googleGeocodeApi.getLocationFromLanLng(latLngLastKnown);
    }

    public void action_search_address(View v){
        hideKeyboard(v);
        googleGeocodeApi.getLocationFromString(txtAddress.getText().toString()+C.CURRENT_LOCATION);
    }



    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.place_picker, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent returnIntent = new Intent();
        Bundle b = new Bundle();
        if (id == R.id.action_select_location) {
            b.putString("address", txtAddress.getText().toString());
            b.putParcelable("latlng", selected_position_marker.getPosition());
            returnIntent.putExtras(b);
            setResult(RESULT_OK, returnIntent);
            finish();
            return true;
        }


        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        selected_position_marker.setPosition(latLng);
        selected_position_marker.setVisible(true);
        latLngLastKnown = latLng;
        googleGeocodeApi.getLocationFromLanLng(latLng);
    }
}
