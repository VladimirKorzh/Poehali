package com.korzh.poehali.activities;

import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.korzh.poehali.R;

/**
 * Created by vladimir on 7/8/2014.
 */
public class ActivityBase extends ActionBarActivity {

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
