package com.hhyk_sdk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.halocash.lib.interfaces.HaloCashPay;

import sdk.hhyk.com.libhhyk_sdk.PayConfig;
import sdk.hhyk.com.libhhyk_sdk.callback.PayListener;
import sdk.hhyk.com.libhhyk_sdk.control.PayManager;
import sdk.hhyk.com.libhhyk_sdk.entity.OrderModel;
import sdk.hhyk.com.libhhyk_sdk.view.DtDialog;


public class MainActivity extends AppCompatActivity {

  DtDialog mDialog;
    PayManager mPayManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPayManager=PayManager.instance(MainActivity.this);
        HaloCashPay.init(this, PayConfig.MERCHANT_ID);

        findViewById(R.id.tv_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPayManager.payMent( System.currentTimeMillis()+"", "1111", "0.2", "USD", new PayListener() {
                    @Override
                    public void onPaySuccess() {

                    }

                    @Override
                    public void onFailure(String msg) {

                    }
                });
            }
        });
    }


}
