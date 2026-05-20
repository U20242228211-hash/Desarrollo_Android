package com.example.fase2;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ListaGeneralRecordatoriosActivity extends AppCompatActivity {
    private Button botonVolverAlInicio;
    private Button botonCrearDesdeLista;
    private LinearLayout contenedorListaRecordatorios;
    private RecordatorioDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_general_recordatorios);

        dbHelper = new RecordatorioDbHelper(this);
        botonVolverAlInicio = findViewById(R.id.botonVolverAlInicioDesdeLista);
        botonCrearDesdeLista = findViewById(R.id.botonCrearDesdeLista);
        contenedorListaRecordatorios = findViewById(R.id.contenedorListaRecordatorios);

        botonVolverAlInicio.setOnClickListener(v -> finish());
        botonCrearDesdeLista.setOnClickListener(v -> startActivity(new Intent(this, CrearEditarRecordatorioActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarLista();
    }

    private void cargarLista() {
        contenedorListaRecordatorios.removeAllViews();
        List<Recordatorio> recordatorios = dbHelper.listarTodos();
        if (recordatorios.isEmpty()) {
            contenedorListaRecordatorios.addView(crearTextoVacio("No hay recordatorios guardados."));
            return;
        }

        for (Recordatorio recordatorio : recordatorios) {
            contenedorListaRecordatorios.addView(crearItem(recordatorio));
        }
    }

    private LinearLayout crearItem(Recordatorio recordatorio) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.HORIZONTAL);
        item.setGravity(android.view.Gravity.CENTER_VERTICAL);
        item.setBackgroundColor(0xFFFFFFFF);
        item.setPadding(24, 20, 24, 20);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 16);
        item.setLayoutParams(params);
        item.setClickable(true);
        item.setFocusable(true);

        LinearLayout textos = new LinearLayout(this);
        textos.setOrientation(LinearLayout.VERTICAL);
        textos.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        TextView titulo = new TextView(this);
        titulo.setText(recordatorio.getTitulo());
        titulo.setTextSize(16);
        titulo.setTypeface(Typeface.DEFAULT_BOLD);
        textos.addView(titulo);

        TextView detalle = new TextView(this);
        detalle.setText(recordatorio.getFecha() + " " + recordatorio.getHora() + " - " +
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
        checkBox.setOnClickListener(v -> completarRecordatorio(recordatorio.getId()));

        item.addView(textos);
        item.addView(checkBox);
        item.setOnClickListener(v -> abrirDetalle(recordatorio.getId()));
        return item;
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
