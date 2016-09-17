package com.bigniu.localimagesfinder;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bigniu.localimagesfinder.acp.Acp;
import com.bigniu.localimagesfinder.acp.AcpListener;
import com.bigniu.localimagesfinder.acp.AcpOptions;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Acp.init(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ImageView imageView = (ImageView) findViewById(R.id.iv_main);
        assert imageView != null;
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ImgBrowsActivity.class));
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Acp.getInstance().request(new AcpOptions.Builder().setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).build(), new AcpListener() {
                @Override
                public void onGranted() {
                    imageView.setClickable(true);
                }

                @Override
                public void onDenied(List<String> permissions) {

                }
            });
        } else {
            imageView.setClickable(true);
        }
    }
}
