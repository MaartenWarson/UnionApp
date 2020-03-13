package be.pxl.unionapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import be.pxl.unionapp.R;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private String streetAndNumber, postalCode, city, addressString;
    private GoogleMap mMap;
    private static final String TAG = "MapsActivity";
    private Address address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Back-knop aan

        setTitle("Test");

        // Auto-generated code
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        init();

        setTitle(addressString);
    }

    private void init() {
        streetAndNumber = getIntent().getStringExtra("address");
        postalCode = getIntent().getStringExtra("postalCode");
        city = getIntent().getStringExtra("city");
        addressString = streetAndNumber + ", " + postalCode + " " + city;
    }


    @Override // Wordt opgeroepen wanneer de map klaar is
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Address ophalen in achtergrond
        //new BackgroundExecuter().execute();

        address = getAddress();

        if (address == null) {
            Toast.makeText(MapsActivity.this, "Fout bij het ophalen van de locatie", Toast.LENGTH_LONG).show();
        }
        else {
            LatLng myLocationLatLng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(myLocationLatLng).title(address.getAddressLine(0)));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocationLatLng, 12.0f));
        }
    }

    private Address getAddress() {
        Geocoder geocoder = new Geocoder(MapsActivity.this);
        List<Address> addressList = new ArrayList<>();

        try {
            addressList = geocoder.getFromLocationName(addressString, 1);
        } catch(IOException e) {
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage());
        }

        if (addressList.size() > 0) {
           Address address = addressList.get(0);
           return address;
        }

        return null;
    }

    // Wanneer er op het pijltje in de ActionBar wordt geklikt ...
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            Log.i(TAG, "Went back to previous activity successfully");
            return true;
        }

        Log.e(TAG, "Something went wrong going back to the previous activity");
        return super.onOptionsItemSelected(item);
    }

    // Deze voert een achtergrondtaak uit (Background Thread)
    public class BackgroundExecuter extends AsyncTask<String, Void, Void> {
        // VOOR het uitvoeren van de taak (uitgevoerd in Main Thread)
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        // UITVOEREN van de taak (uitgevoerd in Background Thread = op de achtergrond)
        @Override
        protected Void doInBackground(String... strings) {
            address = getAddress();
            return null;
        }

        // NA het uitvoeren van de taak (geen speciale implementatie voor deze app) (uitgevoerd in Main Thread)
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
