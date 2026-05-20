package com.example.fase2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CrearEditarRecordatorioActivity extends AppCompatActivity {
    public static final String EXTRA_RECORDATORIO_ID = "recordatorio_id";

    private EditText campoTitulo;
    private EditText campoDescripcion;
    private Spinner selectorCategoria;
    private Spinner selectorPrioridad;
    private Button botonConfigurarUbicacion;
    private Button botonGestionarAdjuntos;
    private Button botonRegresar;
    private Button botonGuardar;
    private Button botonSeleccionarFecha;
    private Button botonSeleccionarHora;
    private TextView textoTituloPantalla;

    private RecordatorioDbHelper dbHelper;
    private long recordatorioId = -1;
    private String fechaSeleccionada;
    private String horaSeleccionada;
    private String ubicacion;
    private String adjuntoRuta;
    private String adjuntoNombre;

    private final SimpleDateFormat formatoDb = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private final SimpleDateFormat formatoVista = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    private final ActivityResultLauncher<Intent> launcherUbicacion = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    ubicacion = result.getData().getStringExtra(UbicacionRecordatorioActivity.EXTRA_UBICACION);
                    if (!TextUtils.isEmpty(ubicacion)) {
                        botonConfigurarUbicacion.setText("Ubicación: " + ubicacion);
                    }
                }
            });

    private final ActivityResultLauncher<Intent> launcherAdjuntos = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    adjuntoRuta = result.getData().getStringExtra(AdjuntosEvidenciasActivity.EXTRA_ADJUNTO_RUTA);
                    adjuntoNombre = result.getData().getStringExtra(AdjuntosEvidenciasActivity.EXTRA_ADJUNTO_NOMBRE);
                    if (!TextUtils.isEmpty(adjuntoNombre)) {
                        botonGestionarAdjuntos.setText("Adjunto: " + adjuntoNombre);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_editar_recordatorio);

        dbHelper = new RecordatorioDbHelper(this);
        recordatorioId = getIntent().getLongExtra(EXTRA_RECORDATORIO_ID, -1);

        textoTituloPantalla = findViewById(R.id.textoTituloPantallaCrear);
        campoTitulo = findViewById(R.id.campoTituloRecordatorio);
        campoDescripcion = findViewById(R.id.campoDescripcionRecordatorio);
        selectorCategoria = findViewById(R.id.selectorCategoria);
        selectorPrioridad = findViewById(R.id.selectorPrioridad);
        botonConfigurarUbicacion = findViewById(R.id.botonConfigurarUbicacion);
        botonGestionarAdjuntos = findViewById(R.id.botonGestionarAdjuntos);
        botonRegresar = findViewById(R.id.botonRegresarDesdeCrear);
        botonGuardar = findViewById(R.id.botonGuardarRecordatorio);
        botonSeleccionarFecha = findViewById(R.id.botonSeleccionarFecha);
        botonSeleccionarHora = findViewById(R.id.botonSeleccionarHora);

        configurarSpinners();
        configurarValoresIniciales();

        botonConfigurarUbicacion.setOnClickListener(v -> {
            Intent intent = new Intent(this, UbicacionRecordatorioActivity.class);
            if (!TextUtils.isEmpty(ubicacion)) {
                intent.putExtra(UbicacionRecordatorioActivity.EXTRA_UBICACION, ubicacion);
            }
            launcherUbicacion.launch(intent);
        });

        botonGestionarAdjuntos.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdjuntosEvidenciasActivity.class);
            launcherAdjuntos.launch(intent);
        });

        botonRegresar.setOnClickListener(v -> confirmarCancelar());
        botonGuardar.setOnClickListener(v -> guardarRecordatorio());
        botonSeleccionarFecha.setOnClickListener(v -> mostrarDatePicker());
        botonSeleccionarHora.setOnClickListener(v -> mostrarTimePicker());
    }

    private void configurarSpinners() {
        configurarSpinner(selectorCategoria, new String[]{"Personal", "Trabajo", "Salud", "Estudio", "Otro"});
        configurarSpinner(selectorPrioridad, new String[]{"Baja", "Media", "Alta"});
    }

    private void configurarSpinner(Spinner spinner, String[] opciones) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opciones);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void configurarValoresIniciales() {
        if (recordatorioId > 0) {
            Recordatorio recordatorio = dbHelper.obtenerPorId(recordatorioId);
            if (recordatorio == null) {
                Toast.makeText(this, "Recordatorio no encontrado", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            textoTituloPantalla.setText("Editar Recordatorio");
            campoTitulo.setText(recordatorio.getTitulo());
            campoDescripcion.setText(recordatorio.getDescripcion());
            fechaSeleccionada = recordatorio.getFecha();
            horaSeleccionada = recordatorio.getHora();
            ubicacion = recordatorio.getUbicacion();
            adjuntoRuta = recordatorio.getAdjuntoRuta();
            adjuntoNombre = recordatorio.getAdjuntoNombre();
            seleccionarSpinner(selectorCategoria, recordatorio.getCategoria());
            seleccionarSpinner(selectorPrioridad, recordatorio.getPrioridad());
            botonSeleccionarFecha.setText(formatearFechaVista(fechaSeleccionada));
            botonSeleccionarHora.setText(horaSeleccionada);
            if (!TextUtils.isEmpty(ubicacion)) {
                botonConfigurarUbicacion.setText("Ubicación: " + ubicacion);
            }
            if (!TextUtils.isEmpty(adjuntoNombre)) {
                botonGestionarAdjuntos.setText("Adjunto: " + adjuntoNombre);
            }
        } else {
            fechaSeleccionada = formatoDb.format(new Date());
            horaSeleccionada = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
            botonSeleccionarFecha.setText(formatoVista.format(new Date()));
            botonSeleccionarHora.setText(horaSeleccionada);
        }
    }

    private void seleccionarSpinner(Spinner spinner, String valor) {
        if (valor == null) return;
        for (int i = 0; i < spinner.getCount(); i++) {
            if (valor.equals(spinner.getItemAtPosition(i).toString())) {
                spinner.setSelection(i);
                return;
            }
        }
    }

    private void confirmarCancelar() {
        String titulo = campoTitulo.getText().toString().trim();
        String desc = campoDescripcion.getText().toString().trim();
        if (titulo.isEmpty() && desc.isEmpty()) { finish(); return; }
        new AlertDialog.Builder(this)
                .setTitle("Descartar cambios")
                .setMessage("¿Deseas salir sin guardar?")
                .setPositiveButton("Descartar", (d, w) -> finish())
                .setNegativeButton("Seguir editando", null)
                .show();
    }

    private void guardarRecordatorio() {
        String titulo = campoTitulo.getText().toString().trim();
        if (TextUtils.isEmpty(titulo)) {
            campoTitulo.setError("El título es obligatorio");
            campoTitulo.requestFocus();
            return;
        }

        Recordatorio recordatorio = new Recordatorio();
        recordatorio.setId(recordatorioId);
        recordatorio.setTitulo(titulo);
        recordatorio.setDescripcion(campoDescripcion.getText().toString().trim());
        recordatorio.setFecha(fechaSeleccionada);
        recordatorio.setHora(horaSeleccionada);
        recordatorio.setCategoria(selectorCategoria.getSelectedItem().toString());
        recordatorio.setPrioridad(selectorPrioridad.getSelectedItem().toString());
        recordatorio.setEstado(Recordatorio.ESTADO_PENDIENTE);
        recordatorio.setUbicacion(ubicacion);
        recordatorio.setAdjuntoRuta(adjuntoRuta);
        recordatorio.setAdjuntoNombre(adjuntoNombre);

        if (recordatorioId > 0) {
            Recordatorio existente = dbHelper.obtenerPorId(recordatorioId);
            if (existente != null) {
                recordatorio.setEstado(existente.getEstado());
            }
            dbHelper.actualizarRecordatorio(recordatorio);
            Toast.makeText(this, "Recordatorio actualizado", Toast.LENGTH_SHORT).show();
        } else {
            dbHelper.crearRecordatorio(recordatorio);
            Toast.makeText(this, "Recordatorio guardado", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void mostrarDatePicker() {
        long seleccion = MaterialDatePicker.todayInUtcMilliseconds();
        try {
            Date fecha = formatoDb.parse(fechaSeleccionada);
            if (fecha != null) {
                seleccion = fecha.getTime();
            }
        } catch (ParseException ignored) {
        }

        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Seleccionar fecha")
                .setSelection(seleccion)
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.setTimeInMillis(selection);
            Date fecha = calendar.getTime();
            fechaSeleccionada = formatoDb.format(fecha);
            botonSeleccionarFecha.setText(formatoVista.format(fecha));
        });

        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
    }

    private void mostrarTimePicker() {
        Calendar calendar = Calendar.getInstance();
        String[] partes = horaSeleccionada != null ? horaSeleccionada.split(":") : new String[0];
        int hora = partes.length == 2 ? Integer.parseInt(partes[0]) : calendar.get(Calendar.HOUR_OF_DAY);
        int minuto = partes.length == 2 ? Integer.parseInt(partes[1]) : calendar.get(Calendar.MINUTE);

        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(hora)
                .setMinute(minuto)
                .setTitleText("Seleccionar hora")
                .build();

        timePicker.addOnPositiveButtonClickListener(v -> {
            horaSeleccionada = String.format(Locale.getDefault(), "%02d:%02d", timePicker.getHour(), timePicker.getMinute());
            botonSeleccionarHora.setText(horaSeleccionada);
        });

        timePicker.show(getSupportFragmentManager(), "TIME_PICKER");
    }

    private boolean hayCambiosSinGuardar() {
        String titulo = campoTitulo.getText().toString().trim();
        String descripcion = campoDescripcion.getText().toString().trim();
        if (recordatorioId > 0) {
            Recordatorio original = dbHelper.obtenerPorId(recordatorioId);
            if (original == null) return false;
            return !titulo.equals(original.getTitulo())
                    || !descripcion.equals(TextUtils.isEmpty(original.getDescripcion()) ? "" : original.getDescripcion().trim());
        }
        return !titulo.isEmpty() || !descripcion.isEmpty();
    }

    private void confirmarCancelar() {
        if (!hayCambiosSinGuardar()) {
            finish();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("¿Descartar cambios?")
                .setMessage("Tienes cambios sin guardar. ¿Deseas salir sin guardar?")
                .setPositiveButton("Salir", (dialog, which) -> finish())
                .setNegativeButton("Seguir editando", null)
                .show();
    }

    @Override
    public void onBackPressed() {
        confirmarCancelar();
    }

    private String formatearFechaVista(String fechaDb) {
        try {
            Date fecha = formatoDb.parse(fechaDb);
            return fecha != null ? formatoVista.format(fecha) : fechaDb;
        } catch (ParseException e) {
            return fechaDb;
        }
    }
}
