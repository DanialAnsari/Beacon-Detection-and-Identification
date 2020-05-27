package com.example.beacondetecting;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.LauncherActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanRecord;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.SoundPool;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URL;
import java.nio.channels.GatheringByteChannel;
import java.sql.Blob;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView list;
    Button btn;
    TextView tx;
    int count=0;
    int pointer=-1;
    boolean run=false;
    boolean check4reg=false;
    CheckBox checkBox;
    ImageView img;
    String tempimg;
    String tempname;
    String tempAdd;
    Thread th;
    DecimalFormat df = new DecimalFormat("#.####");

    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList;
    private ArrayList<String> addressList;
    private ArrayList<String> addressList2;
    int b=1;
    double [] a=new double[100];
public static final String TAG="Hello";
public Cursor crs;
    BluetoothAdapter bt;

    DataBaseHelper db;

    private final BroadcastReceiver BroadCastReviever = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {

                final int state=intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,bt.ERROR);

                switch (state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG,"onRecieve: State  Off");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG,"onRecieve: State Turning OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG,"onRecieve: State ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG,"onRecieve: State Turning ON");
                        break;

                }
            }
        }
    };

    private BroadcastReceiver DeviceCheck=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            final String action=intent.getAction();
            Log.d(TAG,"onRecieve: Action Found");

            if(action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);


                Log.d(TAG, "OnRecieve: Name=" + device.getName());
                boolean hello2 = false;
                if(check4reg==true) {

                    if (crs.moveToFirst()) {
                        do {


                            if (device.getAddress().equals(crs.getString(0))) {
                                hello2 = false;

                                break;
                            }
                            else{
                                hello2=true;
                            }

                        } while (crs.moveToNext());
                    }
                    else{
                        hello2=true;

                    }
                }

                if(hello2==false) {
                    int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);

                    String tsString = tx.getText().toString();
                    int txnum = Integer.parseInt(tsString);
                    double distance = (txnum - rssi) / 20.0;
                    distance = Math.pow(10.0, distance);
                    distance = distance * 100.0;


                    if (distance <= 500) {
                        if (b == 0) {
                            b = 1;
                        }

                        boolean check = true;
                        if (pointer > -1) {
                            for (int i = 0; i <= pointer; i++) {

                                if (addressList.get(i).equals(device.getAddress())) {
                                    //Toast.makeText(getApplicationContext(),"List "+device.getAddress()+" "+addressList.get(i), Toast.LENGTH_SHORT).show();
                                    addressList2.set(i, device.getAddress() + " " + df.format(distance) + " cm");
                                    check = false;
                                    a[i] = distance;

                                }
                            }
                        }
                        if (check == true) {
                            pointer++;
                            a[pointer] = rssi;
                            arrayList.add(device.getName());
                            addressList.add(device.getAddress());
                            addressList2.add(device.getAddress() + " " + df.format(distance) + " cm");
                        }




                        adapter.notifyDataSetChanged();
                        SetBestImage();

                    }
                }
                else{

                    adapter.notifyDataSetChanged();
                }


            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(BroadCastReviever);
        unregisterReceiver(DeviceCheck);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        df.setRoundingMode(RoundingMode.CEILING);
        list=(ListView)findViewById(R.id.listOfDevices);
        arrayList = new ArrayList<String>();
        addressList=new ArrayList<String>();
        addressList2=new ArrayList<String>();
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, addressList2);
        btn=(Button)findViewById(R.id.findBtn);
        img=(ImageView)findViewById(R.id.imageView);
        tx=(TextView)findViewById(R.id.editText);
        tx.setText("-69");
        checkBox=(CheckBox)findViewById(R.id.checkBox);
        String[] arraySpinner = new String[] {
               " ", "RSSI","Major","Minor"
        };

        //Setting Spinner List
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        list.setAdapter(adapter);
        db=new DataBaseHelper(this);
        bt=BluetoothAdapter.getDefaultAdapter();
        crs=db.Select("Select Mac_Address,pic from BEACONIMAGES");

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                               @Override
                                               public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {

                                     if(check4reg==false) {
                                         check4reg = true;

                                     }
                                     else{
                                         check4reg=false;
                                     }
                                               }
                                           }
        );

        //db.Insert("Insert into BeaconImages values(2,'asasa','hello',null);");
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
Log.d(TAG,"onItemClick: clicked on "+arrayList.get(position));


String name;
String mac;
int count=1;
String pic=null;
                Bitmap bmp=null;
if(crs.moveToFirst()){
    do{

        mac=crs.getString(0);

        if(addressList.get(position).equals(mac)){

            count=0;
            pic=crs.getString(1);

            break;
        }

    }while(crs.moveToNext());
}

