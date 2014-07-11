package com.korzh.poehali.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ShareActionProvider;

import com.korzh.poehali.R;
import com.korzh.poehali.fragments.ProfileFragment;

/**
 * Created by vladimir on 7/7/2014.
 */
public class Profile extends ActivityBase {

    private ProfileFragment profileFragment = null;
    ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base);

        profileFragment = new ProfileFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, profileFragment)
                .commit();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    public void onButtonClick(View v){
        profileFragment.onButtonClick(v);
    }
}
