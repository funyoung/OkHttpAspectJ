package com.journaldev.okhttp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.journaldev.netorking.Network;
import com.journaldev.netorking.impl.HandlerListener;

import okhttp3.MediaType;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TextView txtString;
    Button asynchronousGet, synchronousGet, asynchronousPOST;

    public String url = "https://reqres.in/api/users/2";
    public String postUrl = "https://reqres.in/api/users/";
    public String postBody = "{\n" +
            "    \"name\": \"morpheus\",\n" +
            "    \"job\": \"leader\"\n" +
            "}";

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        asynchronousGet = findViewById(R.id.asynchronousGet);
        synchronousGet = findViewById(R.id.synchronousGet);
        asynchronousPOST = findViewById(R.id.asynchronousPost);

        asynchronousGet.setOnClickListener(this);
        synchronousGet.setOnClickListener(this);
        asynchronousPOST.setOnClickListener(this);

        txtString = findViewById(R.id.txtString);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.asynchronousGet:
                Network.run(url, new HandlerListener() {
                    @Override
                    public void onResult(String s) {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                txtString.setText(s);
                            }
                        });
                    }
                });
                break;
            case R.id.synchronousGet:
                Network.synchronousGet(url, new HandlerListener() {
                    @Override
                    public void onResult(String s) {
                        txtString.setText(s);
                    }
                });
                break;
            case R.id.asynchronousPost:
                Network.postRequest(postUrl, postBody, JSON);
                break;

        }
    }
}



