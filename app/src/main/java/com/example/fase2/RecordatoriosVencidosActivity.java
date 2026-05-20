package com.example.fase2;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class RecordatoriosVencidosActivity extends AppCompatActivity {
    private Button botonVolver;
    private LinearLayout contenedorListaVencidos;
    private RecordatorioDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordatorios_vencidos);

        dbHelper = new RecordatorioDbHelper(this);
        botonVolver = findViewById(R.id.botonVolverVencidos);
        contenedorListaVencidos = findViewById(R.id.contenedorListaVencidos);
        botonVolver.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarVencidos();
    }

    private void cargarVencidos() {
        contenedorListaVencidos.removeAllViews();
        List<Recordatorio> recordatorios = dbHelper.listarVencidos();
        if (recordatorios.isEmpty()) {
            contenedorListaVencidos.addView(crearTexto("No hay recordatorios vencidos.", false));
            return;
        }
        for (Recordatorio recordatorio : recordatorios) {
            contenedorListaVencidos.addView(crearItem(recordatorio));
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
        checkBox.setOnClickListener(v -> completarRecordatorio(recordatorio.getId()));

        item.addView(textos);
        item.addView(checkBox);
        item.setOnClickListener(v -> abrirDetalle(recordatorio.getId()));
        return item;
    }

    private TextView crearTexto(String texto, boolean titulo) {
        TextView textView = new TextView(this);
        textView.setText(texto);
        textView.setTextSize(titulo ? 16 : 13);
        textView.setTextColor(titulo ? 0xFF212121 : 0xFFD32F2F);
        if (titulo) textView.setTypeface(Typeface.DEFAULT_BOLD);
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
        cargarVencidos();
    }
}
