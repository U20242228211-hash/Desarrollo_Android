package com.example.fase2;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetalleRecordatorioActivity extends AppCompatActivity {
    private Button botonEditar;
    private Button botonEliminar;
    private Button botonVolver;
    private Button botonCompartir;
    private Button botonCompletado;
    private TextView textoTituloDetalle;
    private TextView textoDescripcionDetalle;
    private TextView textoCategoriaDetalle;
    private TextView textoFechaDetalle;
    private TextView textoHoraDetalle;
    private TextView textoPrioridadDetalle;
    private TextView textoEstadoDetalle;
    private TextView seccionAdjuntosDetalle;
    private TextView seccionUbicacionDetalle;

    private RecordatorioDbHelper dbHelper;
    private long recordatorioId;
    private Recordatorio recordatorio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_recordatorio);

        dbHelper = new RecordatorioDbHelper(this);
        recordatorioId = getIntent().getLongExtra(CrearEditarRecordatorioActivity.EXTRA_RECORDATORIO_ID, -1);

        textoTituloDetalle = findViewById(R.id.textoTituloDetalle);
        textoDescripcionDetalle = findViewById(R.id.textoDescripcionDetalle);
        textoCategoriaDetalle = findViewById(R.id.textoCategoriaDetalle);
        textoFechaDetalle = findViewById(R.id.textoFechaDetalle);
        textoHoraDetalle = findViewById(R.id.textoHoraDetalle);
        textoPrioridadDetalle = findViewById(R.id.textoPrioridadDetalle);
        textoEstadoDetalle = findViewById(R.id.textoEstadoDetalle);
        seccionAdjuntosDetalle = findViewById(R.id.seccionAdjuntosDetalle);
        seccionUbicacionDetalle = findViewById(R.id.seccionUbicacionDetalle);
        botonEditar = findViewById(R.id.botonEditarRecordatorio);
        botonEliminar = findViewById(R.id.botonEliminarRecordatorio);
        botonVolver = findViewById(R.id.botonVolverDesdeDetalle);
        botonCompartir = findViewById(R.id.botonCompartirRecordatorio);
        botonCompletado = findViewById(R.id.botonMarcarCompletado);

        botonEditar.setOnClickListener(v -> {
            Intent intent = new Intent(this, CrearEditarRecordatorioActivity.class);
            intent.putExtra(CrearEditarRecordatorioActivity.EXTRA_RECORDATORIO_ID, recordatorioId);
            startActivity(intent);
        });

        botonEliminar.setOnClickListener(v -> confirmarEliminacion());
        botonCompletado.setOnClickListener(v -> marcarCompletado());
        botonCompartir.setOnClickListener(v -> compartirRecordatorio());
        botonVolver.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarRecordatorio();
    }

    private void cargarRecordatorio() {
        if (recordatorioId <= 0) {
            Toast.makeText(this, "No se recibió el recordatorio", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        recordatorio = dbHelper.obtenerPorId(recordatorioId);
        if (recordatorio == null) {
            Toast.makeText(this, "El recordatorio ya no existe", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        textoTituloDetalle.setText(recordatorio.getTitulo());
        textoDescripcionDetalle.setText(valorOTexto(recordatorio.getDescripcion(), "Sin descripción"));
        textoCategoriaDetalle.setText(valorOTexto(recordatorio.getCategoria(), "Sin categoría"));
        textoFechaDetalle.setText(formatearFechaVista(recordatorio.getFecha()));
        textoHoraDetalle.setText(recordatorio.getHora());
        textoPrioridadDetalle.setText(valorOTexto(recordatorio.getPrioridad(), "Sin prioridad"));
        textoEstadoDetalle.setText(recordatorio.getEstado());
        seccionAdjuntosDetalle.setText(valorOTexto(recordatorio.getAdjuntoNombre(), "Sin adjuntos"));

        if (!TextUtils.isEmpty(recordatorio.getUbicacion())) {
            seccionUbicacionDetalle.setText(recordatorio.getUbicacion());
        } else {
            seccionUbicacionDetalle.setText("Sin ubicación asociada");
        }

        boolean completado = Recordatorio.ESTADO_COMPLETADO.equals(recordatorio.getEstado());
        botonCompletado.setEnabled(!completado);
        botonCompletado.setText(completado ? "Completado" : "Marcar como Completado");
    }

    private void confirmarEliminacion() {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar recordatorio")
                .setMessage("¿Deseas eliminar este recordatorio guardado?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    dbHelper.eliminarRecordatorio(recordatorioId);
                    Toast.makeText(this, "Recordatorio eliminado", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void marcarCompletado() {
        dbHelper.marcarCompletado(recordatorioId);
        Toast.makeText(this, "Recordatorio completado", Toast.LENGTH_SHORT).show();
        cargarRecordatorio();
    }

    private void compartirRecordatorio() {
        if (recordatorio == null) return;
        String texto = "Recordatorio: " + recordatorio.getTitulo() + "\n" +
                "Fecha: " + formatearFechaVista(recordatorio.getFecha()) + " " + recordatorio.getHora() + "\n" +
                "Categoría: " + valorOTexto(recordatorio.getCategoria(), "Sin categoría") + "\n" +
                "Descripción: " + valorOTexto(recordatorio.getDescripcion(), "Sin descripción");

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Recordatorio SeMeOlvidaNo");
        intent.putExtra(Intent.EXTRA_TEXT, texto);
        startActivity(Intent.createChooser(intent, "Compartir recordatorio"));
    }

    private String valorOTexto(String valor, String textoVacio) {
        return TextUtils.isEmpty(valor) ? textoVacio : valor;
    }

    private String formatearFechaVista(String fechaDb) {
        try {
            Date fecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(fechaDb);
            return fecha != null ? new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(fecha) : fechaDb;
        } catch (ParseException e) {
            return fechaDb;
        }
    }
}
