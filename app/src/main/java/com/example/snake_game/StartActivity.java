package com.example.snake_game;

// Importation des bibliothèques

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {

    private Button startButton;

    /**
     * S'exécute lors de la création de l'activité
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Instanciation des variables
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_start);
        startButton = findViewById(R.id.startButton);
    }

    /**
     * S'exécute au début de l'activité
     */
    @Override
    protected void onStart() {
        super.onStart();
        startButton.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gameActivity = new Intent(StartActivity.this,MainActivity.class);
                        startActivity(gameActivity);
            }
        });
    }
}