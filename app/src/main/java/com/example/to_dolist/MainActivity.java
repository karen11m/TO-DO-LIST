package com.example.to_dolist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DBHelper db;
    private TaskAdapter adapter;
    private FloatingActionButton fab;
    private TextView tvContador;
    private LinearLayout layoutVacio;
    private List<Task> listaTareas;  // ← Guardar la lista

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        fab = findViewById(R.id.fab_agregar);
        tvContador = findViewById(R.id.tvContador);
        layoutVacio = findViewById(R.id.layoutVacio);

        db = new DBHelper(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
            startActivity(intent);
        });

        cargarTareas();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarTareas();
    }


    public void cargarTareas() {
        listaTareas = db.obtenerTareas();
        adapter = new TaskAdapter(listaTareas, db, this);
        recyclerView.setAdapter(adapter);
        actualizarContador();
    }


    public void actualizarContador() {
        int pendientes = 0;

        if (listaTareas != null) {
            for (Task t : listaTareas) {
                if (t.getEstado() == 0) {
                    pendientes++;
                }
            }
        }

        if (listaTareas == null || listaTareas.isEmpty()) {
            tvContador.setText("0 tareas pendientes");
            recyclerView.setVisibility(View.GONE);
            layoutVacio.setVisibility(View.VISIBLE);
        } else {
            if (pendientes == 0) {
                tvContador.setText("✓ Todas completadas");
            } else {
                tvContador.setText(pendientes + " tareas pendientes");
            }
            recyclerView.setVisibility(View.VISIBLE);
            layoutVacio.setVisibility(View.GONE);
        }
    }
}