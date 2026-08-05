package com.example.to_dolist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.ListenerRegistration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarFragment extends Fragment {

    private CalendarView calendarView;
    private RecyclerView recyclerView;
    private TextView tvFechaSeleccionada;
    private LinearLayout layoutSinTareas;
    private TaskAdapter adapter;

    private FirebaseHelper firebaseHelper;
    private AuthHelper authHelper;
    private ListenerRegistration listenerRegistration;

    private final List<Task> todasLasTareas = new ArrayList<>();
    private final List<Task> tareasFiltradas = new ArrayList<>();
    private String fechaSeleccionadaStr;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authHelper = new AuthHelper();
        firebaseHelper = new FirebaseHelper();

        calendarView = view.findViewById(R.id.calendarView);
        recyclerView = view.findViewById(R.id.recyclerViewCalendario);
        tvFechaSeleccionada = view.findViewById(R.id.tvFechaSeleccionada);
        layoutSinTareas = view.findViewById(R.id.layoutSinTareasCalendario);

        boolean esAdmin = "admin".equals(authHelper.obtenerRolLocal(requireContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new TaskAdapter(tareasFiltradas, requireContext(), esAdmin);
        recyclerView.setAdapter(adapter);

        fechaSeleccionadaStr = formatearFecha(new Date());
        actualizarTituloFecha(new Date());

        calendarView.setOnDateChangeListener((v, year, month, dayOfMonth) -> {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, dayOfMonth);
            fechaSeleccionadaStr = formatearFecha(cal.getTime());
            actualizarTituloFecha(cal.getTime());
            filtrarTareasPorFecha();
        });

        cargarTareas();
    }

    private void cargarTareas() {
        listenerRegistration = firebaseHelper.obtenerTareasEnTiempoReal(new FirebaseHelper.OnTasksLoadedListener() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                if (!isAdded()) return;
                todasLasTareas.clear();
                todasLasTareas.addAll(tasks);
                filtrarTareasPorFecha();
            }

            @Override
            public void onError(String error) { }
        });
    }

    private void filtrarTareasPorFecha() {
        tareasFiltradas.clear();
        for (Task t : todasLasTareas) {
            if (t.getFecha() != null && formatearFecha(t.getFecha()).equals(fechaSeleccionadaStr)) {
                tareasFiltradas.add(t);
            }
        }
        adapter.notifyDataSetChanged();

        boolean vacio = tareasFiltradas.isEmpty();
        recyclerView.setVisibility(vacio ? View.GONE : View.VISIBLE);
        layoutSinTareas.setVisibility(vacio ? View.VISIBLE : View.GONE);
    }

    private void actualizarTituloFecha(Date fecha) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd 'de' MMMM", new Locale("es", "ES"));
        String texto = sdf.format(fecha);
        tvFechaSeleccionada.setText(texto.substring(0, 1).toUpperCase() + texto.substring(1));
    }

    private String formatearFecha(Date fecha) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(fecha);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }
}