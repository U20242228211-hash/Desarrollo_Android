package com.example.fase2;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ListaGeneralRecordatoriosActivity extends AppCompatActivity {
    private Button botonVolverAlInicio;
    private Button botonCrearDesdeLista;
    private LinearLayout contenedorListaRecordatorios;
    private Spinner filtroCategoria;
    private Spinner filtroEstado;
    private RecordatorioDbHelper dbHelper;

    private String categoriaSeleccionada = "Todas";
    private String estadoSeleccionado = "Todos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_general_recordatorios);

        dbHelper = new RecordatorioDbHelper(this);
        botonVolverAlInicio = findViewById(R.id.botonVolverAlInicioDesdeLista);
        botonCrearDesdeLista = findViewById(R.id.botonCrearDesdeLista);
        contenedorListaRecordatorios = findViewById(R.id.contenedorListaRecordatorios);
        filtroCategoria = findViewById(R.id.filtroCategoria);
        filtroEstado = findViewById(R.id.filtroEstado);

        configurarFiltros();

        botonVolverAlInicio.setOnClickListener(v -> finish());
        botonCrearDesdeLista.setOnClickListener(v -> startActivity(new Intent(this, CrearEditarRecordatorioActivity.class)));
    }

    private void configurarFiltros() {
        ArrayAdapter<String> adapterCat = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Todas", "Personal", "Trabajo", "Salud", "Estudio", "Otro"});
        adapterCat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filtroCategoria.setAdapter(adapterCat);

        ArrayAdapter<String> adapterEst = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Todos", "Pendiente", "Completado"});
        adapterEst.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filtroEstado.setAdapter(adapterEst);

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> p, android.view.View v, int pos, long id) {
                categoriaSeleccionada = filtroCategoria.getSelectedItem().toString();
                estadoSeleccionado = filtroEstado.getSelectedItem().toString();
                cargarLista();
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        };
        filtroCategoria.setOnItemSelectedListener(listener);
        filtroEstado.setOnItemSelectedListener(listener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarLista();
    }

    private void cargarLista() {
        contenedorListaRecordatorios.removeAllViews();
        List<Recordatorio> todos = dbHelper.listarTodos();
        List<Recordatorio> recordatorios = new java.util.ArrayList<>();
        for (Recordatorio r : todos) {
            boolean catOk = categoriaSeleccionada.equals("Todas") || categoriaSeleccionada.equals(r.getCategoria());
            boolean estOk = estadoSeleccionado.equals("Todos") || estadoSeleccionado.equals(r.getEstado());
            if (catOk && estOk) recordatorios.add(r);
        }
        if (recordatorios.isEmpty()) {
            contenedorListaRecordatorios.addView(crearTextoVacio("No hay recordatorios guardados."));
            return;
        }
        for (Recordatorio recordatorio : recordatorios) {
            contenedorListaRecordatorios.addView(crearItem(recordatorio));
        }
    }

    private int colorPrioridad(String prioridad) {
        if ("Alta".equals(prioridad)) return 0xFFD32F2F;
        if ("Media".equals(prioridad)) return 0xFFFFA000;
        return 0xFF9E9E9E;
    }

    private LinearLayout crearItem(Recordatorio recordatorio) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.HORIZONTAL);
        item.setGravity(android.view.Gravity.CENTER_VERTICAL);
        item.setBackgroundColor(0xFFFFFFFF);
        item.setPadding(0, 0, 24, 0);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 16);
        item.setLayoutParams(params);
        item.setClickable(true);
        item.setFocusable(true);

        android.view.View barra = new android.view.View(this);
        LinearLayout.LayoutParams barraParams = new LinearLayout.LayoutParams(16, ViewGroup.LayoutParams.MATCH_PARENT);
        barra.setLayoutParams(barraParams);
        barra.setBackgroundColor(colorPrioridad(recordatorio.getPrioridad()));
        item.addView(barra);

        LinearLayout textos = new LinearLayout(this);
        textos.setOrientation(LinearLayout.VERTICAL);
        textos.setPadding(20, 20, 0, 20);
        textos.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        TextView titulo = new TextView(this);
        titulo.setText(recordatorio.getTitulo());
        titulo.setTextSize(16);
        titulo.setTypeface(Typeface.DEFAULT_BOLD);
        textos.addView(titulo);

        TextView detalle = new TextView(this);
        detalle.setText(formatearFecha(recordatorio.getFecha()) + " " + recordatorio.getHora() + " - " +
                recordatorio.getCategoria() + " - " + recordatorio.getEstado());
        detalle.setTextColor(0xFF757575);
        textos.addView(detalle);

        if (!TextUtils.isEmpty(recordatorio.getDescripcion())) {
            TextView descripcion = new TextView(this);
            descripcion.setText(recordatorio.getDescripcion());
            descripcion.setMaxLines(2);
            descripcion.setEllipsize(TextUtils.TruncateAt.END);
            textos.addView(descripcion);
        }

        CheckBox checkBox = new CheckBox(this);
        checkBox.setChecked(Recordatorio.ESTADO_COMPLETADO.equals(recordatorio.getEstado()));
        checkBox.setEnabled(!Recordatorio.ESTADO_COMPLETADO.equals(recordatorio.getEstado()));
        checkBox.setMinWidth(48);
        checkBox.setMinHeight(48);
        checkBox.setOnClickListener(v -> completarRecordatorio(recordatorio.getId()));

        item.addView(textos);
        item.addView(checkBox);
        item.setOnClickListener(v -> abrirDetalle(recordatorio.getId()));
        return item;
    }

    private String formatearFecha(String fechaDb) {
        try {
            Date fecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(fechaDb);
            return fecha != null ? new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(fecha) : fechaDb;
        } catch (ParseException e) { return fechaDb; }
    }

    private TextView crearTextoVacio(String texto) {
        TextView textView = new TextView(this);
        textView.setText(texto);
        textView.setPadding(12, 24, 12, 24);
        textView.setTextColor(0xFF757575);
        return textView;
    }

    private void abrirDetalle(long id) {
        Intent intent = new Intent(this, DetalleRecordatorioActivity.class);
        intent.putExtra(CrearEditarRecordatorioActivity.EXTRA_RECORDATORIO_ID, id);
        startActivity(intent);
    }

    private void completarRecordatorio(long id) {
        dbHelper.marcarCompletado(id);
        Toast.makeText(this, "Recordatorio marcado como completado", Toast.LENGTH_SHORT).show();
        cargarLista();
    }
}
