package com.example.to_dolist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private FloatingActionButton fab;
    private LinearLayout layoutVacio;
    private BottomNavigationView bottomNavigation;
    private TextView tvProgresoTexto, tvContadorTareas;
    private ProgressBar progressBarTareas;

    private FirebaseHelper firebaseHelper;
    private List<Task> taskList = new ArrayList<>();
    private ListenerRegistration listenerRegistration;  // ← Para liberar memoria

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        fab = findViewById(R.id.fab_agregar);
        layoutVacio = findViewById(R.id.layoutVacio);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        tvProgresoTexto = findViewById(R.id.tvProgresoTexto);
        tvContadorTareas = findViewById(R.id.tvContadorTareas);
        progressBarTareas = findViewById(R.id.progressBarTareas);

        firebaseHelper = new FirebaseHelper();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TaskAdapter(taskList, this);
        recyclerView.setAdapter(adapter);

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
            startActivity(intent);
        });

        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_calendar) {
                startActivity(new Intent(MainActivity.this, CalendarActivity.class));
                return true;
            } else if (id == R.id.nav_stats) {
                startActivity(new Intent(MainActivity.this, StatsActivity.class));
                return true;
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            }
            return false;
        });

        conectarFirestore();
    }

    private void conectarFirestore() {
        // Guardamos la referencia del listener para poder liberarla después
        listenerRegistration = firebaseHelper.obtenerTareasEnTiempoReal(new FirebaseHelper.OnTasksLoadedListener() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                taskList.clear();
                taskList.addAll(tasks);
                adapter.notifyDataSetChanged();
                actualizarUI();

                if (taskList.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    layoutVacio.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    layoutVacio.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(MainActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void actualizarUI() {
        int completadas = 0;
        int total = taskList.size();

        for (Task t : taskList) {
            if (t.isCompletado()) {
                completadas++;
            }
        }

        int porcentaje = total > 0 ? (completadas * 100 / total) : 0;
        int pendientes = total - completadas;

        tvProgresoTexto.setText(porcentaje + "%");
        progressBarTareas.setProgress(porcentaje);
        tvContadorTareas.setText(pendientes + " tareas pendientes");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    // ✅ Liberar memoria de Firebase al cerrar la app
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
            listenerRegistration = null;
        }
    }
}