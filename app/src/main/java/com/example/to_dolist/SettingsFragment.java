package com.example.to_dolist;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsFragment extends Fragment {

    private TextView tvNombrePerfil, tvEmailPerfil, tvRolPerfil, tvInicial;
    private MaterialButton btnCerrarSesion;
    private MaterialCardView cardPanelAdmin;
    private AuthHelper authHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authHelper = new AuthHelper();

        tvNombrePerfil = view.findViewById(R.id.tvNombrePerfil);
        tvEmailPerfil = view.findViewById(R.id.tvEmailPerfil);
        tvRolPerfil = view.findViewById(R.id.tvRolPerfil);
        tvInicial = view.findViewById(R.id.tvInicial);
        btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion);
        cardPanelAdmin = view.findViewById(R.id.cardPanelAdmin);

        cargarPerfil();

        boolean esAdmin = "admin".equals(authHelper.obtenerRolLocal(requireContext()));
        cardPanelAdmin.setVisibility(esAdmin ? View.VISIBLE : View.GONE);
        cardPanelAdmin.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), UserAdminActivity.class)));

        btnCerrarSesion.setOnClickListener(v -> confirmarCerrarSesion());
    }

    private void cargarPerfil() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String rolLocal = authHelper.obtenerRolLocal(requireContext());
        String nombreLocal = authHelper.obtenerNombreLocal(requireContext());

        String email = user != null ? user.getEmail() : "—";
        String nombre = (nombreLocal != null && !nombreLocal.isEmpty()) ? nombreLocal : email;
        String rolTexto = "admin".equals(rolLocal) ? "Administrador" : "Empleado";

        boolean verificado = user != null && user.isEmailVerified();

        tvNombrePerfil.setText(nombre);
        tvEmailPerfil.setText(email + (verificado ? " ✓" : " (sin verificar)"));
        tvRolPerfil.setText(rolTexto);
        tvInicial.setText(nombre.substring(0, 1).toUpperCase());
    }

    private void confirmarCerrarSesion() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Cerrar sesión")
                .setMessage("¿Seguro que quieres salir?")
                .setPositiveButton("Cerrar sesión", (dialog, which) -> {
                    authHelper.cerrarSesion(requireContext());
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}