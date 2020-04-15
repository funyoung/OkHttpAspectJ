package com.journaldev.netorking.impl;

import android.os.AsyncTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpHandler extends AsyncTask<String, Void, Response> {
    private final HandlerListener callback;

    public OkHttpHandler(HandlerListener cb) {
        callback = cb;
    }

    OkHttpClient client = new OkHttpClient();

    @Override
    protected Response doInBackground(String... params) {
        Request.Builder builder = new Request.Builder();
        builder.url(params[0]);
        Request request = builder.build();
        try {
            return client.newCall(request).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Response s) {
        super.onPostExecute(s);
        try {
            if (null != s) {
                callback.onResult(s.body().string());
            }
        } catch (Exception ex) {
            // todo:
        }
    }
}
