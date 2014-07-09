package com.korzh.poehali.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.korzh.poehali.R;
import com.korzh.poehali.common.interfaces.MOTPPhoneConfirmAPI;

public class PhoneNumberValidation extends ActivityBase {
    private MOTPPhoneConfirmAPI motpPhoneConfirmAPI = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);

        Bundle b = getIntent().getExtras();
        String number = b.getString("number");
        motpPhoneConfirmAPI = new MOTPPhoneConfirmAPI(this);
        motpPhoneConfirmAPI.setResultHandler(resultHandler);
        motpPhoneConfirmAPI.verifyPhoneNumber(number);
    }


    private MOTPPhoneConfirmAPI.onVerificationResultHandler resultHandler = new MOTPPhoneConfirmAPI.onVerificationResultHandler() {
        @Override
        public void onReceiveResult(boolean result) {
            if (result) finishWithResult(RESULT_OK);
            else finishWithResult(RESULT_CANCELED);
        }
    };

    private void finishWithResult(int res){
        Intent returnIntent = new Intent();
        setResult(res,returnIntent);
        finish();
    }

    public void onButtonClick(View v){
        switch (v.getId()){
            case R.id.btnCancelVerification:
                finishWithResult(RESULT_CANCELED);
                break;
        }
    }
}
