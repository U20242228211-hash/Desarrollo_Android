package com.example.fase2;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdjuntosEvidenciasActivity extends AppCompatActivity {
    public static final String EXTRA_ADJUNTO_RUTA = "adjunto_ruta";
    public static final String EXTRA_ADJUNTO_NOMBRE = "adjunto_nombre";

    private Button botonVolver;
    private Button botonTomarFoto;
    private Button botonSeleccionarArchivo;
    private Button botonEliminarEjemplo;
    private TextView textoNombreArchivo;

    private String rutaFotoActual;
    private Uri uriFotoActual;
    private String adjuntoRutaSeleccionado;
    private String adjuntoNombreSeleccionado;

    // Launcher para la cámara
    private final ActivityResultLauncher<Uri> launcherCamara = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            result -> {
                if (result) {
                    adjuntoRutaSeleccionado = rutaFotoActual;
                    adjuntoNombreSeleccionado = new File(rutaFotoActual).getName();
                    prepararResultado();
                    Toast.makeText(this, "Foto capturada: " + rutaFotoActual, Toast.LENGTH_LONG).show();
                    if (textoNombreArchivo != null) {
                        textoNombreArchivo.setText("Foto: " + adjuntoNombreSeleccionado);
                    }
                }
            }
    );

    // Launcher para seleccionar archivo
    private final ActivityResultLauncher<String> launcherArchivo = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    String nombre = obtenerNombreArchivo(uri);
                    adjuntoRutaSeleccionado = uri.toString();
                    adjuntoNombreSeleccionado = nombre;
                    prepararResultado();
                    Toast.makeText(this, "Archivo seleccionado: " + nombre, Toast.LENGTH_SHORT).show();
                    if (textoNombreArchivo != null) {
                        textoNombreArchivo.setText("Archivo: " + nombre);
                    }
                }
            }
    );

    // Launcher para pedir permiso de cámara
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    gestionarCapturaFoto();
                } else {
                    Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjuntos_evidencias);

        botonVolver = findViewById(R.id.botonVolverDesdeAdjuntos);
        botonTomarFoto = findViewById(R.id.botonTomarFoto);
        botonSeleccionarArchivo = findViewById(R.id.botonSeleccionarArchivo);
        botonEliminarEjemplo = findViewById(R.id.botonEliminarAdjuntoEjemplo);

        ViewGroup contenedor = findViewById(R.id.contenedorListaAdjuntos);
        // Usamos el TextView de ejemplo para mostrar el resultado
        textoNombreArchivo = contenedor.findViewWithTag("textoEjemplo");
        if (textoNombreArchivo == null) {
            // Si no tiene tag, buscamos el primer TextView en el contenedor (ajuste visual dinámico)
            View layoutEjemplo = contenedor.getChildAt(0);
            if (layoutEjemplo instanceof ViewGroup) {
                textoNombreArchivo = (TextView) ((ViewGroup) layoutEjemplo).getChildAt(0);
            }
        }

        botonVolver.setOnClickListener(v -> {
            prepararResultado();
            finish();
        });

        botonTomarFoto.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                gestionarCapturaFoto();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            }
        });

        botonSeleccionarArchivo.setOnClickListener(v -> launcherArchivo.launch("*/*"));

        botonEliminarEjemplo.setOnClickListener(v -> {
            if (textoNombreArchivo != null) {
                textoNombreArchivo.setText("Ningún archivo seleccionado");
            }
            adjuntoRutaSeleccionado = null;
            adjuntoNombreSeleccionado = null;
            setResult(Activity.RESULT_CANCELED);
            Toast.makeText(AdjuntosEvidenciasActivity.this, "Adjunto quitado", Toast.LENGTH_SHORT).show();
        });
    }

    private void gestionarCapturaFoto() {
        File archivoFoto = null;
        try {
            archivoFoto = crearArchivoImagen();
        } catch (IOException ex) {
            Toast.makeText(this, "Error al crear archivo", Toast.LENGTH_SHORT).show();
        }

        if (archivoFoto != null) {
            uriFotoActual = FileProvider.getUriForFile(this,
                    "com.example.fase2.fileprovider",
                    archivoFoto);
            launcherCamara.launch(uriFotoActual);
        }
    }

    private File crearArchivoImagen() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String nombreImagen = "JPEG_" + timestamp + "_";
        File directorio = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imagen = File.createTempFile(nombreImagen, ".jpg", directorio);
        rutaFotoActual = imagen.getAbsolutePath();
        return imagen;
    }

    private String obtenerNombreArchivo(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index != -1) result = cursor.getString(index);
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void prepararResultado() {
        if (adjuntoRutaSeleccionado == null || adjuntoNombreSeleccionado == null) {
            return;
        }
        Intent data = new Intent();
        data.putExtra(EXTRA_ADJUNTO_RUTA, adjuntoRutaSeleccionado);
        data.putExtra(EXTRA_ADJUNTO_NOMBRE, adjuntoNombreSeleccionado);
        setResult(Activity.RESULT_OK, data);
    }
}
