package koti.blescanapi21;

/**
 * Created by Proot on 8.4.2018.
 */

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.R.layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

/**
 * An activity that displays a Google map with a marker (pin) to indicate a particular location.
 */
public class MapsMarkerActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener {

    String locN;
    String locE;
    private static double locNN;
    private static double locEE;

    private BluetoothAdapter mBluetoothAdapter;
    private LocationManager locationManager;

    private final static int REQUEST_ENABLE_BT = 1;
    final static int BLUETOOTH_PERMISSION_REQUEST_CODE = 0;

    private static GoogleMap map;
    private static Context context;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Intent intententti = getIntent();
        //locN = intententti.getStringExtra("locN");
        //locE = intententti.getStringExtra("locE");
        MapsMarkerActivity.context = getApplicationContext();

        super.onCreate(savedInstanceState);

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //create crap for permissions
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //if bt not enabled, ask to enable it - and so on...
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtintent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtintent, REQUEST_ENABLE_BT);

            if (Build.VERSION.SDK_INT >= 23) { // Marshmallow
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, BLUETOOTH_PERMISSION_REQUEST_CODE);
            } else {
            }

        }
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Intent enableGpsintent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(enableGpsintent);
        }

        //If there is no rights for location:
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.d("JALAJALA: ", "LOC OIKEUS_CHECK");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            //start services for location and ble info.
            //location passes loc parameters to ble which passes them to db.
            Log.d("JALAJALA: ", "LOC OIKEUDET_ON");
            startService(new Intent(this, LocationFetch.class));
            startService(new Intent(this, BleScanner.class));

        }

/*        //OLD MAIN BUTTON, USE LATER FOR LIST THE CONTENT OF DB IF POSSIBLE
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                for (String[] node : nodes) {
                    Log.d("JALAJALA", Arrays.toString(node));
                }

            }
        });*/
    }

    protected void onResume() {
        super.onResume();

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtintent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtintent, REQUEST_ENABLE_BT);

            if (Build.VERSION.SDK_INT >= 23) { // Marshmallow
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, BLUETOOTH_PERMISSION_REQUEST_CODE);
            } else {
            }

        }
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Intent enableGpsintent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(enableGpsintent);
        }

        //If there is no rights for location:
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.d("JALAJALA: ", "LOC OIKEUS_CHECK");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            Log.d("JALAJALA: ", "LOC OIKEUDET_ON");
            startService(new Intent(this, LocationFetch.class));

        }
    }


    /**
     * Manipulates the map when it's available.
     * The API invokes this callback when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user receives a prompt to install
     * Play services inside the SupportMapFragment. The API invokes this method after the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googlemap) {
        Log.d("JALAJALA", "ONMAPREADY");
        map = googlemap;
        DatabaseHandler db = new DatabaseHandler(this);
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        //LatLng sydney = new LatLng(-33.852, 151.211);

        //get coordinates from database and add/upgrade tag to map

        //double locNN = Double.parseDouble(locN);
        //double locEE = Double.parseDouble(locE);

        //get nodes from database
        //DatabaseHandler db = new DatabaseHandler(this);
        List<String[]> nodes;
        nodes = db.getData();

        if (nodes == null) {
        } else {
            for (String[] node : nodes) {

                Log.d("JALAJALA", Arrays.toString(node));


                String title = node[0];
                locNN = Double.parseDouble(node[1]);
                locEE = Double.parseDouble(node[2]);
                String address = node[3];

                //double locNN = 62.25353;
                //double locEE = 24.34342;

                //set location
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                map.setMyLocationEnabled(true);
                map.setOnMyLocationButtonClickListener(this);


                LatLng node1 = new LatLng(locNN, locEE);
                map.addMarker(new MarkerOptions()
                        .position(node1)
                        .snippet(address)
                        .title(title));
                map.moveCamera(CameraUpdateFactory.newLatLng(node1));
            }
        }
        db.close();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }


    //Metodin idea on lukea db.getData():lla tuoreimmat arvot ja verrata aiempiin onMapReadyssä() luettuihin
    //vertaa arvojen määrä nodes listoissa, jos isompi tässä, lue viimeiset ja paiskaa / korvaa (jos update) kartalle, päivitä nodes seuraavaan lukuun
    //tällä hetkellä metodia kutsutaan locationfetchin onlocationchangessa, eli aina ku gps arvot muuttuu
    //siksi, että samalla voidaan passata gps koordinaatit tänne jotta kartta seuraa
    //vois tieten olla viisaampaa tehdä databasehandlerissa...

    public static void upgradeMap(){
        Log.d("JALAJALA", "ONUPGRADEMAP");
        DatabaseHandler db = new DatabaseHandler(MapsMarkerActivity.context);

        //tama rivi rikkoo, nullpointerjadajaa - MIKSI?!?!?
        List<String[]> nodes2;
        nodes2 = db.getData();

        if (nodes2 == null){
        }
        else {
            for (String[] node2 : nodes2) {

                Log.d("JALAJALA", Arrays.toString(node2));

                String title = node2[0];
                locNN = Double.parseDouble(node2[1]);
                locEE = Double.parseDouble(node2[2]);
                String address = node2[3];

                //double locNN = 62.25353;
                //double locEE = 24.34342;

                LatLng node1 = new LatLng(locNN, locEE);
                map.addMarker(new MarkerOptions()
                        .position(node1)
                        .snippet(address)
                        .title(title));
                map.moveCamera(CameraUpdateFactory.newLatLng(node1));
            }
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}