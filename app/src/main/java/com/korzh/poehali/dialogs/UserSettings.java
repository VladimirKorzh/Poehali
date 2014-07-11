package com.korzh.poehali.dialogs;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

import com.korzh.poehali.R;

/**
 * Created by vladimir on 7/10/2014.
 */
public class UserSettings extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.activity_open_main,R.anim.activity_close_next);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}