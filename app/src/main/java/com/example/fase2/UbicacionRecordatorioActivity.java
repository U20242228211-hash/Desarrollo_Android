package com.example.fase2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class UbicacionRecordatorioActivity extends AppCompatActivity {
    public static final String EXTRA_UBICACION = "ubicacion";

    private Button botonVolver;
    private Button botonGuardarUbicacion;
    private Button botonCapturarUbicacion;
    private Button botonOtraUbicacion;
    private EditText campoUbicacion;

    private FusedLocationProviderClient fusedLocationClient;

    private final ActivityResultLauncher<String[]> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                Boolean coarseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);
                if (Boolean.TRUE.equals(fineLocationGranted) || Boolean.TRUE.equals(coarseLocationGranted)) {
                    obtenerUbicacionActual();
                } else {
                    Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubicacion_recordatorio);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        botonVolver = findViewById(R.id.botonVolverDesdeUbicacion);
        botonGuardarUbicacion = findViewById(R.id.botonGuardarUbicacion);
        botonCapturarUbicacion = findViewById(R.id.botonCapturarUbicacion);
        botonOtraUbicacion = findViewById(R.id.botonOtraUbicacion);
        campoUbicacion = findViewById(R.id.campoUbicacion);

        String ubicacionInicial = getIntent().getStringExtra(EXTRA_UBICACION);
        if (ubicacionInicial != null) {
            campoUbicacion.setText(ubicacionInicial);
        }

        botonVolver.setOnClickListener(v -> finish());
        botonGuardarUbicacion.setOnClickListener(v -> {
            if (devolverUbicacion()) {
                finish();
            }
        });

        botonCapturarUbicacion.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                obtenerUbicacionActual();
            } else {
                locationPermissionLauncher.launch(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                });
            }
        });

        botonOtraUbicacion.setOnClickListener(v -> {
            campoUbicacion.requestFocus();
            Toast.makeText(this, "Escribe la ubicación del recordatorio", Toast.LENGTH_SHORT).show();
        });
    }

    @SuppressLint("MissingPermission")
    private void obtenerUbicacionActual() {
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(this, location -> {
                    if (location == null) {
                        Toast.makeText(this, "No se pudo obtener la ubicación. Activa el GPS e inténtalo de nuevo.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    String ubicacion = obtenerDireccion(location.getLatitude(), location.getLongitude());
                    campoUbicacion.setText(ubicacion);
                    Toast.makeText(this, "Ubicación actual capturada", Toast.LENGTH_SHORT).show();
                });
    }

    private String obtenerDireccion(double latitude, double longitude) {
        if (!Geocoder.isPresent()) {
            return "Ubicación actual";
        }

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                String addressLine = addresses.get(0).getAddressLine(0);
                if (addressLine != null && !addressLine.trim().isEmpty()) {
                    return addressLine;
                }
            }
        } catch (IOException ignored) {
        }
        return "Ubicación actual";
    }

    private boolean devolverUbicacion() {
        String ubicacion = campoUbicacion.getText().toString().trim();
        if (ubicacion.isEmpty()) {
            Toast.makeText(this, "Primero captura o escribe una ubicación", Toast.LENGTH_SHORT).show();
            return false;
        }

        Intent data = new Intent();
        data.putExtra(EXTRA_UBICACION, ubicacion);
        setResult(Activity.RESULT_OK, data);
        Toast.makeText(this, "Ubicación guardada", Toast.LENGTH_SHORT).show();
        return true;
    }
}
