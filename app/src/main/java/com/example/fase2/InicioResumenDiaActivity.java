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

public class InicioResumenDiaActivity extends AppCompatActivity {
    private Button botonCrearNuevoRecordatorio;
    private Button botonIrACalendario;
    private Button botonIrAConfiguracion;
    private Button botonVerTodos;
    private LinearLayout contenedorPendientesHoy;
    private LinearLayout contenedorProximos;
    private LinearLayout tarjetaVencidos;
    private LinearLayout tarjetaCompletados;
    private TextView textoCantidadVencidos;
    private TextView textoCantidadCompletados;
    private RecordatorioDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_resumen_dia);

        dbHelper = new RecordatorioDbHelper(this);
        botonCrearNuevoRecordatorio = findViewById(R.id.botonCrearNuevoRecordatorio);
        botonIrACalendario = findViewById(R.id.botonIrACalendario);
        botonIrAConfiguracion = findViewById(R.id.botonIrAConfiguracion);
        botonVerTodos = findViewById(R.id.botonVerTodosRecordatorios);
        contenedorPendientesHoy = findViewById(R.id.contenedorPendientesHoy);
        contenedorProximos = findViewById(R.id.contenedorProximos);
        tarjetaVencidos = findViewById(R.id.tarjetaVencidos);
        tarjetaCompletados = findViewById(R.id.tarjetaCompletados);
        textoCantidadVencidos = findViewById(R.id.textoCantidadVencidos);
        textoCantidadCompletados = findViewById(R.id.textoCantidadCompletados);

        botonCrearNuevoRecordatorio.setOnClickListener(v -> startActivity(new Intent(this, CrearEditarRecordatorioActivity.class)));
        botonVerTodos.setOnClickListener(v -> startActivity(new Intent(this, ListaGeneralRecordatoriosActivity.class)));
        botonIrACalendario.setOnClickListener(v -> startActivity(new Intent(this, CalendarioAgendaActivity.class)));
        botonIrAConfiguracion.setOnClickListener(v -> startActivity(new Intent(this, ConfiguracionPreferenciasActivity.class)));
        tarjetaVencidos.setOnClickListener(v -> startActivity(new Intent(this, RecordatoriosVencidosActivity.class)));
        tarjetaCompletados.setOnClickListener(v -> startActivity(new Intent(this, RecordatoriosCompletadosSemanaActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarResumen();
    }

    private void cargarResumen() {
        contenedorPendientesHoy.removeAllViews();
        contenedorProximos.removeAllViews();

        List<Recordatorio> pendientesHoy = dbHelper.listarPendientesDeHoy();
        List<Recordatorio> proximos = dbHelper.listarProximosPendientes();

        if (pendientesHoy.isEmpty()) {
            contenedorPendientesHoy.addView(crearTextoVacio("No tienes pendientes para hoy."));
        } else {
            for (Recordatorio recordatorio : pendientesHoy) {
                contenedorPendientesHoy.addView(crearTarjeta(recordatorio));
            }
        }

        if (proximos.isEmpty()) {
            contenedorProximos.addView(crearTextoVacio("No tienes próximos compromisos."));
        } else {
            for (Recordatorio recordatorio : proximos) {
                contenedorProximos.addView(crearTarjeta(recordatorio));
            }
        }

        textoCantidadVencidos.setText(String.valueOf(dbHelper.contarVencidos()));
        textoCantidadCompletados.setText(String.valueOf(dbHelper.contarCompletados()));
    }

    private int colorPrioridad(String prioridad) {
        if ("Alta".equals(prioridad)) return 0xFFD32F2F;
        if ("Media".equals(prioridad)) return 0xFFFFA000;
        return 0xFF9E9E9E;
    }

    private LinearLayout crearTarjeta(Recordatorio recordatorio) {
        LinearLayout tarjeta = new LinearLayout(this);
        tarjeta.setOrientation(LinearLayout.HORIZONTAL);
        tarjeta.setGravity(android.view.Gravity.CENTER_VERTICAL);
        tarjeta.setBackgroundColor(0xFFFFFFFF);
        tarjeta.setPadding(0, 0, 24, 0);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 16);
        tarjeta.setLayoutParams(params);
        tarjeta.setClickable(true);
        tarjeta.setFocusable(true);

        android.view.View barra = new android.view.View(this);
        LinearLayout.LayoutParams barraParams = new LinearLayout.LayoutParams(16, ViewGroup.LayoutParams.MATCH_PARENT);
        barra.setLayoutParams(barraParams);
        barra.setBackgroundColor(colorPrioridad(recordatorio.getPrioridad()));
        tarjeta.addView(barra);

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
        detalle.setText(recordatorio.getFecha() + " - " + recordatorio.getHora() + " - " + recordatorio.getCategoria());
        detalle.setTextSize(12);
        detalle.setTextColor(0xFF757575);
        textos.addView(detalle);

        CheckBox checkBox = new CheckBox(this);
        checkBox.setClickable(true);
        checkBox.setFocusable(true);
        checkBox.setMinWidth(48);
        checkBox.setMinHeight(48);
        checkBox.setChecked(Recordatorio.ESTADO_COMPLETADO.equals(recordatorio.getEstado()));
        checkBox.setOnClickListener(v -> completarRecordatorio(recordatorio.getId()));

        tarjeta.addView(textos);
        tarjeta.addView(checkBox);
        tarjeta.setOnClickListener(v -> abrirDetalle(recordatorio.getId()));
        return tarjeta;
    }

    private TextView crearTextoVacio(String texto) {
        TextView textView = new TextView(this);
        textView.setText(texto);
        textView.setTextColor(0xFF757575);
        textView.setPadding(12, 16, 12, 16);
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
        cargarResumen();
    }
}
