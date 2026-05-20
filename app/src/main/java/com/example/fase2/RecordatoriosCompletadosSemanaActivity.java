package com.example.fase2;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class RecordatoriosCompletadosSemanaActivity extends AppCompatActivity {
    private Button botonVolver;
    private LinearLayout contenedorListaCompletados;
    private RecordatorioDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordatorios_completados_semana);

        dbHelper = new RecordatorioDbHelper(this);
        botonVolver = findViewById(R.id.botonVolverCompletados);
        contenedorListaCompletados = findViewById(R.id.contenedorListaCompletados);
        botonVolver.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarCompletados();
    }

    private void cargarCompletados() {
        contenedorListaCompletados.removeAllViews();
        List<Recordatorio> recordatorios = dbHelper.listarCompletados();
        if (recordatorios.isEmpty()) {
            contenedorListaCompletados.addView(crearTexto("No hay recordatorios completados.", false));
            return;
        }
        for (Recordatorio recordatorio : recordatorios) {
            contenedorListaCompletados.addView(crearItem(recordatorio));
        }
    }

    private LinearLayout crearItem(Recordatorio recordatorio) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.HORIZONTAL);
        item.setGravity(android.view.Gravity.CENTER_VERTICAL);
        item.setBackgroundColor(0xFFFFFFFF);
        item.setPadding(28, 24, 28, 24);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 16);
        item.setLayoutParams(params);
        item.setClickable(true);
        item.setFocusable(true);

        LinearLayout textos = new LinearLayout(this);
        textos.setOrientation(LinearLayout.VERTICAL);
        textos.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        textos.addView(crearTexto(recordatorio.getTitulo(), true));
        textos.addView(crearTexto(recordatorio.getFecha() + " - " + recordatorio.getHora(), false));

        CheckBox checkBox = new CheckBox(this);
        checkBox.setChecked(true);
        checkBox.setClickable(false);
        checkBox.setFocusable(false);

        item.addView(textos);
        item.addView(checkBox);
        item.setOnClickListener(v -> abrirDetalle(recordatorio.getId()));
        return item;
    }

    private TextView crearTexto(String texto, boolean titulo) {
        TextView textView = new TextView(this);
        textView.setText(texto);
        textView.setTextSize(titulo ? 16 : 13);
        textView.setTextColor(titulo ? 0xFF757575 : 0xFF388E3C);
        if (titulo) textView.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC));
        return textView;
    }

    private void abrirDetalle(long id) {
        Intent intent = new Intent(this, DetalleRecordatorioActivity.class);
        intent.putExtra(CrearEditarRecordatorioActivity.EXTRA_RECORDATORIO_ID, id);
        startActivity(intent);
    }
}
