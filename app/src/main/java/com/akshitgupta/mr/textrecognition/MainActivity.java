package com.akshitgupta.mr.textrecognition;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    TextView text;
    SurfaceView cameraview;
    CameraSource camerasource;
    final int RequestCameraPermissionID = 1001;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode)
        {
            case RequestCameraPermissionID:
            {
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                    {
                    return ;
                    }
                    try {
                        camerasource.start(cameraview.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                }
            }
        }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraview = (SurfaceView) findViewById(R.id.Surface_view);
        text = (TextView) findViewById(R.id.text);
        TextRecognizer textrecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!textrecognizer.isOperational()) {
            Log.w("MainActivity", "Detector dependencies are not yet available");
        } else {
            camerasource = new CameraSource.Builder(getApplicationContext(), textrecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setRequestedFps(2.0f)
                    .setAutoFocusEnabled(true).build();

            cameraview.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceholder) {
                    try {


                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA},RequestCameraPermissionID);
                            return;
                        }
                        camerasource.start(cameraview.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
camerasource.stop();
                }});


            textrecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {
final SparseArray<TextBlock> item= detections.getDetectedItems();
                    if(item.size()!=0)
                    {
                        text.post(new Runnable() {
                            @Override
                            public void run() {
                                StringBuilder stringbuilder=new StringBuilder();
                                for( int i=0;i<item.size();++i)
                                {
                                    TextBlock items =item.valueAt(i);
                                    stringbuilder.append(items.getValue());
                                    stringbuilder.append("\n");

                                }

                                text.setText(stringbuilder.toString());
                            }
                        });
                }
            }


        });
    }


}}

