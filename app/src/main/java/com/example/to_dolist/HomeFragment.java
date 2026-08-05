package com.example.to_dolist;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private LinearLayout layoutVacio;
    private TextView tvProgresoTexto, tvContadorTareas, tvCompletadas;
    private ProgressBar progressBarTareas;
    private ImageButton btnLogout;

    private FirebaseHelper firebaseHelper;
    private AuthHelper authHelper;
    private final List<Task> taskList = new ArrayList<>();
    private ListenerRegistration listenerRegistration;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authHelper = new AuthHelper();
        firebaseHelper = new FirebaseHelper();

        recyclerView = view.findViewById(R.id.recyclerView);
        layoutVacio = view.findViewById(R.id.layoutVacio);
        tvProgresoTexto = view.findViewById(R.id.tvProgresoTexto);
        tvContadorTareas = view.findViewById(R.id.tvContadorTareas);
        tvCompletadas = view.findViewById(R.id.tvCompletadas);
        progressBarTareas = view.findViewById(R.id.progressBarTareas);
        btnLogout = view.findViewById(R.id.btnLogout);

        boolean esAdmin = "admin".equals(authHelper.obtenerRolLocal(requireContext()));

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new TaskAdapter(taskList, requireContext(), esAdmin);
        recyclerView.setAdapter(adapter);

        btnLogout.setOnClickListener(v -> confirmarCerrarSesion());

        conectarFirestore();
    }

    private void confirmarCerrarSesion() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Cerrar sesión")
                .setMessage("¿Seguro que quieres salir?")
                .setPositiveButton("Cerrar sesión", (dialog, which) -> {
                    authHelper.cerrarSesion(requireContext());
                    Toast.makeText(requireContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void conectarFirestore() {
        listenerRegistration = firebaseHelper.obtenerTareasEnTiempoReal(new FirebaseHelper.OnTasksLoadedListener() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                if (!isAdded()) return;
                taskList.clear();
                taskList.addAll(tasks);
                adapter.notifyDataSetChanged();
                actualizarUI();

                boolean vacio = taskList.isEmpty();
                recyclerView.setVisibility(vacio ? View.GONE : View.VISIBLE);
                layoutVacio.setVisibility(vacio ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onError(String error) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void actualizarUI() {
        int completadas = 0;
        int total = taskList.size();

        for (Task t : taskList) {
            if (t.isCompletado()) completadas++;
        }

        int porcentaje = total > 0 ? (completadas * 100 / total) : 0;
        int pendientes = total - completadas;

        tvProgresoTexto.setText(porcentaje + "%");
        progressBarTareas.setProgress(porcentaje);
        tvContadorTareas.setText(pendientes + " pendientes");
        tvCompletadas.setText(completadas + " completadas");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
            listenerRegistration = null;
        }
    }
}