package com.korzh.poehali.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.korzh.poehali.R;

/**
 * Created by vladimir on 7/6/2014.
 */
public abstract class AsyncTaskWithProgress<T,T1,T2> extends AsyncTask<T,T1,T2>{
    protected ProgressDialog dialog;
    private Context context;

    public AsyncTaskWithProgress(Context activity) {
        dialog = new ProgressDialog(activity);
        this.context = activity;
    }

    @Override
    protected void onPreExecute() {
        dialog.setMessage(context.getString(R.string.dialog_msg_progress_busy));
        dialog.show();
    }

    @Override
    protected void onPostExecute(T2 t2) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
