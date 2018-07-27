package com.example.axilleas.sana;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.SQLException;

/**
 * Created by axilleas on 17/10/15.
 */
public class Exercise extends Activity implements SensorEventListener {

    TextView steps,calories,distance;
    ImageView speedi;
    Button start,reset;


    DataBase DbHelper;
///////////////////////////
private float lastX=0, lastY=0, lastZ=0;
    private long timestamp=0, previous_timestamp = 0;
    private double[] samplesX = {0,0,0,0,0,0,0,0,0,0,};
    private double[] samplesY = {0,0,0,0,0,0,0,0,0,0,};
    private double[] samplesZ = {0,0,0,0,0,0,0,0,0,0,};
    private int sample_counter = 0;

    private float a1 = 0, a2 = 0, a3 = 0, b1 = 0, b2 = 0, b3 = 0, c1 = 0, c2 = 0, c3 = 0;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private float deltaXMax = 0;
    private float deltaYMax = 0;
    private float deltaZMax = 0;

    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;

    private double threshold_error = 10;

    private int steps_walking = 0;
    private int steps_running = 0;

    //private TextView currentX, currentY, currentZ, maxX, maxY, maxZ;

    //public Vibrator v;

    private float DISTANCE = 0;
    private float SPEED = 0;
    private float CALORIES = 0;
    private int STEPS=0;

    private float Stride = 0;
    private float Height = (float) 1.88;
    private float Weight = 88;

////////////////////////


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercise);

        DbHelper = new DataBase(this);

        steps = (TextView)findViewById(R.id.steps);
        calories = (TextView)findViewById(R.id.calories);
        distance = (TextView)findViewById(R.id.distance);
        speedi = (ImageView)findViewById(R.id.speed);

        start = (Button)findViewById(R.id.start);
        reset = (Button)findViewById(R.id.reset);

        String hght = null;
        String wght = null;

        try {
            DbHelper.open();
            hght = DbHelper.retriveHeight();
            DbHelper.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            DbHelper.open();
            wght = DbHelper.retriveWeight();
            DbHelper.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        Height = (Float.parseFloat(hght))/100f;
        Weight = Float.parseFloat(wght);
///////////////////////


        //initialize vibration
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer

            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

            //sensorManager.r
            // sensorManager.registerListener((SensorEventListener) this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            float vibrateThreshold = accelerometer.getMaximumRange() / 2;
        } else {
            // fai! we dont have an accelerometer!
        }

        previous_timestamp = (System.currentTimeMillis()/ 1000) % 60 ;
        //Log.d("Previous Timestamp: ", String.valueOf(previous_timestamp));
