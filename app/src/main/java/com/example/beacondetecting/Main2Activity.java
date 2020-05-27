package com.example.beacondetecting;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {

    Cursor crs;
    DataBaseHelper db;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList;
    private ArrayList<String> addressList;
    private ArrayList<String> pathList;
    public final String  Tag="";
    Button Upd;
    Button del;
    Button vimg;
    String name="";
    String tempimg="";
    ListView list;
    int pos=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
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
        list=(ListView)findViewById(R.id.listOfDevices);
        arrayList = new ArrayList<String>();
        addressList=new ArrayList<String >();
        pathList=new ArrayList<String >();
        adapter=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, arrayList);
        Upd=(Button)findViewById(R.id.button);
        del=(Button)findViewById(R.id.button2);
        vimg=(Button)findViewById(R.id.button6);
        Upd.setEnabled(false);
        del.setEnabled(false);
        vimg.setEnabled(false);
        db=new DataBaseHelper(this);
        crs=db.Select("Select name,Mac_Address,pic from BEACONIMAGES");
        list.setAdapter(adapter);
        if(crs.moveToFirst()) {
            do {

                arrayList.add(crs.getString(0));
                addressList.add(crs.getString(1));
                pathList.add(crs.getString(2));

            } while (crs.moveToNext());
        }
        adapter.notifyDataSetChanged();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pos=position;
                Log.d(Tag,"onItemClick: clicked on "+arrayList.get(position));
                name=addressList.get(position);
                Upd.setEnabled(true);
                del.setEnabled(true);
                vimg.setEnabled(true);


            }
        });
    }


    public void DeleteonClick(View v){
        db.Delete("Delete From BeaconImages where Mac_Address='"+name+"'");
        Toast.makeText(getApplicationContext(),"Deletion Sucessful ", Toast.LENGTH_SHORT).show();
        arrayList.remove(pos);
        addressList.remove(pos);
        adapter.notifyDataSetChanged();

    }

    public void UpdateonClick(View v){
        pickFromGallery();

    }

    public void viewImage(View v){
        OpenImageViewer(pathList.get(pos));
    }

    public void OpenImageViewer(String path){
        Intent it= new Intent(Main2Activity.this, View_Image.class);
        it.putExtra("key",path);
        startActivity(it);
    }

    private void pickFromGallery(){
        //Create an Intent with action as ACTION_PICK
        Intent intent=new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        // Launching the Intent
        startActivityForResult(intent, 1002);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode){
                case 1002:
                    //data.getData returns the content URI for the selected Image
                    Uri selectedImage = data.getData();
                    String picturePath = getPath( this.getApplicationContext( ), selectedImage );



                    tempimg=picturePath;

                    db.Delete("Update BeaconImages Set pic='"+tempimg+"' where Mac_Address='"+name+"'");



                    Toast.makeText(Main2Activity.this,"Assigned Picture to "+name,Toast.LENGTH_SHORT).show();
                    break;
            }
    }

    public static String getPath(Context context, Uri uri ) {
        String result = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver( ).query( uri, proj, null, null, null );
        if(cursor != null){
            if ( cursor.moveToFirst( ) ) {
                int column_index = cursor.getColumnIndexOrThrow( proj[0] );
                result = cursor.getString( column_index );
            }
            cursor.close( );
        }
        if(result == null) {
            result = "Not found";
        }
        return result;
    }

    public void goBack(View v){
        Intent it= new Intent(Main2Activity.this, MainActivity.class);
        startActivity(it);
    }
}

