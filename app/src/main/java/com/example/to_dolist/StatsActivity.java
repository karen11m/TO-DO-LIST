package com.example.to_dolist;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.List;

public class StatsActivity extends AppCompatActivity {

    private TextView tvTotalTareas, tvCompletadas, tvPendientes, tvPorcentaje, tvEstadoGeneral;
    private ProgressBar progressBarStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("📊 Estadísticas");

        tvTotalTareas = findViewById(R.id.tvTotalTareas);
        tvCompletadas = findViewById(R.id.tvCompletadas);
        tvPendientes = findViewById(R.id.tvPendientes);
        tvPorcentaje = findViewById(R.id.tvPorcentaje);
        tvEstadoGeneral = findViewById(R.id.tvEstadoGeneral);
        progressBarStats = findViewById(R.id.progressBarStats);

        cargarEstadisticas();
    }

    private void cargarEstadisticas() {
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.obtenerTareasEnTiempoReal(new FirebaseHelper.OnTasksLoadedListener() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                int total = tasks.size();
                int completadas = 0;
                for (Task t : tasks) {
                    if (t.isCompletado()) {
                        completadas++;
                    }
                }
                int pendientes = total - completadas;
                int porcentaje = total > 0 ? (completadas * 100 / total) : 0;

                tvTotalTareas.setText("📋 Total tareas: " + total);
                tvCompletadas.setText("✅ Completadas: " + completadas);
                tvPendientes.setText("⏳ Pendientes: " + pendientes);
                tvPorcentaje.setText("📊 Progreso: " + porcentaje + "%");
                progressBarStats.setProgress(porcentaje);

                if (total == 0) {
                    tvEstadoGeneral.setText("💡 Aún no tienes tareas");
                } else if (porcentaje == 100) {
                    tvEstadoGeneral.setText("🎉 ¡Felicidades! Todas completadas");
                } else if (porcentaje >= 50) {
                    tvEstadoGeneral.setText("💪 ¡Vas por buen camino!");
                } else {
                    tvEstadoGeneral.setText("📝 ¡Ánimo, tú puedes!");
                }
            }

            @Override
            public void onError(String error) {
                tvTotalTareas.setText("❌ Error al cargar datos");
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}