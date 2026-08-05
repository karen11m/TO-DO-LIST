package com.example.to_dolist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;

public class StatsFragment extends Fragment {

    private TextView tvTotal, tvCompletadas, tvPendientes, tvPorcentaje;
    private TextView tvAltaCount, tvMediaCount, tvBajaCount;
    private View barAlta, barMedia, barBaja;

    private FirebaseHelper firebaseHelper;
    private ListenerRegistration listenerRegistration;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvTotal = view.findViewById(R.id.tvTotal);
        tvCompletadas = view.findViewById(R.id.tvCompletadasStat);
        tvPendientes = view.findViewById(R.id.tvPendientesStat);
        tvPorcentaje = view.findViewById(R.id.tvPorcentajeStat);
        tvAltaCount = view.findViewById(R.id.tvAltaCount);
        tvMediaCount = view.findViewById(R.id.tvMediaCount);
        tvBajaCount = view.findViewById(R.id.tvBajaCount);
        barAlta = view.findViewById(R.id.barAlta);
        barMedia = view.findViewById(R.id.barMedia);
        barBaja = view.findViewById(R.id.barBaja);

        firebaseHelper = new FirebaseHelper();
        cargarEstadisticas();
    }

    private void cargarEstadisticas() {
        listenerRegistration = firebaseHelper.obtenerTareasEnTiempoReal(new FirebaseHelper.OnTasksLoadedListener() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                if (isAdded()) actualizarEstadisticas(tasks);
            }

            @Override
            public void onError(String error) { }
        });
    }

    private void actualizarEstadisticas(List<Task> tasks) {
        int total = tasks.size();
        int completadas = 0, alta = 0, media = 0, baja = 0;

        for (Task t : tasks) {
            if (t.isCompletado()) completadas++;
            String p = t.getPrioridad();
            if ("Alta".equals(p)) alta++;
            else if ("Media".equals(p)) media++;
            else if ("Baja".equals(p)) baja++;
        }

        int pendientes = total - completadas;
        int porcentaje = total > 0 ? (completadas * 100 / total) : 0;

        tvTotal.setText(String.valueOf(total));
        tvCompletadas.setText(String.valueOf(completadas));
        tvPendientes.setText(String.valueOf(pendientes));
        tvPorcentaje.setText(porcentaje + "%");

        tvAltaCount.setText(String.valueOf(alta));
        tvMediaCount.setText(String.valueOf(media));
        tvBajaCount.setText(String.valueOf(baja));

        actualizarBarra(barAlta, alta, total);
        actualizarBarra(barMedia, media, total);
        actualizarBarra(barBaja, baja, total);
    }

    private void actualizarBarra(View barra, int cantidad, int total) {
        float porcentaje = total > 0 ? (cantidad / (float) total) : 0f;
        barra.post(() -> {
            if (!isAdded()) return;
            View parent = (View) barra.getParent();
            int anchoDisponible = parent.getWidth();
            barra.getLayoutParams().width = (int) (anchoDisponible * porcentaje);
            barra.requestLayout();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }
}