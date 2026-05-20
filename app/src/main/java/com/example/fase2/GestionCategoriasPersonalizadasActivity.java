package com.example.fase2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class GestionCategoriasPersonalizadasActivity extends AppCompatActivity {

    private Button botonVolver;
    private Button botonAgregarCategoria;
    
    // Botones de eliminación (visuales)
    private ImageButton botonEliminarPersonal;
    private ImageButton botonEliminarTrabajo;
    private ImageButton botonEliminarSalud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_categorias_personalizadas);

        // Referencias
        botonVolver = findViewById(R.id.botonVolverCategorias);
        botonAgregarCategoria = findViewById(R.id.botonAgregarCategoria);
        
        botonEliminarPersonal = findViewById(R.id.botonEliminarPersonal);
        botonEliminarTrabajo = findViewById(R.id.botonEliminarTrabajo);
        botonEliminarSalud = findViewById(R.id.botonEliminarSalud);

        // Navegación de regreso
        botonVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Acciones visuales simples
        botonAgregarCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GestionCategoriasPersonalizadasActivity.this, "Categoría agregada (Simulado)", Toast.LENGTH_SHORT).show();
            }
        });

        View.OnClickListener accionEliminar = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GestionCategoriasPersonalizadasActivity.this, "Categoría eliminada (Simulado)", Toast.LENGTH_SHORT).show();
            }
        };

        botonEliminarPersonal.setOnClickListener(accionEliminar);
        botonEliminarTrabajo.setOnClickListener(accionEliminar);
        botonEliminarSalud.setOnClickListener(accionEliminar);
    }
}
