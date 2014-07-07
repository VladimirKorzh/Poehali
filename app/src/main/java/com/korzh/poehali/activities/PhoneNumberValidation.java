package com.korzh.poehali.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.korzh.poehali.R;
import com.korzh.poehali.interfaces.MOTPLoginAPI;

public class PhoneNumberValidation extends Activity {

    private InputMethodManager inputMethodManager;

    private MOTPLoginAPI motpLoginAPI = null;

    private RelativeLayout relCode = null;
    private RelativeLayout relPhone = null;
    private EditText editPhoneNumber = null;
    private EditText editCode = null;

    private int currentStep = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);

        editCode = (EditText) findViewById(R.id.edtConfirmationCode);

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        Bundle b = getIntent().getExtras();
        String number = b.getString("number");
        motpLoginAPI = new MOTPLoginAPI(this);
        motpLoginAPI.verifyPhoneNumber(number);
    }

    @Override
    public void onResume(){
        super.onResume();
        editCode.requestFocus();
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void onButtonClick(View v){
        Intent returnIntent = new Intent();
        switch (v.getId()){
            case R.id.btnCancelVerification:

                setResult(RESULT_CANCELED,returnIntent);
                finish();
                break;

            case R.id.btnCodeEntered:
                    if (motpLoginAPI.validateCode(String.valueOf(editCode.getText()))){
                        // code valid
                        setResult(RESULT_OK,returnIntent);
                        finish();
                    }
                    else {
                        // give user second chance
                        // DO NOTHING
                    }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
}
