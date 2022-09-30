package com.example.monitoring_system;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.util.Log;
import android.os.Bundle;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.net.Socket;

@SuppressLint("ApplySharedPref")    // suppress "apply() instead of commit()" warning

public class MainActivity extends AppCompatActivity {

    public static final String PREFS = "MyPrefs";

    private int setTempValue, setHumidValue;
    TextView tTemp, tHumid;

    private Button menuTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences settings = getSharedPreferences(PREFS, 0);
        setTempValue = settings.getInt("currentSetTemp", 70);
        setHumidValue = settings.getInt("currentSetHumid", 50);

        tTemp = findViewById(R.id.setTemp);
        tHumid = findViewById(R.id.setHumid);

        menuTwo = (Button) findViewById(R.id.switchButton);
        menuTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivity2();
            }
        });
    }

    public void openActivity2(){
        Intent intent = new Intent(this, Activity2.class );
        startActivity(intent);
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

    public void pingIP(View v) {
//        try {
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
//                    .permitAll().build();
//
//            StrictMode.setThreadPolicy(policy);
//
//            int timeoutMs = 2000;
//            Socket sock = new Socket();
//            SocketAddress sockaddr = new InetSocketAddress("23.127.196.133", 54321);
//
//            sock.connect(sockaddr, timeoutMs);
//            sock.close();
//            Log.i("CONNECTION STATUS:", "connected");
//
//        } catch (IOException ioException) {
//            Log.i("CONNECTION STATUS:", "disconnected");
//        }
//
        try {
            int timeout = 2000;

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("23.127.196.133", 54321), timeout);

            OutputStream out = socket.getOutputStream();
            PrintWriter output = new PrintWriter(out);

            Log.i("CONNECTION STATUS", "sending...");
            output.println("Hello from Android");
            output.flush();
            Log.i("CONNECTION STATUS", "sent");

            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String message = null;

            while(true) {

                message = input.readLine();

                if(message != null){
                    break;
                }
            }

            Log.i("MESSAGE RECEIVED", message);


            socket.close();
        }
        catch (IOException ioException){
            Log.i("CONNECTION STATUS", "disconnected");
        }

    }
}