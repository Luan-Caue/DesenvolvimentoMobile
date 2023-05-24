package com.example.gmaps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

public class ContadorPasso extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;

    private boolean running = false;

    private float totalSteps = 0f;

    private float previousTotalSteps = 0f;

    private static final int SENSOR_PERMISSION_CODE = 1;

    CircularProgressBar circularProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contador_passo);

        circularProgressBar = findViewById(R.id.circularProgressBar);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        Sensor stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if(stepSensor == null){
            Toast.makeText(this, "Este dispositivo não possui Sensor de movimento, esta função esta desabilitada!", Toast.LENGTH_SHORT).show();
        }
        else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, SENSOR_PERMISSION_CODE);
            }else {
                sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        running = true;

        Sensor stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if(stepSensor == null){
            Toast.makeText(this, "Este dispositivo não possui Sensor de movimento, esta função esta desabilitada!", Toast.LENGTH_SHORT).show();
        }
        else {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        TextView tv_stepsTaken = findViewById(R.id.tv_StepsTaken);

        if (running){
            totalSteps = sensorEvent.values[0];
            int currentSteps = (int) (totalSteps - previousTotalSteps);
            tv_stepsTaken.setText(String.valueOf(currentSteps));

            float porcentagem = (totalSteps /circularProgressBar.getProgressMax() * 100);
            circularProgressBar.setProgressWithAnimation(porcentagem, 1000L);

            circularProgressBar.setProgress(circularProgressBar.getProgress() + 1);
        }


    }

    public void resetSteps(){
        TextView tv_stepsTaken = findViewById(R.id.tv_StepsTaken);
        tv_stepsTaken.setOnClickListener(view -> Toast.makeText(this, "Segure para resetar o contador!", Toast.LENGTH_SHORT).show());

        tv_stepsTaken.setOnLongClickListener(view -> {
            previousTotalSteps = totalSteps;
            saveData();
            tv_stepsTaken.setText("0");
            return true;
        });
    }

    private void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putFloat("key1", previousTotalSteps);
        editor.apply();
    }

    private void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        float savedNumber = sharedPreferences.getFloat("key1", 0f);
        previousTotalSteps = savedNumber;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == SENSOR_PERMISSION_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Sensor stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
                sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
            }
            else {
                Toast.makeText(this, "Usuário negou o acesso ao Sensor!", Toast.LENGTH_SHORT).show();
            }

        }

    }
}