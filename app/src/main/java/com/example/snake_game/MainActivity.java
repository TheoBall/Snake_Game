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
import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    // Instanciation des variables
    // Ces valeurs sont calculées sur le fait qu'il y a 7 déplacements possibles en hauteur
    // et 15 en largeurs sur l'écran
    private final int MAX_WIDTH_TILE_VALUE = 15;
    private final int MAX_HEIGHT_TILE_VALUE = 7;
    // Ces valeurs servent à décaler l'apparition de l'étoile de sorte à ce qu'elle ne se
    // trouve pas entre deux cases
    private final int SCREEN_WIDTH_GAP_VALUE = 54;
    private final int SCREEN_HEIGHT_GAP_VALUE = 5;
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
    private int score = 0;
    private int cooldownValue = 5;
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
        score = 0;
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
            double roundedX = Math.round(x * 100.0) / 100.0;
            double roundedY = Math.round(y * 100.0) / 100.0;
            gravitometerValues.setText("X: " + roundedX + "\nY: " + roundedY + "\nScore: " + score);
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
     * @param x direction x de l'inclinaison du téléphone
     * @param y direction y de l'inclinaison du téléphone
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
                snakeAcceleration();
                cooldown = cooldownValue;
        }
        cooldown--;
    }

    /**
     * Sert à changer la direction du serpent et à faire qu'il ne puisse pas retourner sur ses pas
     * @param x inclinaison du téléphone sur l'axe x
     * @param y inclinaison du téléphone sur l'axe y
     */
    private void rotateSnake(float x, float y) {
        if (x > 2 && directionX != -MOVEMENT_VALUE) {
            directionX = MOVEMENT_VALUE;
            directionY = 0;
            snakeHead.setRotation(90);
        } else if (x < -2 && directionX != MOVEMENT_VALUE) {
            directionX = -MOVEMENT_VALUE;
            directionY = 0;
            snakeHead.setRotation(-90);
        } else if (y > 2 && directionY != -MOVEMENT_VALUE) {
            directionY = MOVEMENT_VALUE;
            directionX = 0;
            snakeHead.setRotation(180);
        } else if (y < -2 && directionY != MOVEMENT_VALUE) {
            directionY = -MOVEMENT_VALUE;
            directionX = 0;
            snakeHead.setRotation(0);
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

    /**
     * Fais grossir le serpent en ajoutant un segment à sa liste
     */
    private void snakeGrown() {
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
        score++;
        respawnStar();
    }

    private void snakeAcceleration() {
        switch(score){
            case 2: cooldownValue=4; break;
            case 10: cooldownValue=3; break;
            case 20: cooldownValue=2; break;
            case 40: cooldownValue=1; break;
        }
    }

    /**
     * Fais réapparaitre l'étoile à un autre endroit sur le terrain
     */
    private void respawnStar() {
        int newX = 0;
        int newY = 0;
        Random random = new Random();
        newX = random.nextInt(MAX_WIDTH_TILE_VALUE)*MOVEMENT_VALUE+SCREEN_WIDTH_GAP_VALUE;
        newY = random.nextInt(MAX_HEIGHT_TILE_VALUE)*MOVEMENT_VALUE+SCREEN_HEIGHT_GAP_VALUE;
        etoile.setX(newX);
        etoile.setY(newY);
    }

    /**
     * Game Over
     */
    private void killSnake() {
        this.finish();
    }
}