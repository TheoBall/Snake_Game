package com.example.snake_game;

// Importation des bibliothèques
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Image;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    // Instanciation des variables
    private final int MAX_COOLDOWN_VALUE = 5;
    private final int MOVEMENT_VALUE = 140;
    private SensorManager sensorManager;
    private Sensor gravitometer;
    private TextView gravitometerValues;
    private ImageView snakeHead;
    private ImageView startSnakeSegment;
    private ImageView etoile;
    private ConstraintLayout gameLayout;
    private int cooldown = 0;
    private int directionX = 0;
    private int directionY = 0;
    ArrayList<SnakeSegment> snakeSegmentList  = new ArrayList<>();

    /**
     * S'exécute lors de la création de l'activité
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Instanciation des variables
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);
        snakeHead = findViewById(R.id.snakeHead);
        etoile = findViewById(R.id.etoile);
        startSnakeSegment = findViewById(R.id.snakeSegment);
        gravitometerValues = findViewById(R.id.gravitometerValues);
        gameLayout = findViewById(R.id.gameLayout);
        snakeHead.setX(0);
        snakeHead.setY(0);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gravitometer = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        cooldown = 10;
        directionX = 0;
        directionY = -MOVEMENT_VALUE;
        snakeSegmentList.add(new SnakeSegment(startSnakeSegment));
    }

    /**
     * S'exécute quand l'activité est active
     */
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, gravitometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * S'exécute quand l'activité est en pause
     */
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    /**
     * Change les valeurs des variables quand le capteur bouge
     * @param event
     */
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
            float x = event.values[0];
            float y = event.values[1];
            gravitometerValues.setText("X: " + x + "\nY: " + y);
            moveSnake(x, y);
        }
    }

    /**
     * Le listener demande cette fonction mais nous ne l'utilisons pas
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    /**
     * Bouge le serpent et ses parties en fonction des directions X et Y
     */
    private void moveSnake(float x, float y) {
        if (cooldown == 0) {
            rotateSnake(y, x);
                float oldHeadX = snakeHead.getX();
                float newX;
                newX = snakeHead.getX() + directionX;

                snakeHead.setX(newX);

                float oldHeadY = snakeHead.getY();
                float newY;
                newY = snakeHead.getY() + directionY;

                if (newX < -20 || newY < -20
                        || newX > (getWindowManager().getDefaultDisplay().getWidth() - MOVEMENT_VALUE)
                        || newY > (getWindowManager().getDefaultDisplay().getHeight() - MOVEMENT_VALUE)) {
                    killSnake();
                }
                snakeHead.setY(newY);
                int i = 0;
                for (SnakeSegment segment: snakeSegmentList) {
                    if (i == 0) {
                        segment.moveSegment(oldHeadX, oldHeadY);
                    } else {
                        segment.moveSegment(snakeSegmentList.get(i-1).getPreviousX(), snakeSegmentList.get(i-1).getPreviousY());
                    }
                    i++;
                }
                snakeCollision();
                cooldown = MAX_COOLDOWN_VALUE;
        }
        cooldown--;
    }

    /**
     * Sert à changer la direction du serpent et à faire qu'il ne puisse pas retourner sur ses pas
     * @param x
     * @param y
     */
    private void rotateSnake(float x, float y) {
        if (y > 2 && directionY != -MOVEMENT_VALUE) {
            directionY = MOVEMENT_VALUE;
            directionX = 0;
            snakeHead.setRotation(180);
        } else if (y < -2 && directionY != MOVEMENT_VALUE) {
            directionY = -MOVEMENT_VALUE;
            directionX = 0;
            snakeHead.setRotation(0);
        } else if (x > 2 && directionX != -MOVEMENT_VALUE) {
            directionX = MOVEMENT_VALUE;
            directionY = 0;
            snakeHead.setRotation(90);
        } else if (x < -2 && directionX != MOVEMENT_VALUE) {
            directionX = -MOVEMENT_VALUE;
            directionY = 0;
            snakeHead.setRotation(-90);
        }
    }

    /**
     * Sert à vérifier si le serpent est en collision avec une étoile ou un segment, si étoile, déclenche la fonction qui
     * fait grandir le serpent et fait réapparaître l'étoile, si segment, déclenche la méthode killSnake
     */
    private void snakeCollision() {
        Rect snakeHeadRect = new Rect(Math.round(snakeHead.getX()), Math.round(snakeHead.getY()), Math.round(snakeHead.getX() + snakeHead.getWidth()), Math.round(snakeHead.getY() + snakeHead.getHeight()));
        Rect etoileRect = new Rect(Math.round(etoile.getX()), Math.round(etoile.getY()), Math.round(etoile.getX() + etoile.getWidth()), Math.round(etoile.getY() + etoile.getHeight()));
        // Vérifier la collision avec etoile
        if (snakeHeadRect.intersect(etoileRect)) {
            snakeGrown();
        }
        for (SnakeSegment segment: snakeSegmentList) {
            Rect segmentRect = new Rect(Math.round(segment.getX()+10), Math.round(segment.getY()+10), Math.round(segment.getX()-10 + segment.getWidth()), Math.round(segment.getY()-10 + segment.getHeight()));

            if (snakeHeadRect.intersect(segmentRect)) {
                killSnake();
            }
        }
    }

    public void snakeGrown() {
        // Obtenir le dernier segment
        SnakeSegment lastSegment = snakeSegmentList.get(snakeSegmentList.size() - 1);

        // Récupérer les coordonnées précédentes du dernier segment
        float previousX = lastSegment.getPreviousX();
        float previousY = lastSegment.getPreviousY();

        // créer le nouveau segment et lui donne les coordonnées précédantes du dernier segment
        ImageView newSegmentImage = new ImageView(this);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int dp = 50; // Taille en dp
        int tailleDp = (int) (dp * displayMetrics.density);
        newSegmentImage.setMaxWidth(tailleDp);
        newSegmentImage.setMaxHeight(tailleDp);
        newSegmentImage.setAdjustViewBounds(true);
        newSegmentImage.setImageResource(R.drawable.snake_segment);
        SnakeSegment newSegment = new SnakeSegment(newSegmentImage);
        newSegment.setX(previousX);
        newSegment.setY(previousY);

        // Ajouter le nouveau segment à la liste
        snakeSegmentList.add(newSegment);
        gameLayout.addView(newSegmentImage);
    }

    private void killSnake() {
        this.finish();
    }
}