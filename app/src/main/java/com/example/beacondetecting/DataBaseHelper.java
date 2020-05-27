package com.example.beacondetecting;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String dataBaseName="BeaconDetection";
    public static final String tableBaseName="BeaconImages";
    public static final String col1="ID";
    public static final String col2="Name";
    public static final String col3="Mac_Address";
    public static final String col4="Image";
    SQLiteDatabase db;

    public DataBaseHelper(@Nullable Context context) {
        super(context, dataBaseName,null,1);
         db=this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    db.execSQL("create Table "+tableBaseName+"(ID integer primary key autoincrement,Name Text,Mac_Address Text,pic Text);");

    }

    public void Insert(String na,String add,String img){
        ContentValues cv = new  ContentValues();
        cv.put("Name",na);
        cv.put("Mac_Address", add);
        cv.put("pic", img);

        db.insert(tableBaseName,null,cv);
    }

    public Cursor Select(String query){
       Cursor curs= db.rawQuery(query,null);

       return curs;
    }

    public void Delete(String query){
        db.execSQL(query);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
db.execSQL("DROP TABLE IF EXISTS "+tableBaseName+";");
onCreate(db);
    }
}
