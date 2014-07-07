package com.korzh.poehali.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.korzh.poehali.R;
import com.korzh.poehali.interfaces.GoogleDirectionsApi;
import com.korzh.poehali.util.C;
import com.korzh.poehali.util.G;
import com.korzh.poehali.util.U;

import org.w3c.dom.Document;

/**
 * Created by vladimir on 7/5/2014.
 */
public class NavigationRoutePicker extends ActionBarActivity {

    private View clicked = null;
    private TextView startPoint = null;
    private TextView endPoint = null;
    private TextView distance = null;
    private TextView time = null;

    private LatLng pointA = null;
    private LatLng pointB = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_picker);

        startPoint = (TextView) findViewById(R.id.txtStartPoint);
        endPoint = (TextView) findViewById(R.id.txtEndPoint);
        distance = (TextView) findViewById(R.id.txtDistance);
        time = (TextView) findViewById(R.id.txtTime);
    }

    public void pickPlace(View v) {
        clicked = v;
        Intent i = new Intent(this, PlacePicker.class);
        startActivityForResult(i, C.REQUEST_CODE_ADDRESS_INPUT);
    }

    public void onButtonClick(View v) {
        if (v.getId() == R.id.btnCalculateTrip){
            GoogleDirectionsApi gd = new GoogleDirectionsApi(this);

            gd.setOnDirectionResponseListener(new GoogleDirectionsApi.OnDirectionResponseListener() {
                public void onResponse(String status, Document doc, GoogleDirectionsApi gd) {
                    distance.setText(gd.getTotalDistanceText(doc));
                    time.setText(gd.getTotalDurationText(doc));
                    G.getInstance().currentRoute = doc;
                }
            });
            gd.request(pointA,pointB,"driving");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.route_picker, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (item.getItemId() == R.id.action_select_route) {
            if (pointA != null && pointB != null){
                Intent returnIntent = new Intent();
                Bundle b = new Bundle();
                b.putParcelable("startLatLng", pointA);
                b.putParcelable("endLatLng", pointB);
                returnIntent.putExtras(b);
                setResult(RESULT_OK, returnIntent);
            }
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == C.REQUEST_CODE_ADDRESS_INPUT) {
            if(resultCode == RESULT_OK) {
                String address = data.getStringExtra("address");
                LatLng coord = data.getParcelableExtra("latlng");
                U.Log(getClass().getName(), "onActivityResult: "+address+" "+coord);
                if (clicked.getId() == R.id.relPointA) {
                    U.Log(getClass().getName(), "point A");
                    startPoint.setText(address);
                    pointA = coord;
                }
                if (clicked.getId() == R.id.relPointB) {
                    U.Log(getClass().getName(), "point B");
                    endPoint.setText(address);
                    pointB = coord;
                }
            }
            if (resultCode == RESULT_CANCELED) {
                clicked = null;
            }
        }
    }
}
