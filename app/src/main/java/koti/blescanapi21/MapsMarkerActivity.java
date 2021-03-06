package koti.blescanapi21;

/**
 * Created by Proot on 8.4.2018.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.R.layout;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

/**
 * An activity that displays a Google map with a marker (pin) to indicate a particular location.
 */
public class MapsMarkerActivity  extends AppCompatActivity implements OnMapReadyCallback {

    //String locN;
    //String locE;
    double locNN;
    double locEE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Intent intententti = getIntent();
        //locN = intententti.getStringExtra("locN");
        //locE = intententti.getStringExtra("locE");

        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        //LatLng sydney = new LatLng(-33.852, 151.211);

        //get coordinates from database and add/upgrade tag to map

        //double locNN = Double.parseDouble(locN);
        //double locEE = Double.parseDouble(locE);

        //get nodes from database
        DatabaseHandler db = new DatabaseHandler(this);
        List <String[]> nodes = db.getData();

        if (nodes == null){
        }
        else {
            for (String[] node : nodes) {
                Log.d("JALAJALA", Arrays.toString(node));
                String title = node[0];
                locNN = Double.parseDouble(node[1]);
                locEE = Double.parseDouble(node[2]);
                String address = node[3];

                //double locNN = 62.25353;
                //double locEE = 24.34342;

                LatLng node1 = new LatLng(locNN, locEE);
                googleMap.addMarker(new MarkerOptions()
                        .position(node1)
                        .snippet(address)
                        .title(title));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(node1));
            }
        }
        db.close();


    }
}

