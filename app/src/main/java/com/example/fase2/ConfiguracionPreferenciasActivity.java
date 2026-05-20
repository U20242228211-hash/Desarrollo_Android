package com.example.fase2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ConfiguracionPreferenciasActivity extends AppCompatActivity {

    private Button botonVolverAlInicio;
    private Button botonLimpiarHistorial;
    private Button botonCategorias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion_preferencias);

        botonVolverAlInicio = findViewById(R.id.botonVolverDesdeConfiguracion);
        botonLimpiarHistorial = findViewById(R.id.botonLimpiarHistorial);
        botonCategorias = findViewById(R.id.botonCategoriasPersonalizadas);

        botonVolverAlInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfiguracionPreferenciasActivity.this, InicioResumenDiaActivity.class);
                startActivity(intent);
                finish();
            }
        });

        botonLimpiarHistorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ConfiguracionPreferenciasActivity.this, "Historial limpiado (Simulado)", Toast.LENGTH_SHORT).show();
            }
        });

        botonCategorias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ahora abre la nueva pantalla de gestión de categorías
                Intent intent = new Intent(ConfiguracionPreferenciasActivity.this, GestionCategoriasPersonalizadasActivity.class);
                startActivity(intent);
            }
        });
    }
}
