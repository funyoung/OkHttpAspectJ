package com.journaldev.netorking;

import android.util.Log;

import com.journaldev.netorking.impl.HandlerListener;
import com.journaldev.netorking.impl.OkHttpHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Network {
    public static void postRequest(String postUrl, String postBody, MediaType mediaType) {
        try {
            OkHttpClient client = new OkHttpClient();

            RequestBody body = RequestBody.create(mediaType, postBody);

            Request request = new Request.Builder()
                    .url(postUrl)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    call.cancel();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    Log.d("TAG", response.body().string());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void synchronousGet(String url, HandlerListener handlerListener) {
        OkHttpHandler okHttpHandler = new OkHttpHandler(handlerListener);
        okHttpHandler.execute(url);
    }

    public static void run(String url, HandlerListener listener) {
        try {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    call.cancel();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    final String myResponse = response.body().string();
                    try {
                        JSONObject json = new JSONObject(myResponse);
                        listener.onResult("First Name: "+json.getJSONObject("data").getString("first_name") + "\nLast Name: " + json.getJSONObject("data").getString("last_name"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