//////////////////////////



        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tmp = (String) start.getText();
                if(tmp.equals("Start")) {
                    start.setText("Stop");
                    sensorManager.registerListener(Exercise.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);


                }else{
                    start.setText("Start");
                    sensorManager.unregisterListener(Exercise.this);
                }

            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                steps.setText("-");
                distance.setText("-");
                calories.setText("-");

                DISTANCE = 0;
                SPEED = 0;
                CALORIES = 0;
                STEPS=0;

                Stride = 0;
                sensorManager.unregisterListener(Exercise.this);
                start.setText("Start");
            }
        });
    }


    //////////////////
    public void initializeViews() {
        //currentX = (TextView) findViewById(R.id.currentX);
        //currentY = (TextView) findViewById(R.id.currentY);
        //currentZ = (TextView) findViewById(R.id.currentZ);

        //maxX = (TextView) findViewById(R.id.maxX);
        //maxY = (TextView) findViewById(R.id.maxY);
        //maxZ = (TextView) findViewById(R.id.maxZ);
    }

    //onResume() register the accelerometer for listening the events
    protected void onResume() {
        super.onResume();
        //sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        //sensorManager.registerListener((SensorEventListener) this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    //onPause() unregister the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        //sensorManager.unregisterListener((SensorEventListener) this);
    }

    //@Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    //@Override
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void onSensorChanged(SensorEvent event) {

        // clean current values
        //displayCleanValues();
        // display the current x,y,z accelerometer values
        //displayCurrentValues();
        // display the max x,y,z accelerometer values
        //displayMaxValues();

        // get the change of the x,y,z values of the accelerometer
        //values[0] = x values[1] = y values[2] = z,
        deltaX = Math.abs(lastX - event.values[0]);
        deltaY = Math.abs(lastY - event.values[1]);
        deltaZ = Math.abs(lastZ - event.values[2]);

        lastX = event.values[0];
        lastY = event.values[1];
        lastZ = event.values[2];

        a1=a2; a2=a3; b1=b2; b2=b3; c1=c2; c2=c3;

        //pairnei ana 10 samples kai ananewnei ta thresholds
        samplesX[sample_counter] = event.values[0];
        samplesY[sample_counter] = event.values[1];
        samplesZ[sample_counter] = event.values[2];
        sample_counter++;

        a3 = event.values[0];
        b3 = event.values[1];
        c3 = event.values[2];

        int thres_x=0 ,thres_y=0, thres_z=0;
        if (sample_counter>=10) {
            thres_x = wma(samplesX,10, 0, 0);
            thres_y = wma(samplesY,10, 0, 0);
            thres_z = wma(samplesZ,10, 0, 0);
        }

        //pros ta pou ginetai i kinisi
        double temp = 0;
        float delta = 0;

        if (deltaX >= deltaY && deltaX>= deltaZ) {
            threshold_error= thres_x;
            temp = HanningFilter(a3,a2,a1);
            delta = deltaX;
            Log.e("DELTA","X");
            /*if (deltaX >= deltaZ) {
                threshold_error = thres_x;
                temp = HanningFilter(a3,a2,a1);
                delta = deltaX;
            }
            else if (deltaX < deltaZ){
                threshold_error = thres_z;
                temp = HanningFilter(c3,c2,c1);
                delta = deltaZ;
            }*/
        }
        else if (deltaY >= deltaX && deltaY >= deltaZ)
        {
            threshold_error = thres_y;
            temp = HanningFilter(b3,b2,b1);
            delta = deltaY;
            Log.e("DELTA","Y");

            /*if (deltaY>=deltaZ)
            {
                threshold_error = thres_y;
                temp = HanningFilter(b3,b2,b1);
                delta = deltaY;
            }
            else if (deltaY<deltaZ)
            {
                threshold_error = thres_z;
                temp = HanningFilter(c3,c2,c1);
                delta = deltaZ;
            }*/
        }else{
            Log.e("DELTA","Z");

            threshold_error = thres_z;
            temp = HanningFilter(c3,c2,c1);
            delta = deltaZ;
        }

        Log.e("Threshold Error: ", String.valueOf(threshold_error));
        Log.e("Hanning: ", String.valueOf(temp));


        //edw xerw pros poia kateuthynsi kinoumaste
        //poio einai to error threshold
        //kai i smoothed timi tou hanning

        //timestamp = (System.currentTimeMillis()/ 1000) % 60 ;
        //Log.d("Previous Timestamp: ", String.valueOf(previous_timestamp));

        if (temp <= 0.14)
        {

            Log.d("Akinisia: ", "...");
        }
        else if (temp < 0.46 && temp>0.14 )//EDW PERPATAS
        {
            //speedi.setBackground(this.getResources().getDrawable(R.drawable.walk));

            if(delta> 10) {
                timestamp = (System.currentTimeMillis()/ 1000) % 60 ;

                steps_walking++;
                STEPS++;
                steps.setText(String.valueOf(STEPS));


                if (Math.abs(timestamp - previous_timestamp) >= 2) {

                    Log.e("Walking", String.valueOf(temp));

                    Calculate_Stride(steps_walking);
                    Log.e("Steps: ", String.valueOf(steps_walking));

                    steps_walking = 0;
                    previous_timestamp = timestamp;

                }
            }else{
                speedi.setBackground(this.getResources().getDrawable(R.drawable.stand));
            }
        }
        else if (temp >= 0.46 )//EDW TREXEIS
        {

            if(delta> 10) {
                timestamp = (System.currentTimeMillis() / 1000) % 60;
                steps_running++;
                STEPS++;
                steps.setText(String.valueOf(STEPS));

                if (Math.abs(timestamp - previous_timestamp) >= 2) {

                    Log.e("Running", String.valueOf(temp));
                    Calculate_Stride(steps_running);
                    Log.e("Steps: ", String.valueOf(steps_running));

                    steps_running = 0;
                    previous_timestamp = timestamp;

                }
            }else{
                speedi.setBackground(this.getResources().getDrawable(R.drawable.stand));
            }

        }

    }

    // Hanning filter
    public double HanningFilter(double a, double b, double c) {return 0.25 * (a + 2 * b + c);}

    private int wma(double[] a,int b,int i,double temp){

        if(b==0) {
            sample_counter=0;
            return (int) (temp / 55);
        }
        double t=b*a[i];
        return wma(a,b-1,i+1,t+temp);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void Calculate_Stride(int a) {

        if (a <= 2) Stride = Height/5;
        else if (a <= 3) Stride = Height/4;
        else if (a <= 4) Stride = Height/3;
        else if (a <= 5) Stride = Height/2;
        else if (a <= 6) Stride = (float) (Height/1.2);
        else if (a <= 8) Stride = Height;
        else Stride = (float) (1.2 * Height);
        //Stride = 35;
        //a = 2;
        if(a<3) {
            speedi.setBackground(this.getResources().getDrawable(R.drawable.walk));
        }else{
            speedi.setBackground(this.getResources().getDrawable(R.drawable.run));
        }

        DISTANCE = (float) (DISTANCE + a * Stride * 0.01);// steps * m
        SPEED = (float) (a * (Stride/2));// m
        CALORIES = CALORIES + (SPEED * (Weight/400));//

        Log.e("Distance: ", String.valueOf(DISTANCE) + "m");
        Log.e("Speed: ", String.valueOf(SPEED) + "m/s");
        Log.e("Calories: ", String.valueOf(CALORIES)+ " "+STEPS  + "cal");

        distance.setText(String.valueOf(DISTANCE));
        calories.setText(String.valueOf(CALORIES));
        //EDW EMFANIZEI STO XRISTI

    }

    /*public void displayCleanValues() {
        currentX.setText("0.0");
        currentY.setText("0.0");
        currentZ.setText("0.0");
    }*/

    // display the current x,y,z accelerometer values
//    public void displayCurrentValues() {
//        currentX.setText(Float.toString(deltaX));
//        currentY.setText(Float.toString(deltaY));
//        currentZ.setText(Float.toString(deltaZ));
//    }

    // display the max x,y,z accelerometer values
    public void displayMaxValues() {
        if (deltaX > deltaXMax) {
            deltaXMax = deltaX;
            //maxX.setText(Float.toString(deltaXMax));
        }
        if (deltaY > deltaYMax) {
            deltaYMax = deltaY;
            //maxY.setText(Float.toString(deltaYMax));
        }
        if (deltaZ > deltaZMax) {
            deltaZMax = deltaZ;
            //maxZ.setText(Float.toString(deltaZMax));
        }
    }


    }
