package com.example.donatetosave;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.common.collect.MapMaker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MapsActivity extends FragmentActivity implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback {
    private ImageView Search_BTN;
    public static final int MY_LOCATION_REQUEST_CODE = 99;
    List<Place.Field> fields = Arrays.asList(Place.Field.LAT_LNG,Place.Field.ID, Place.Field.NAME);
    private GoogleMap mMap;
    private ArrayList<Marker> itemList = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseDatabase fdb = FirebaseDatabase.getInstance();
    public LocationManager locationManager;
    private final int AUTOCOMPLETE_REQUEST_CODE = 98;
    private Place place;
    Intent intent;
    private FusedLocationProviderClient fusedLocationClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyC-dn3G-usyAwqgUIJVVVAO2qz4iZ2CTmo");
        }
        PlacesClient placesClient = Places.createClient(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        intent = (Intent) new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields)
                .setCountry("VN").build(MapsActivity.this);
        mapFragment.getMapAsync(this);
        Search_BTN = findViewById(R.id.btn_places_search);
        Search_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_LOCATION_REQUEST_CODE);
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                            getDocumentNearBy(latLng,10);
                        }
                    }
                });

    }

    @Override
    public boolean onMyLocationButtonClick() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                            getDocumentNearBy(latLng,10);
                        }
                    }
                });
        Log.i("checkclick", "herhe");
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        if (location != null) {
            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
            getDocumentNearBy(latLng,10);
            Log.d("check click", "herhe");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED)
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_LOCATION_REQUEST_CODE);
                }
            Log.e("IMHERE","@A@");
            mMap.setMyLocationEnabled(true);
        } else {
            // Permission was denied. Display an error message.
        }
    }
    private void getDocumentNearBy(LatLng latlng, double distance){
        double lat = 0.0144927536231884;
        double lng = 0.0181818181818182;
        double lowerlat = latlng.latitude - lat * distance;
        double lowerlng = latlng.longitude - lng * distance;
        double greaterlat = latlng.latitude + lat*distance;
        double greaterlng = latlng.longitude + lat*distance;

        GeoPoint lesserGeopoint = new GeoPoint(lowerlat,lowerlng);
        GeoPoint greaterGeopoint = new GeoPoint(greaterlat,greaterlng);
        fdb.getReference().child("Item").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> listChirdren = dataSnapshot.getChildren();
                for(DataSnapshot child: listChirdren) {
                    LatLng latLng = new LatLng(child.child("geo").child("latitude").getValue(Double.class),child.child("geo").child("longitude").getValue(Double.class));
                    String tag = child.child("tag").getValue(String.class);
                    final Marker  marker = mMap.addMarker(new MarkerOptions().position(latLng));
                    marker.setTag(child.child("key").getValue(String.class));
                    db.collection("Item").document(child.child("key").getValue(String.class))
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    marker.setTitle(document.get("title",String.class));
                                    Log.d("nah", "DocumentSnapshot data: " + document.get("title", String.class));
                                } else {
                                    Log.d("nah", "nathing");

                                }
                            }
                        }
                    });
                    itemList.add(marker);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        /*
        db.collection("Item")
                .whereGreaterThanOrEqualTo("LatLng",lesserGeopoint)
                .whereLessThanOrEqualTo("LatLng",greaterGeopoint)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                Log.d("Noice", document.getId() + " => " + document.getData());
                            }
                        }
                        else {
                            Log.d("Get FAIL!","Error");
                        }
                    }
                });*/

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                place = Autocomplete.getPlaceFromIntent(data);
                if (place != null) {
                    LatLng latLng = place.getLatLng();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                    getDocumentNearBy(latLng,10);

                }
                Log.i("duoc", "Place: " + place.getName() + ", " + place.getId());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("duoc", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }
}
