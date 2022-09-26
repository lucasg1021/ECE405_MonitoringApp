package com.example.monitoring_system;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;


import android.os.Bundle;

import org.w3c.dom.Text;

@SuppressLint("ApplySharedPref")    // suppress "apply() instead of commit()" warning

public class MainActivity extends AppCompatActivity {

    public static final String PREFS = "MyPrefs";

    private int setTempValue, setHumidValue;
    TextView tTemp, tHumid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences settings = getSharedPreferences(PREFS, 0);
        setTempValue = settings.getInt("currentSetTemp", 70);
        setHumidValue = settings.getInt("currentSetHumid", 50);

        tTemp = findViewById(R.id.setTemp);
        tHumid = findViewById(R.id.setHumid);
    }

    @Override
    protected void onStart(){
        super.onStart();

        String outputTemp = setTempValue + "°F";
        String outputHumid = setHumidValue + "%";

        tTemp.setText(outputTemp);
        tHumid.setText(outputHumid);
    }

    public void increaseSetTemp(View v){
        // update temperature and save to preferences
        setTempValue++;
        SharedPreferences settings = getSharedPreferences(PREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("currentSetTemp", setTempValue);
        editor.commit();

        // convert temp back to string to output
        String output = setTempValue + "°F";

        // display
        ((TextView)findViewById(R.id.setTemp)).setText(output);
    }

    public void decreaseSetTemp(View v){
        // update temperature and save to preferences
        setTempValue -= 1;
        SharedPreferences settings = getSharedPreferences(PREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("currentSetTemp", setTempValue);
        editor.commit();

        // convert temp back to string to output
        String output = setTempValue + "°F";

        // display
        ((TextView)findViewById(R.id.setTemp)).setText(output);
    }

    public void increaseSetHumid(View v){
        // update humidity and save to preferences
        setHumidValue++;
        SharedPreferences settings = getSharedPreferences(PREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("currentSetHumid", setHumidValue);
        editor.commit();

        // convert updated humidity back to string
        String output = setHumidValue + "%";

        // display
        ((TextView)findViewById(R.id.setHumid)).setText(output);
    }

    public void decreaseSetHumid(View v){
        // update humidity and save to preferences
        setHumidValue -= 1;
        SharedPreferences settings = getSharedPreferences(PREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("currentSetHumid", setHumidValue);
        editor.commit();

        // convert updated humidity back to string
        String output = setHumidValue + "%";

        // display
        ((TextView)findViewById(R.id.setHumid)).setText(output);
    }
}