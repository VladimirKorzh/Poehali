package com.korzh.poehali.common.interfaces;

import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;

import com.korzh.poehali.common.R;
import com.korzh.poehali.common.util.AsyncTaskWithProgress;
import com.korzh.poehali.common.util.C;
import com.korzh.poehali.common.util.G;
import com.korzh.poehali.common.util.U;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vladimir on 7/6/2014.
 */
public class MOTPPhoneConfirmAPI {

    private Context context = null;

    public MOTPPhoneConfirmAPI(Context c){
        this.context = c;
    }

    public void verifyPhoneNumber(String number){
        new LoginSequence(context).execute(number);
    }

    private onVerificationResultHandler resultHandler;

    public void setResultHandler(onVerificationResultHandler resultHandler) {
        this.resultHandler = resultHandler;
    }

    public interface onVerificationResultHandler {
        public void onReceiveResult(boolean result);
    };


    private class LoginSequence extends AsyncTaskWithProgress<String, Void, String> {

        public LoginSequence(Context activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(String... cellPhoneNumber) {
            String sid = verifyUsersPhone(cellPhoneNumber[0]);
            String callerNumber = decodeCallerNumber(sid);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String lastNumber = getLastCallingNumber();
            boolean verificationResult = false;
            if (callerNumber != null && lastNumber != null)
                if (callerNumber.equals(lastNumber)){
                    verificationResult = true;
                }
            resultHandler.onReceiveResult(verificationResult);
            return "done";
        }

        private String verifyUsersPhone(String phoneNumber) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(C.MOTP_ENDPOINT_CALL + String.valueOf(phoneNumber));
            try {
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity entity = response.getEntity();

                String whatWeGot = EntityUtils.toString(entity, "UTF-8");

                JSONObject json = new JSONObject(whatWeGot);

                String status = json.getString("Status");
                String result = json.getString("Result");

                U.Log(getClass().getSimpleName(),"Status: "+status+" Result: "+result);

                if (status.equals("Success")){
                    return result;
                }
                else {
                    G.getInstance().showInfoDialog(context, context.getString(R.string.dialog_title_error), context.getString(R.string.dialog_msg_number_error));
                }
            } catch (Exception e) {
                U.Log(getClass().getSimpleName(), "Authentication error: " + e.getMessage());
            }
            return null;
        }


        private String decodeCallerNumber(String sid){
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(C.MOTP_ENDPOINT_CONFIRM + sid);
            try {

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("private", C.MOTP_PRIVATE_KEY));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity entity = response.getEntity();

                String whatWeGot = EntityUtils.toString(entity, "UTF-8");

                JSONObject json = new JSONObject(whatWeGot);

                String status = json.getString("Status");
                String result = json.getString("Result");
                U.Log(getClass().getSimpleName(),"Status: "+status+" Result: "+result);

                if (status.equals("Success")){
                    return result;
                }
            } catch (Exception e) {
                U.Log(getClass().getSimpleName(), "Authentication error: " + e.getMessage());
            }
            return null;
        }

        private String getLastCallingNumber(){
            String[] projection = new String[]{CallLog.Calls.NUMBER};
            Cursor cur = context.getContentResolver().query(CallLog.Calls.CONTENT_URI,
                    projection, null, null, CallLog.Calls.DATE +" desc");
            int numberColumn = cur.getColumnIndex(CallLog.Calls.NUMBER);
            cur.moveToFirst();
            String number = cur.getString(numberColumn);
            number = number.substring(1);
            U.Log(getClass().getSimpleName(), "Last number: " + number);
            return number;
        }
    }
}
