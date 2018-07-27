package com.example.axilleas.sana;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.sql.SQLException;

/**
 * Created by axilleas on 17/10/15.
 */
public class EditUser extends Activity {

    DataBase DbHelper;

    EditText height,weight;
    Button save;
    private static boolean storedHeight;
    private static boolean storedWeight;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DbHelper = new DataBase(this);

        height = (EditText) findViewById(R.id.height);
        weight = (EditText) findViewById(R.id.weight);
        save = (Button) findViewById(R.id.add_user);

        String hght = null;
        String wght = null;
        try {
            DbHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            hght = DbHelper.retriveHeight();
            Log.e("Test", hght);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        DbHelper.close();
//////
        try {
            DbHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            wght = DbHelper.retriveWeight();
            Log.e("Test", hght);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        DbHelper.close();

        if ( hght.equals("0") ){
            storedHeight= true;
        }else{
            storedHeight = false;
        }
/////
        if ( wght.equals("0") ){
            storedWeight= true;
        }else{
            storedWeight = false;
        }



        if (storedHeight == false){
            try {
                DbHelper.open();
                height.setText(DbHelper.retriveHeight());
                DbHelper.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
///////
        if (storedWeight == false){
            try {
                DbHelper.open();
                weight.setText(DbHelper.retriveWeight());
                DbHelper.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    DbHelper.open();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                DbHelper.insertALL(height.getText().toString(),weight.getText().toString());
                //DbHelper.insertWeight(weight.getText().toString());
                DbHelper.close();

                finish();

            }
        });



    }
}
