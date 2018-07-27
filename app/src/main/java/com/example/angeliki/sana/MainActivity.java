package com.example.axilleas.sana;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.sql.SQLException;

public class MainActivity extends Activity {

    private DataBase DbHelper;
    Context ctx = this;
    String text1;

    public static boolean storedHeight = false;

    TextView steps,calories,distance,run;
    Button exercise_btn,user_btn,log_btn,fb_btn;
    EditText height;
//////////////////

////////////////////



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        //Log.e("Test",Setting.)

        user_btn = (Button)findViewById(R.id.user);
        exercise_btn = (Button)findViewById(R.id.exercise);
        log_btn= (Button)findViewById(R.id.log);
        fb_btn= (Button)findViewById(R.id.fb);






        user_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditUser.class);
                MainActivity.this.startActivity(intent);

            }
        });

        exercise_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Exercise.class);
                MainActivity.this.startActivity(intent);

            }
        });

        log_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LogUser.class);
                MainActivity.this.startActivity(intent);

            }
        });

        fb_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    shareTextUrl();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        });


    }

    private void shareTextUrl() throws SQLException {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

//        DbHelper.open();
//        text1 =  DbHelper.retriveHeight();
//        DbHelper.close();


        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, "Title Of The Post");
        //share.putExtra(Intent.EXTRA_TEXT,text1);
        share.putExtra(Intent.EXTRA_TEXT, "http://www.vidavo.eu/index.php/en/");

        startActivity(Intent.createChooser(share, "Share link!"));
    }

    ///////////////////////////////////



}



