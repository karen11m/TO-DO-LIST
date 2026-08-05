package com.example.to_dolist;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private List<Task> taskList;
    private Context context;
    private FirebaseHelper firebaseHelper;
    private boolean esAdmin;

    public TaskAdapter(List<Task> taskList, Context context, boolean esAdmin) {
        this.taskList = taskList;
        this.context = context;
        this.esAdmin = esAdmin;
        this.firebaseHelper = new FirebaseHelper();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task task = taskList.get(position);

        holder.titulo.setText(task.getTitulo());
        holder.descripcion.setText(task.getDescripcion());
        holder.check.setChecked(task.isCompletado());

        String prioridad = task.getPrioridad();
        if (prioridad != null && !prioridad.isEmpty() && !prioridad.equals("Seleccionar prioridad")) {
            holder.tvPrioridad.setText(prioridad);
            holder.tvPrioridad.setVisibility(View.VISIBLE);
            if (prioridad.equals("Alta")) {
                holder.tvPrioridad.setBackgroundResource(R.drawable.bg_priority_high);
            } else if (prioridad.equals("Media")) {
                holder.tvPrioridad.setBackgroundResource(R.drawable.bg_priority_medium);
            } else if (prioridad.equals("Baja")) {
                holder.tvPrioridad.setBackgroundResource(R.drawable.bg_priority_low);
            }
        } else {
            holder.tvPrioridad.setVisibility(View.GONE);
        }

        String categoria = task.getCategoria();
        if (categoria != null && !categoria.isEmpty() && !categoria.equals("Seleccionar categoría")) {
            holder.tvCategoria.setText(categoria);
            holder.tvCategoria.setVisibility(View.VISIBLE);
        } else {
            holder.tvCategoria.setVisibility(View.GONE);
        }

        if (task.getFecha() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            holder.tvFecha.setText(sdf.format(task.getFecha()));
        }

        // RBAC: solo el admin puede eliminar tareas
        holder.btnEliminar.setVisibility(esAdmin ? View.VISIBLE : View.GONE);

        holder.check.setOnClickListener(v -> {
            task.setCompletado(holder.check.isChecked());
            firebaseHelper.actualizarTarea(task, new FirebaseHelper.OnTaskUpdatedListener() {
                @Override
                public void onSuccess() { }

                @Override
                public void onError(String error) {
                    Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
                    holder.check.setChecked(!task.isCompletado());
                }
            });
        });

        holder.btnEditar.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddEditTaskActivity.class);
            intent.putExtra("id", task.getId());
            intent.putExtra("titulo", task.getTitulo());
            intent.putExtra("descripcion", task.getDescripcion());
            intent.putExtra("completado", task.isCompletado());
            intent.putExtra("prioridad", task.getPrioridad());
            intent.putExtra("categoria", task.getCategoria());
            context.startActivity(intent);
        });

        holder.btnEliminar.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Eliminar Tarea")
                    .setMessage("¿Eliminar esta tarea?")
                    .setPositiveButton("Eliminar", (dialog, which) -> {
                        firebaseHelper.eliminarTarea(task.getId(), new FirebaseHelper.OnTaskDeletedListener() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(context, "✓ Tarea eliminada", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(String error) {
                                Toast.makeText(context, "❌ Error: " + error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, descripcion, tvFecha, tvPrioridad, tvCategoria;
        CheckBox check;
        ImageButton btnEditar, btnEliminar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.titulo);
            descripcion = itemView.findViewById(R.id.descripcion);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvPrioridad = itemView.findViewById(R.id.tvPrioridad);
            tvCategoria = itemView.findViewById(R.id.tvCategoria);
            check = itemView.findViewById(R.id.check);
            btnEditar = itemView.findViewById(R.id.btn_editar);
            btnEliminar = itemView.findViewById(R.id.btn_eliminar);
        }
    }
}