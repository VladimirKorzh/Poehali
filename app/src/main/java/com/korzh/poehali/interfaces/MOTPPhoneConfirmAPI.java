package com.korzh.poehali.interfaces;

import android.content.Context;

import com.korzh.poehali.R;
import com.korzh.poehali.util.AsyncTaskWithProgress;
import com.korzh.poehali.util.C;
import com.korzh.poehali.util.G;
import com.korzh.poehali.util.U;

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

    private String LastCode = null;
    private Context context = null;

    public MOTPPhoneConfirmAPI(Context c){
        this.context = c;
    }

    public void verifyPhoneNumber(String number){
        new LoginSequence(context).execute(number);
    }

    public boolean validateCode(String code){
        if (LastCode == null) {
            U.Log(getClass().getName(),"ERROR: LastCode == null");
            return false;
        }

        if (code.equals(LastCode)){
            return true;
        }
        else {
            G.getInstance().showInfoDialog(context, context.getString(R.string.dialog_title_error), context.getString(R.string.dialog_msg_auth_code_invalid));
            return false;
        }
    }



    private class LoginSequence extends AsyncTaskWithProgress<String, Void, String> {

        public LoginSequence(Context activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(String... cellPhoneNumber) {
            verifyUsersPhone(cellPhoneNumber[0]);
            return "done";
        }

        private void verifyUsersPhone(String phoneNumber) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(C.MOTP_ENDPOINT_CALL + String.valueOf(phoneNumber));
            try {
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity entity = response.getEntity();

                String whatWeGot = EntityUtils.toString(entity, "UTF-8");

                JSONObject json = new JSONObject(whatWeGot);

                String status = json.getString("Status");
                String result = json.getString("Result");

                U.Log(getClass().getName(),"Status: "+status+" Result: "+result);

                if (status.equals("Success")){
                    decodeCallerNumber(result);
                }
                else {
                    G.getInstance().showInfoDialog(context, context.getString(R.string.dialog_title_error), context.getString(R.string.dialog_msg_number_error));
                }
            } catch (Exception e) {
                U.Log(getClass().getName(), "Authentication error: " + e.getMessage());
            }
        }


        private void decodeCallerNumber(String sid){
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
                U.Log(getClass().getName(),"Status: "+status+" Result: "+result);

                if (status.equals("Success")){
                    LastCode = result.substring(7);
                }
            } catch (Exception e) {
                U.Log(getClass().getName(), "Authentication error: " + e.getMessage());
            }
        }
    }
}