if(count==1){
    tempname=arrayList.get(position);
    tempAdd=addressList.get(position);
    a=new double[50];
    run=false;
    pickFromGallery();

}
else{

    File imgFile = new  File(pic);

    if(imgFile.exists()){

      OpenImageViewer(pic);


    }
}
            }
            });
    }



    public void onClickBtn(View v) {

        if(tx.getText().length()==0) {
                Toast.makeText(MainActivity.this,"Please Enter TX first ",Toast.LENGTH_SHORT).show();
            }
            else{
                th = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            while (run) {

                                enableDisableBL();

                                Thread.sleep(9000);
                                emptyArrays();
                            }
                        } catch (Exception ex) {
                            Log.d(TAG, "enableDisable: Problem in Thread");
                        }
                    }
                });
                if (run == false) {
                    btn.setText("Stop Searching");
                    run = true;
                    th.start();
                } else {
                    btn.setText("Start Searching");
                    run = false;

                }
            }
    }
    public void sortByRssi(View V){
        if (pointer >= 0) {

                if (a.length > 1) {
                    a = reverse(a);
                    adapter.notifyDataSetChanged();
                }



        }
    }

    public void enableDisableBL(){

        if(bt==null){
            Log.d(TAG,"enableDisable:Does not Have Bluetooth");
        }

        if(!bt.isEnabled()){
            Intent enableBl=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBl);

            IntentFilter filter=new IntentFilter((BluetoothAdapter.ACTION_STATE_CHANGED));
            registerReceiver(BroadCastReviever,filter);

        }
        else{
            Log.d(TAG,"enableDisable:Bluetooth Already On");
        }

        Log.d(TAG,"enableDisable:Looking For devices");

        if(bt.isDiscovering()){
            bt.cancelDiscovery();
            Log.d(TAG,"enableDisable:Cancel Search");

            checkBtPermission();

            bt.startDiscovery();
            IntentFilter filter2=new IntentFilter((BluetoothDevice.ACTION_FOUND));
            registerReceiver(DeviceCheck,filter2);
        }

        if(!bt.isDiscovering()){
            Log.d(TAG,"enableDisable:Yes working");

            checkBtPermission();
          boolean a=  bt.startDiscovery();

          if(a){
              Log.d(TAG,"enableDisable :Finding");
          }

            else {
                Log.d(TAG,"enableDisable : Not Finding");
            }
            IntentFilter filter2=new IntentFilter((BluetoothDevice.ACTION_FOUND));
            registerReceiver(DeviceCheck,filter2);
        }
    }


    private void checkBtPermission(){
        if(Build.VERSION.SDK_INT> Build.VERSION_CODES.LOLLIPOP){
            Log.d(TAG,"enableDisable:Yes working2");
            int permissionCheck=this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck+=this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            Log.d(TAG,"enableDisable:Permission Got "+permissionCheck);
            if(permissionCheck!=0){
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1001);

            }
            else{
                Log.d(TAG,"checkbtPermission:No need for Permission");
            }

        }
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
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode){
                case 1002:
                    //data.getData returns the content URI for the selected Image
                    Uri selectedImage = data.getData();
                    String picturePath = getRealPathFromURI(selectedImage);



                    tempimg=picturePath;



                    db.Insert(tempname,tempAdd,picturePath);

                    crs=db.Select("Select Mac_Address,pic from BEACONIMAGES");
run=true;

                    break;
            }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public void OpenImageViewer(String path){

        Intent it= new Intent(MainActivity.this, View_Image.class);
        it.putExtra("key",path);
        startActivity(it);
    }

    public  double[] reverse(double[] num) {
        int length=pointer+1;
        for (int i = 0; i < length; i++)
        {
            for (int j = i + 1; j < length; j++) {
                if (num[i] < num[j])
                {
                   double temp = num[i];
                    num[i] = num[j];
                    num[j] = temp;

                    String temp2=arrayList.get(i);
                    arrayList.set(i,arrayList.get(j));
                    arrayList.set(j,temp2);

                    temp2=addressList.get(i);
                    addressList.set(i,addressList.get(j));
                    addressList.set(j,temp2);
                }
            }
        }
        Toast.makeText(getApplicationContext(),"Sorintg Completed", Toast.LENGTH_SHORT).show();
        return num;

        }

        public void emptyArrays(){

        if(pointer>=0) {
            for (int i = 0; i <= pointer; i++) {
                arrayList.clear();
                addressList.clear();
                addressList2.clear();

            }
                b=0;
            pointer = -1;
        }
        }
    public void goBack(View v){
        run=false;
        Intent it= new Intent(MainActivity.this, Main2Activity.class);
        startActivity(it);
    }

    public void SetBestImage(){

        if(crs.moveToFirst()) {
            do {

                if (crs.getString(0).equals(addressList.get(pointer))) {
                    if(pointer==0) {
                        SetPathonImageView(crs.getString(1));
                    }
                    else if(pointer>0 && a[pointer]>a[pointer-1]){
                        Log.d(TAG,"SetPathonImageView: Yes Working" );
                        SetPathonImageView(crs.getString(1));
                    }
                }


            } while (crs.moveToNext());
        }
    }

    public void SetPathonImageView(String path){
        Log.d(TAG,"SetPathonImageView: "+path);
        path.replace("file://","");
        File imgFile = new File(path);

        if (imgFile.exists()) {


            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            Log.d(TAG,"SetPathonImageView: Working here");



            img.setImageBitmap(myBitmap);
        }
    }

    public int findMax(double[] num){
        int counter=0;
        int resultindex=0;
        for(int i=0;i<num.length;i++){
            for(int j=0;j<num.length;j++){
                if(num[i]>=num[j]){
                    counter++;
                }

            }
            if(counter==num.length){
                resultindex=i;
            }
            else{
                counter=0;
            }
        }
        return resultindex;
    }

    }


