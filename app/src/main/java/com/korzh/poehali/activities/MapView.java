package com.korzh.poehali.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;

import com.korzh.poehali.R;
import com.korzh.poehali.fragments.MapViewFragment;

public class MapView extends ActivityBase {

    public MapViewFragment mapViewFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);


        // pre-init all the fragments
        mapViewFragment = new MapViewFragment();

        if(savedInstanceState != null) return;
        // put the initial fragment in
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, mapViewFragment)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
    }

}
