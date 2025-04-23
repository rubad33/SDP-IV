package com.bongoacademy.masterjuba_new;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

public class JubaActivity extends AppCompatActivity {

    public static String NAME = "";
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juba);
        button = findViewById(R.id.button);

        button.setText(NAME);
    }
}