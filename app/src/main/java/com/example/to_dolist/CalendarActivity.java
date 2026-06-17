package com.example.to_dolist;

import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;      // ← IMPORTANTE: Agregar este import
import java.util.List;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private TextView tvFechaSeleccionada, tvTareasDelDia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Calendario");

        calendarView = findViewById(R.id.calendarView);
        tvFechaSeleccionada = findViewById(R.id.tvFechaSeleccionada);
        tvTareasDelDia = findViewById(R.id.tvTareasDelDia);

        // Fecha actual
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd 'de' MMMM yyyy", new Locale("es", "ES"));
        tvFechaSeleccionada.setText("📌 " + sdf.format(cal.getTime()));

        // Cargar tareas del día
        cargarTareasDelDia(cal.getTime());

        // Escuchar selección
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar selected = Calendar.getInstance();
            selected.set(year, month, dayOfMonth);
            String fecha = new SimpleDateFormat("EEEE, dd 'de' MMMM yyyy", new Locale("es", "ES")).format(selected.getTime());
            tvFechaSeleccionada.setText("📌 " + fecha);
            cargarTareasDelDia(selected.getTime());
        });
    }

    private void cargarTareasDelDia(Date fecha) {
        FirebaseHelper helper = new FirebaseHelper();
        helper.obtenerTareasEnTiempoReal(new FirebaseHelper.OnTasksLoadedListener() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String fechaStr = sdf.format(fecha);
                int contador = 0;

                for (Task t : tasks) {
                    if (t.getFecha() != null) {
                        String taskFecha = sdf.format(t.getFecha());
                        if (taskFecha.equals(fechaStr)) {
                            contador++;
                        }
                    }
                }

                tvTareasDelDia.setText((contador == 0)
                        ? "📭 No hay tareas para este día"
                        : "📋 " + contador + " tareas para este día");
            }

            @Override
            public void onError(String error) {
                tvTareasDelDia.setText("❌ Error al cargar tareas");
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}