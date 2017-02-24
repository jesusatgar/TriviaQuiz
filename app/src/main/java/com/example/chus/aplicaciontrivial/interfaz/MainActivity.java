package com.example.chus.aplicaciontrivial.interfaz;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.chus.aplicaciontrivial.R;
import com.google.android.gms.location.LocationRequest;

public class MainActivity extends AppCompatActivity {

    private LocationRequest mLocationRequest;
    private static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pedirPermisos();
        crearLocationRequest();
    }

    public void empezarPartida(View view){
        Intent i = new Intent(this, ActivityPregunta.class );
        startActivity(i);
    }

    public void verRanking(View view){
        Intent i = new Intent(this, ActivityRanking.class );
        startActivity(i);
    }

    protected void crearLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(
                LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void pedirPermisos() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_ACCESS_FINE_LOCATION);

            }
        }
    }
}
