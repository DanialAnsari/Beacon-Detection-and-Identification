package com.example.beacondetecting;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

public class View_Image extends AppCompatActivity {

ImageView img;
Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view__image);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        img=(ImageView)findViewById(R.id.image);
        btn=(Button)findViewById(R.id.Unique);
        Bundle bundle = getIntent().getExtras();
        String message = bundle.getString("key");

        File imgFile = new  File(message);

        if(imgFile.exists()) {

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

        img.setImageBitmap(myBitmap);


        }
    }

    public void goBack(View v){
        Intent it= new Intent(View_Image.this, Main2Activity.class);
        startActivity(it);
    }

}
