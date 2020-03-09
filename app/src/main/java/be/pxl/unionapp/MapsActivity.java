package be.pxl.unionapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private String streetAndNumber, postalCode, city, addressString;
    private GoogleMap mMap;
    private static final String TAG = "MapsActivity";

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

        Address address = getAddress();
        LatLng myLocationLatLng = new LatLng(address.getLatitude(), address.getLongitude());
        mMap.addMarker(new MarkerOptions().position(myLocationLatLng).title(address.getAddressLine(0)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocationLatLng, 12.0f));
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
}
