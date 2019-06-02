package cn.sl.player.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import cn.sl.player.R;

public class StartActivity extends AppCompatActivity {

    private Button button;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    int count = msg.arg1;
                    if(count > 0){
                        button.setText(count + " S 跳过");
                    }else{
                        startActivity(new Intent(StartActivity.this,MainActivity.class));
                    }
                    break;
            }

        }
    };
    private int count = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        setPermissions();
        initViews();

    }

    private void initViews() {
        count = 3;
        button = findViewById(R.id.start_skip);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count = -1;
                startActivity(new Intent(StartActivity.this,MainActivity.class));
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(count >= 0){
                    Message msg = handler.obtainMessage();
                    msg.what = 0;
                    msg.arg1 = count;
                    handler.sendMessage(msg);
                    count--;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

//    private Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//
//            count -= 1;
//            handler.postDelayed(this,1000);
//
//        }
//    };


    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static int REQUEST_PERMISSION_CODE = 1;

    public void setPermissions(){
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            }
        }
    }
}
