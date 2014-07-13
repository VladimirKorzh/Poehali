package com.korzh.poehali.dialogs;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.korzh.poehali.R;
import com.korzh.poehali.activities.ActivityBase;
import com.korzh.poehali.common.util.C;
import com.korzh.poehali.common.util.U;

/**
 * Created by vladimir on 7/5/2014.
 */
public class NewOrder extends ActivityBase {

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
        setContentView(R.layout.activity_new_order);

        startPoint = (TextView) findViewById(R.id.txtStartPoint);
        endPoint = (TextView) findViewById(R.id.txtEndPoint);

    }

    public void pickPlace(View v) {
        clicked = v;
        Intent i = new Intent(this, PointPicker.class);
        startActivityForResult(i, C.REQUEST_CODE_ADDRESS_INPUT);
    }

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
                U.Log(getClass().getSimpleName(), "onActivityResult: "+address+" "+coord);
                if (clicked.getId() == R.id.relPointA) {
                    startPoint.setText(address);
                    pointA = coord;
                }
                if (clicked.getId() == R.id.relPointB) {
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
