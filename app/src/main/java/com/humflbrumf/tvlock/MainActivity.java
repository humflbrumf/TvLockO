package com.humflbrumf.tvlock;

import android.R.color;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/*
 * Main Activity class that loads {@link MainFragment}.
 */
public class MainActivity extends Activity {

    final String TAG = "MainActivity";
    static boolean active = false;

    public TextView textViewBitte;
    public TextView textViewStatus;

    public ImageView imageViewPin1;
    public ImageView imageViewPin2;
    public ImageView imageViewPin3;
    public ImageView imageViewPin4;

    public int counter;
    public boolean isDigit;
    public boolean isPIN;

    Service mService;
    boolean mBound = false;

    /** Defines callbacks for service binding, passed to bindService() */
    private final ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            Service.TvLockBinder binder = (Service.TvLockBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    public void onStart(){
        Log.d(TAG,"onStart()");
        super.onStart();
        active = true;
        isPIN = false;
        textViewStatus.setText("");
        imageViewPin1.setImageDrawable(getDrawable(R.drawable.pin_empty));
        imageViewPin2.setImageDrawable(getDrawable(R.drawable.pin_empty));
        imageViewPin3.setImageDrawable(getDrawable(R.drawable.pin_empty));
        imageViewPin4.setImageDrawable(getDrawable(R.drawable.pin_empty));
        if ( !isServiceRunning(Service.class) ) {
            Log.d(TAG,"onStart() - startService()");
            Intent intent = new Intent(this, Service.class);
            startService(intent);
            Toast.makeText(this, "startService(intent)", Toast.LENGTH_SHORT).show();
        }
        else {
            Log.d(TAG,"onStart() - service already started");
        }
        if ( isServiceRunning(Service.class) ) {
            if (!mBound) {
                Log.d(TAG,"onStart() - bindService()");
                // Bind to Service
                Intent intent = new Intent(this, Service.class);
                bindService(intent, connection, Context.BIND_AUTO_CREATE);
                Toast.makeText(this, "bindService()", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG,"onStart() - already bound");
            }
        }
        else
        {
            Log.d(TAG,"onStart() - cannot bindService() - service not running");
            Toast.makeText(this, "cannot bindService() - service not running", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRestart(){
        Log.d(TAG,"onRestart()");
        super.onRestart();
        active = true;
    }
    @Override
    public void onResume(){
        Log.d(TAG,"onResume()");
        super.onResume();
        active = true;
    }
    @Override
    public void onPause(){
        Log.d(TAG,"onPause()");
        super.onPause();
        active = false;
    }
    @Override
    public void onStop(){
        Log.d(TAG,"onStop()");
        super.onStop();
        active = false;
    }

    @Override
    public void onDestroy(){
        Log.d(TAG,"onDestroy()");
        super.onDestroy();
        active = false;
        if (mBound){
            unbindService(connection);
            mBound = false;
        }
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG,"onCreate()");

        textViewBitte = findViewById(R.id.textViewBitte);
        textViewStatus = findViewById(R.id.textViewStatus);

        imageViewPin1 = findViewById(R.id.imageViewPin1);
        imageViewPin2 = findViewById(R.id.imageViewPin2);
        imageViewPin3 = findViewById(R.id.imageViewPin3);
        imageViewPin4 = findViewById(R.id.imageViewPin4);

        counter = 1;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        Log.d(TAG, "onKeyUp() = " + keyCode);
        if ( keyCode == KeyEvent.KEYCODE_S || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY ){
            if ( !isServiceRunning(Service.class) ) {
                Intent intent = new Intent(this, Service.class);
                startService(intent);
                Toast.makeText(this, "startService(intent)", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "service already started", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        if ( keyCode == KeyEvent.KEYCODE_B || keyCode == KeyEvent.KEYCODE_MEDIA_NEXT ){
            if (!mBound) {
                // Bind to Service
                Intent intent = new Intent(this, Service.class);
                bindService(intent, connection, Context.BIND_AUTO_CREATE);
                Toast.makeText(this, "bindService()", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "already bound", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        if ( keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_R ){
            return true;
        }

        if ( counter == 1 )
        {
            imageViewPin1.setImageDrawable(getDrawable(R.drawable.pin_filled));
            imageViewPin2.setImageDrawable(getDrawable(R.drawable.pin_empty));
            imageViewPin3.setImageDrawable(getDrawable(R.drawable.pin_empty));
            imageViewPin4.setImageDrawable(getDrawable(R.drawable.pin_empty));
            textViewStatus.setText("");
            if ( keyCode == KeyEvent.KEYCODE_4 )
            {
                Log.d(TAG, "onKeyUp() - " + counter + ". digit is correct");
                isDigit = true;
            }
            else
            {
                Log.d(TAG, "onKeyUp() - " + counter + ". digit is wrong");
                isDigit = false;
            }
        }
        if ( counter == 2 )
        {
            imageViewPin2.setImageDrawable(getDrawable(R.drawable.pin_filled));
            if ( keyCode == KeyEvent.KEYCODE_7 )
            {
                Log.d(TAG, "onKeyUp() - " + counter + ". digit is correct");
            }
            else
            {
                Log.d(TAG, "onKeyUp() - " + counter + ". digit is wrong");
                isDigit = false;
            }
        }
        if ( counter == 3 )
        {
            imageViewPin3.setImageDrawable(getDrawable(R.drawable.pin_filled));
            if ( keyCode == KeyEvent.KEYCODE_1 )
            {
                Log.d(TAG, "onKeyUp() - " + counter + ". digit is correct");
            }
            else
            {
                Log.d(TAG, "onKeyUp() - " + counter + ". digit is wrong");
                isDigit = false;
            }
        }
        if ( counter == 4 )
        {
            imageViewPin4.setImageDrawable(getDrawable(R.drawable.pin_filled));
            if ( keyCode == KeyEvent.KEYCODE_1 )
            {
                Log.d(TAG, "onKeyUp() - " + counter + ". digit is correct");
                if (isDigit) {
                    Log.d(TAG, "onKeyUp() - PIN IS CORRECT!!!");
                    if (mBound) {
                        Log.d(TAG, "setIsPIN(true)");
                        mService.setIsPIN(true);
                    }
                    else
                    {
                        Log.d(TAG, "cannot setIsPIN(true) - service not bound");
                        Toast.makeText(this, "cannot setIsPIN(true) - service not bound", Toast.LENGTH_SHORT).show();
                    }
                    isPIN = true;
                    textViewStatus.setText(getString(R.string.TextOkPIN));
                    textViewStatus.setTextColor(getResources().getColor(color.white));
                    textViewBitte.setTextColor(getResources().getColor(color.white));
                    MainActivity.super.moveTaskToBack(true);
                    //finish();
                }
                else
                {
                    Log.d(TAG, "onKeyUp() - PIN IS WRONG!!!");
                    textViewStatus.setText(getString(R.string.TextWrongPIN));
                    textViewStatus.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                    textViewBitte.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                    imageViewPin1.setImageDrawable(getDrawable(R.drawable.pin_empty));
                    imageViewPin2.setImageDrawable(getDrawable(R.drawable.pin_empty));
                    imageViewPin3.setImageDrawable(getDrawable(R.drawable.pin_empty));
                    imageViewPin4.setImageDrawable(getDrawable(R.drawable.pin_empty));
                }
            }
            else
            {
                Log.d(TAG, "onKeyUp() - " + counter + ". digit is wrong");
                isDigit = false;
                Log.d(TAG, "onKeyUp() - PIN IS WRONG!!!");
                textViewStatus.setText(getString(R.string.TextWrongPIN));
                textViewStatus.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                textViewBitte.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                imageViewPin1.setImageDrawable(getDrawable(R.drawable.pin_empty));
                imageViewPin2.setImageDrawable(getDrawable(R.drawable.pin_empty));
                imageViewPin3.setImageDrawable(getDrawable(R.drawable.pin_empty));
                imageViewPin4.setImageDrawable(getDrawable(R.drawable.pin_empty));
            }
            counter = 0;
        }
        counter++;
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        Log.d(TAG, "onKeyDown() = " + keyCode);
        return true;
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


}
