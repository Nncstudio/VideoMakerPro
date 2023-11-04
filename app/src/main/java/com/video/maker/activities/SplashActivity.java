package com.video.maker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.video.maker.R;

public class SplashActivity extends BaseActivity {


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_splash);


        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        }, 3000);
    }



    public void onDestroy() {
        super.onDestroy();
    }


}
