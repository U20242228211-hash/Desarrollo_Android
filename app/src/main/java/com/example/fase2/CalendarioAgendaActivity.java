package com.example.fase2;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarioAgendaActivity extends AppCompatActivity {
    private Button botonVolverAlInicio;
    private CalendarView vistaCalendario;
    private LinearLayout contenedorRecordatoriosCalendario;
    private RecordatorioDbHelper dbHelper;
    private String fechaSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario_agenda);

        dbHelper = new RecordatorioDbHelper(this);
        botonVolverAlInicio = findViewById(R.id.botonVolverAlInicioDesdeCalendario);
        vistaCalendario = findViewById(R.id.vistaCalendario);
        contenedorRecordatoriosCalendario = findViewById(R.id.contenedorRecordatoriosCalendario);

        Calendar calendar = Calendar.getInstance();
        fechaSeleccionada = formatearFecha(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        botonVolverAlInicio.setOnClickListener(v -> finish());
        vistaCalendario.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            fechaSeleccionada = formatearFecha(year, month, dayOfMonth);
            cargarRecordatorios();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarRecordatorios();
    }

    private void cargarRecordatorios() {
        contenedorRecordatoriosCalendario.removeAllViews();
        List<Recordatorio> recordatorios = dbHelper.listarPorFecha(fechaSeleccionada);
        if (recordatorios.isEmpty()) {
            contenedorRecordatoriosCalendario.addView(crearTexto("No hay recordatorios para esta fecha.", false));
            return;
        }

        for (Recordatorio recordatorio : recordatorios) {
            contenedorRecordatoriosCalendario.addView(crearItem(recordatorio));
        }
    }

    private LinearLayout crearItem(Recordatorio recordatorio) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.VERTICAL);
        item.setBackgroundColor(0xFFFFFFFF);
        item.setPadding(24, 20, 24, 20);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 16);
        item.setLayoutParams(params);
        item.setClickable(true);
        item.setFocusable(true);

        TextView titulo = crearTexto(recordatorio.getTitulo(), true);
        TextView detalle = crearTexto(recordatorio.getHora() + " - " + recordatorio.getCategoria() + " - " + recordatorio.getEstado(), false);
        item.addView(titulo);
        item.addView(detalle);
        item.setOnClickListener(v -> abrirDetalle(recordatorio.getId()));
        return item;
    }

    private TextView crearTexto(String texto, boolean titulo) {
        TextView textView = new TextView(this);
        textView.setText(texto);
        textView.setTextSize(titulo ? 16 : 13);
        textView.setTextColor(titulo ? 0xFF212121 : 0xFF757575);
        textView.setPadding(0, 4, 0, 4);
        if (titulo) textView.setTypeface(Typeface.DEFAULT_BOLD);
        return textView;
    }

    private String formatearFecha(int year, int month, int dayOfMonth) {
        return String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
    }

    private void abrirDetalle(long id) {
        Intent intent = new Intent(this, DetalleRecordatorioActivity.class);
        intent.putExtra(CrearEditarRecordatorioActivity.EXTRA_RECORDATORIO_ID, id);
        startActivity(intent);
    }
}
