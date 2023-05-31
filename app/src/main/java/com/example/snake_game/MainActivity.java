package com.example.snake_game;

// Importation des bibliothèques
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    // Instanciation des variables
    private final int MAX_COOLDOWN_VALUE = 5;
    // Permet d'instancier une valeur en dp (50 dp)
    //DisplayMetrics displayMetrics_dp = getResources().getDisplayMetrics();
    //private final int MOVEMENT_VALUE = (int) (50 * displayMetrics_dp.density);
    private final int MOVEMENT_VALUE = 140;
    private SensorManager sensorManager;
    private Sensor gravitometer;
    private TextView gravitometerValues;
    private ImageView snakeHead;
    private ImageView snakeSegment;
    private int screenWidth;
    private int screenHeight;

    private int cooldown = 0;
    private int directionX = 0;
    private int directionY = 0;

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
        snakeSegment = findViewById(R.id.snakeSegment);
        gravitometerValues = findViewById(R.id.gravitometerValues);
        snakeHead.setX(0);
        snakeHead.setY(0);
        snakeSegment.setX(0);
        snakeSegment.setY(0);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gravitometer = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        cooldown = 10;
        directionX = 0;
        directionY = 0;
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
                snakeSegment.setX(oldHeadX);

                float oldHeadY = snakeHead.getY();
                float newY;
                newY = snakeHead.getY() + directionY;

                if (newX < -20 || newY < -20
                        || newX > (getWindowManager().getDefaultDisplay().getWidth() - MOVEMENT_VALUE)
                        || newY > (getWindowManager().getDefaultDisplay().getHeight() - MOVEMENT_VALUE)) {
                    killSnake();
                }
                snakeHead.setY(newY);
                snakeSegment.setY(oldHeadY);
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

    private void killSnake() {
        this.finish();
    }
}