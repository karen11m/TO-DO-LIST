package com.example.to_dolist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.ViewHolder> {

    private List<Usuario> usuarios;
    private Context context;
    private FirebaseFirestore firestore;

    public UsuarioAdapter(List<Usuario> usuarios, Context context) {
        this.usuarios = usuarios;
        this.context = context;
        this.firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuario, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Usuario usuario = usuarios.get(position);

        holder.tvNombre.setText(usuario.getNombre());
        holder.tvEmail.setText(usuario.getEmail());
        holder.tvRol.setText(usuario.getRol().equals("admin") ? "Administrador" : "Empleado");

        holder.btnCambiarRol.setOnClickListener(v -> {
            String nuevoRol = usuario.getRol().equals("admin") ? "empleado" : "admin";
            firestore.collection("usuarios").document(usuario.getUid())
                    .update("rol", nuevoRol)
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(context, "Rol actualizado", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        holder.btnEliminar.setOnClickListener(v ->
                new AlertDialog.Builder(context)
                        .setTitle("Eliminar usuario")
                        .setMessage("¿Eliminar a " + usuario.getNombre() + " de la base de datos?")
                        .setPositiveButton("Eliminar", (dialog, which) -> {
                            firestore.collection("usuarios").document(usuario.getUid())
                                    .delete()
                                    .addOnSuccessListener(aVoid ->
                                            Toast.makeText(context, "Usuario eliminado", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e ->
                                            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        })
                        .setNegativeButton("Cancelar", null)
                        .show());
    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvEmail, tvRol;
        ImageButton btnCambiarRol, btnEliminar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreUsuario);
            tvEmail = itemView.findViewById(R.id.tvEmailUsuario);
            tvRol = itemView.findViewById(R.id.tvRolUsuario);
            btnCambiarRol = itemView.findViewById(R.id.btnCambiarRol);
            btnEliminar = itemView.findViewById(R.id.btnEliminarUsuario);
        }
    }
}