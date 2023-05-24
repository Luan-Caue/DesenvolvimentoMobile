package com.example.gmaps;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap gMap;
    ArrayList<LatLng>arrayList = new ArrayList<LatLng>();
    LatLng CooperativaReciclagem = new LatLng(-23.49993795929152, -47.483801617706916);
    LatLng CooperativaCoreso = new LatLng(-23.513153271675158, -47.435100908006845);
    LatLng Metareciclagem = new LatLng(-23.4921726065122, -47.473348358179166);
    LatLng ColetaPilha = new LatLng(-23.472118478861177, -47.43923500396602);
    LatLng ColetaReciclagem = new LatLng(-23.520127364919144, -47.512629715458715);
    LatLng CooperativaSorocaba = new LatLng(-23.498694331160895, -47.514662449132295);
    public static int LOCATION_REQUEST_CODE = 100;

    FusedLocationProviderClient fusedLocationProviderClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button Button = findViewById(R.id.botaoPasso);
        Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ContadorPasso.class);
                startActivity(intent);
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);
        mapFragment.getMapAsync(this);
        arrayList.add(CooperativaReciclagem);
        arrayList.add(CooperativaCoreso);
        arrayList.add(Metareciclagem);
        arrayList.add(ColetaPilha);
        arrayList.add(ColetaReciclagem);
        arrayList.add(CooperativaSorocaba);


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        checkLocationPermission();


    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //Pegar a Localização
            getUserLocation();

        } else {
            requestForPermissions();
        }

    }

    private void getUserLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();

        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                if (location != null){
                    double lat = location.getLatitude();
                    double longitude = location.getLongitude();

                    LatLng userLocation = new LatLng(lat, longitude);

                    gMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
                    gMap.animateCamera(CameraUpdateFactory.zoomTo(15));

                    gMap.addMarker(new MarkerOptions().position(userLocation).title("Sua Localização")
                            .icon(setIcon(MainActivity.this, R.drawable.baseline_my_location_24)));

                }

            }
        });

    }

    private void requestForPermissions() {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Accepted", Toast.LENGTH_SHORT).show();
                getUserLocation();
            } else {
                Toast.makeText(this, "Permission Rejected", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(this, R.raw.custom_map_style)
        );
        gMap = googleMap;

        for (int i=0;i<arrayList.size();i++){
            gMap.addMarker(new MarkerOptions().position(arrayList.get(i)).title("Pontos de Coleta").icon(setIcon(MainActivity.this, R.drawable.baseline_location_city_24)));
        }

        gMap.getUiSettings().setMyLocationButtonEnabled(true);

        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(latLng.latitude+ " : " + latLng.longitude);
                gMap.clear();
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                gMap.addMarker(markerOptions);


            }
        });


    }

    public BitmapDescriptor setIcon(Activity context, int drawableID){
        Drawable drawable = ActivityCompat.getDrawable(context, drawableID);

        drawable.setBounds(0,0, drawable.getIntrinsicHeight(), drawable.getIntrinsicWidth());

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);

        drawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);


    }
    

}

