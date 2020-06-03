package com.journaldev.okhttp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView txtString;
    private Button asynchronousGet, synchronousGet, asynchronousPOST;

    private Button switchButton;
    private TextView durationText;

    public static final String url = "https://reqres.in/api/users/2";
    public static final String postUrl = "https://reqres.in/api/users/";
    public static final String postBody = "{\n" +
            "    \"name\": \"morpheus\",\n" +
            "    \"job\": \"leader\"\n" +
            "}";

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private UserModel userModel;
    private StuModel studentModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        asynchronousGet = findViewById(R.id.asynchronousGet);
        synchronousGet = findViewById(R.id.synchronousGet);
        asynchronousPOST = findViewById(R.id.asynchronousPost);

        switchButton = findViewById(R.id.ime_switcher);
        durationText = findViewById(R.id.ime_duration);

        asynchronousGet.setOnClickListener(this);
        synchronousGet.setOnClickListener(this);
        asynchronousPOST.setOnClickListener(this);

        switchButton.setOnClickListener(this);
        durationText.setOnClickListener(this);

        txtString = findViewById(R.id.txtString);

        studentModel = new StuModel("Hello S");
        userModel = new UserModel("Hello M");
    }

    void postRequest(String postUrl, String postBody) throws IOException {

        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(JSON, postBody);

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
    }


    void run() throws IOException {

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

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            JSONObject json = new JSONObject(myResponse);
                            txtString.setText("First Name: "+json.getJSONObject("data").getString("first_name") + "\nLast Name: " + json.getJSONObject("data").getString("last_name"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.asynchronousGet:
                try {
                    run();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.synchronousGet:
                OkHttpHandler okHttpHandler = new OkHttpHandler();
                okHttpHandler.execute(url);
                break;
            case R.id.asynchronousPost:
                try {
                    postRequest(postUrl, postBody);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.ime_switcher:
            case R.id.ime_duration:
                if (isstop || 0 == startStamp) {
                    ((InputMethodManager)MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE)).showInputMethodPicker();
                } else {
                    stopWatch();
                }
                break;

        }
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        startWatch();
//    }

    public class OkHttpHandler extends AsyncTask<String, Void, String> {

        OkHttpClient client = new OkHttpClient();

        @Override
        protected String doInBackground(String... params) {

            Request.Builder builder = new Request.Builder();
            builder.url(params[0]);
            Request request = builder.build();

            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            txtString.setText(s);
        }
    }

    private final HashMap<String, List<Long>> costTrackerMap = new HashMap<>();
    private long startStamp;
    private long stopStamp;
    private boolean isstop = false;
    private final long REFRESH_INTERVAL = 100;
    private final int REFRESH_CODE = 1;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case REFRESH_CODE:
                    updateView();
                    break;
                case 0:
                    break;
            }
        }

    };

    private void startWatch() {
        startStamp = stopStamp = System.currentTimeMillis();
        isstop = false;
        updateView();
    }

    private void stopWatch() {
        stopStamp = System.currentTimeMillis();
        isstop = true;
        updateView();
    }

    private void nextTimer() {
        mHandler.sendEmptyMessageDelayed(REFRESH_CODE, REFRESH_INTERVAL);
    }

    // 添加更新ui的代码
    private void updateView() {
        long difference = (System.currentTimeMillis() - startStamp);
        if (isstop) {
            appendTrack();
            String displayText = String.format("切换输入法(%d'%d\")", difference / 1000, difference % 1000);
            durationText.setText(displayText);
        } else {
            nextTimer();
            String displayText = String.format("%d'%d\"", difference / 1000, difference % 1000);
            durationText.setText(displayText);
        }
    }

    private void appendTrack() {
        String ss= Settings.Secure.getString(getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
        final List<Long> ssTrackList;
        if (costTrackerMap.containsKey(ss)) {
            ssTrackList = costTrackerMap.get(ss);
        } else {
            ssTrackList = new ArrayList<>();
            costTrackerMap.put(ss, ssTrackList);
        }
        ssTrackList.add(stopStamp - startStamp);
        Log.i(TAG, costTrackerMap.toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        listenToImeChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopListening();
    }

    private static final String INPUT_METHOD_ACTION = "android.intent.action.INPUT_METHOD_CHANGED";

    private void listenToImeChanged() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(INPUT_METHOD_ACTION);
        registerReceiver(inputMethodChangeReceiver, intentFilter);
    }

    private void stopListening() {
        unregisterReceiver(inputMethodChangeReceiver);
    }

    private BroadcastReceiver inputMethodChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(INPUT_METHOD_ACTION)) {
                //监听到输入法发生改变
                startWatch();
            }
        }
    };
}



