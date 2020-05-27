package com.example.beacondetecting;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;

public class BeaconScanning extends AppCompatActivity implements BeaconConsumer, RangeNotifier {
    private BeaconManager mBeaconManager;
    ListView list;
    protected static final String TAG = "RangingActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_scanning);
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
        mBeaconManager = BeaconManager.getInstanceForApplication(this);
        // In this example, we will use Eddystone protocol, so we have to define it here
        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.ALTBEACON_LAYOUT));
        // Binds this activity to the BeaconService
        mBeaconManager.bind(BeaconScanning.this);


    }


    @Override
    public void onBeaconServiceConnect() {
        // Encapsulates a beacon identifier of arbitrary byte length

        // Represents a criteria of fields used to match beacon
        Region region = new Region("AllBeaconsRegion",null,null,null);
        try {
            // Tells the BeaconService to start looking for beacons that match the passed Region object
            mBeaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        // Specifies a class that should be called each time the BeaconService gets ranging data, once per second by default
        mBeaconManager.addRangeNotifier(this);
        Toast.makeText(getApplicationContext(),"Please Work", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        Toast.makeText(getApplicationContext(),"Please Work or I'll die", Toast.LENGTH_SHORT).show();
        if (beacons.size() > 0) {
            Toast.makeText(getApplicationContext(),"This is "+beacons.iterator().next().getDistance(), Toast.LENGTH_SHORT).show();

        }
    }

}
