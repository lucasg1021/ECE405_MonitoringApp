package com.example.monitoring_system;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Activity2 extends AppCompatActivity {

    public static String ipAddress;
    public static int portNum;

    EditText input1;
    EditText input2;

    Button submitButton;

    public static final String PREFSTwo = "PrefsTwo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

        input1 = (EditText) findViewById(R.id.input1);
        input2 = (EditText) findViewById(R.id.input2);

        SharedPreferences settings = getSharedPreferences(PREFSTwo, 0);
        ipAddress = settings.getString("ipstring", "");
        portNum = settings.getInt("Port Number", 54321);

        submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences settings = getSharedPreferences(PREFSTwo, 0);
                SharedPreferences.Editor editor = settings.edit();

                ipAddress = input1.getText().toString();
                editor.putString("ipstring", ipAddress);
                editor.commit();

                portNum = Integer.parseInt(input2.getText().toString());
                editor.putInt("Port Number", portNum);
                editor.commit();

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        input1.setText(ipAddress);
        input2.setText(String.valueOf(portNum));

    }

}