package com.jstech.tom;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handle = new Handler();
        handle.postDelayed(new splashhandler(), 500);  //일단 0.5초 후 Main으로

    }

    private class splashhandler implements Runnable {
        @Override
        public void run() {
            startActivity(new Intent(getApplication(), LoginActivity.class));
            SplashActivity.this.finish();
        }

    }

    @Override
    public void onBackPressed() {
        //  Splash 에서 뒤로가기 막기 위함.
    }

}
