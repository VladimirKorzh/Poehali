package com.korzh.poehali.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.Window;

import com.korzh.poehali.R;
import com.korzh.poehali.common.interfaces.ServiceFloating;
import com.korzh.poehali.common.util.G;
import com.korzh.poehali.fragments.MainMenuFragment;

/**
 * Created by vladimir on 7/7/2014.
 */
public class MainMenu extends ActivityBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_mainmenu);
        new G(this);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, new MainMenuFragment())
                .commit();

        startService(new Intent(this, ServiceFloating.class));
    }
}
