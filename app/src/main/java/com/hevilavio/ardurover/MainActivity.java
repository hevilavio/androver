package com.hevilavio.ardurover;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.hevilavio.ardurover.bluetooth.BTConnectionInterface;
import com.hevilavio.ardurover.util.Constants;

/**
 * Main screen of the App
 * */
public class MainActivity extends AppCompatActivity {

    final String tag = Constants.LOG_TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        configurePairButton();

        configureAcelerometerButton();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BTConnectionInterface.getInstance().stop();
    }

    private void configureAcelerometerButton() {
        Log.d(tag, "M=configureAccelerometerButton.start");

        Button btnTestAccelerometer = (Button) findViewById(R.id.btn_test_acelerometer);
        final Intent roverControlIntent = new Intent(this, RoverControlActivity.class);

        btnTestAccelerometer.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Log.d(tag, "M=btnTestAccelerometer.OnClickListener, starting new activity...");

                startActivity(roverControlIntent);
            }
        });

        Log.i(tag, "M=configureAcelerometerButton.finish");
    }

    private void configurePairButton() {
        Log.d(tag, "M=configurePairButton.start");

        Button btnPair = (Button) findViewById(R.id.btn_pair);
        final Intent controlIntent = new Intent(this, ControlActivity.class);

        btnPair.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Log.d(tag, "M=btnPair.OnClickListener, starting new activity...");

                if(BTConnectionInterface.getInstance().isBluetoothActive()){
                    startActivity(controlIntent);
                }
                else{
                    disabledBluetoothAlert(view);
                }
            }
        });

        Log.i(tag, "M=configurePairButton.finish");
    }

    private void disabledBluetoothAlert(View view) {
        Snackbar.make(view, "Bluetooth aparentemente desativado", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically onReceive clicks on the Home/Up button, so long
        // as you specify disabledBluetoothAlert parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
