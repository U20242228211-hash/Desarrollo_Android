package com.example.fase2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ConfiguracionPreferenciasActivity extends AppCompatActivity {

    public static final String PREFS = "semeolyidano_prefs";
    public static final String KEY_NOTIFICACIONES = "notificaciones";
    public static final String KEY_SONIDO = "sonido";
    public static final String KEY_VIBRACION = "vibracion";
    public static final String KEY_ANTICIPACION = "anticipacion";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion_preferencias);

        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        Switch switchNotif = findViewById(R.id.switchNotificaciones);
        CheckBox checkSonido = findViewById(R.id.opcionSonido);
        CheckBox checkVibracion = findViewById(R.id.opcionVibracion);
        Spinner spinnerAnticipacion = findViewById(R.id.selectorAnticipacion);
        Button botonCategorias = findViewById(R.id.botonCategoriasPersonalizadas);
        Button botonLimpiar = findViewById(R.id.botonLimpiarHistorial);
        Button botonVolver = findViewById(R.id.botonVolverDesdeConfiguracion);

        String[] opciones = {"5 minutos antes", "10 minutos antes", "15 minutos antes", "30 minutos antes", "1 hora antes"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opciones);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAnticipacion.setAdapter(adapter);

        switchNotif.setChecked(prefs.getBoolean(KEY_NOTIFICACIONES, true));
        checkSonido.setChecked(prefs.getBoolean(KEY_SONIDO, true));
        checkVibracion.setChecked(prefs.getBoolean(KEY_VIBRACION, true));
        spinnerAnticipacion.setSelection(prefs.getInt(KEY_ANTICIPACION, 2));

        switchNotif.setOnCheckedChangeListener((b, checked) ->
                prefs.edit().putBoolean(KEY_NOTIFICACIONES, checked).apply());
        checkSonido.setOnCheckedChangeListener((b, checked) ->
                prefs.edit().putBoolean(KEY_SONIDO, checked).apply());
        checkVibracion.setOnCheckedChangeListener((b, checked) ->
                prefs.edit().putBoolean(KEY_VIBRACION, checked).apply());
        spinnerAnticipacion.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> p, android.view.View v, int pos, long id) {
                prefs.edit().putInt(KEY_ANTICIPACION, pos).apply();
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> p) {}
        });

        botonCategorias.setOnClickListener(v ->
                startActivity(new Intent(this, GestionCategoriasPersonalizadasActivity.class)));

        botonLimpiar.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Limpiar historial")
                        .setMessage("¿Deseas eliminar todos los recordatorios completados?")
                        .setPositiveButton("Eliminar", (d, w) -> {
                            new RecordatorioDbHelper(this).limpiarCompletados();
                            Toast.makeText(this, "Historial limpiado", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancelar", null)
                        .show());

        botonVolver.setOnClickListener(v -> finish());
    }
}
