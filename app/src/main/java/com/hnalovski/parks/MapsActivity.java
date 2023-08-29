package com.hnalovski.parks;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.hnalovski.parks.adapter.CustomInfoWindow;
import com.hnalovski.parks.data.AsyncResponse;
import com.hnalovski.parks.data.Repository;
import com.hnalovski.parks.databinding.ActivityMapsBinding;
import com.hnalovski.parks.model.Park;
import com.hnalovski.parks.model.ParkViewModel;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private ParkViewModel parkViewModel;
    private List<Park> parkList;
    private CardView cardView;
    private EditText stateCodeEt;
    private ImageButton searchButton;

    // code needs to be set to something so some markers are shown on map, others its a null value
    private String code;


    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        parkViewModel = new ViewModelProvider(this).get(ParkViewModel.class);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_container);
        mapFragment.getMapAsync(this);
        cardView = findViewById(R.id.cardView);
        stateCodeEt = findViewById(R.id.floating_state_value_et);
        searchButton = findViewById(R.id.floating_search_button);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.maps_nav_button) {
                if (cardView.getVisibility() == View.INVISIBLE || cardView.getVisibility() == View.GONE) {
                    cardView.setVisibility(View.VISIBLE);
                }
                mMap.clear();
                getSupportFragmentManager().beginTransaction().replace(R.id.map_container, mapFragment).commit();
                mapFragment.getMapAsync(this);
            } else if (id == R.id.parks_nav_button) {
                selectedFragment = ParksFragment.newInstance();
                cardView.setVisibility(View.GONE );
            }


            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.map_container, selectedFragment).commit();
            }
            return true;
        });

        stateCodeEt.setFilters(new InputFilter[]{inputFilter});
        searchButton.setOnClickListener(view -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            parkList.clear();
            String stateCode = stateCodeEt.getText().toString().trim();
            if (!TextUtils.isEmpty(stateCode)) {
                code = stateCode;
                parkViewModel.selectCode(code);
                onMapReady(mMap); //refresh map (rerun the map code from below to refresh markers)
                stateCodeEt.setText("");
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setInfoWindowAdapter(new CustomInfoWindow(getApplicationContext()));
        mMap.setOnInfoWindowClickListener(this);

        parkList = new ArrayList<>();
        parkList.clear();
        populateMap();
    }

    private void populateMap() {
        parkList.clear();
        mMap.clear(); // important!!! clear markers
        Repository.getParks(parks -> {
            parkList = parks;
            for (Park park : parks) {
                LatLng location = new LatLng(Double.parseDouble(park.getLatitude()), Double.parseDouble(park.getLongitude()));

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(location)
                        .title(park.getFullName())
                        .icon(BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_VIOLET))
                        .snippet(park.getStates());
                Marker marker = mMap.addMarker(markerOptions);
                marker.setTag(park);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 5));
                Log.d("Parks", "onMapReady: " + park.getFullName());
            }
            parkViewModel.setSelectedParks(parkList);
            Log.d("Size", "populateMap: " + parkList.size());

        }, code);
    }

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {
        cardView.setVisibility(View.GONE);
        // go to details fragment
        goToDetailsFragment(marker);
    }

    private void goToDetailsFragment(@NonNull Marker marker) {
        parkViewModel.setSelectedPark((Park) marker.getTag());
        getSupportFragmentManager().beginTransaction().replace(R.id.map_container, DetailsFragment.newInstance()).commit();
    }

    public InputFilter inputFilter = (source, start, end, dest, dstart, dend) -> (CharSequence) source.toString().toUpperCase();
}