package com.example.to_dolist;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private List<Task> lista;
    private DBHelper db;
    private Context context;
    private MainActivity mainActivity;  // ← NUEVO

    public TaskAdapter(List<Task> lista, DBHelper db, Context context) {
        this.lista = lista;
        this.db = db;
        this.context = context;
        // Guardar referencia a MainActivity
        if (context instanceof MainActivity) {
            this.mainActivity = (MainActivity) context;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Task t = lista.get(position);
        holder.titulo.setText(t.getTitulo());
        holder.descripcion.setText(t.getDescripcion());
        holder.check.setChecked(t.getEstado() == 1);
        holder.tvFecha.setText(t.getFecha());

        // ← CORREGIDO: actualizar contador al marcar/desmarcar
        holder.check.setOnClickListener(v -> {
            int nuevoEstado = holder.check.isChecked() ? 1 : 0;
            db.actualizarEstado(t.getId(), nuevoEstado);
            t.setEstado(nuevoEstado);

            // Actualizar el contador en MainActivity
            if (mainActivity != null) {
                mainActivity.actualizarContador();  // ← Llama a este método
            }
        });

        holder.btnEditar.setOnClickListener(v -> {
            Intent i = new Intent(context, AddEditTaskActivity.class);
            i.putExtra("id", t.getId());
            i.putExtra("titulo", t.getTitulo());
            i.putExtra("descripcion", t.getDescripcion());
            context.startActivity(i);
        });

        holder.btnEliminar.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Eliminar Tarea")
                    .setMessage("¿Eliminar esta tarea?")
                    .setPositiveButton("Eliminar", (dialog, which) -> {
                        db.eliminar(t.getId());
                        lista.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, lista.size());

                        // Actualizar todo al eliminar
                        if (mainActivity != null) {
                            mainActivity.cargarTareas();
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, descripcion, tvFecha;
        CheckBox check;
        ImageButton btnEditar, btnEliminar;

        public ViewHolder(View view) {
            super(view);
            titulo = view.findViewById(R.id.titulo);
            descripcion = view.findViewById(R.id.descripcion);
            check = view.findViewById(R.id.check);
            btnEditar = view.findViewById(R.id.btn_editar);
            btnEliminar = view.findViewById(R.id.btn_eliminar);
            tvFecha = view.findViewById(R.id.tvFecha);
        }
    }
}