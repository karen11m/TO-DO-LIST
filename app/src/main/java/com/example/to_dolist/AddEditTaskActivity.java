package com.example.to_dolist;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddEditTaskActivity extends AppCompatActivity {
    EditText etTitulo, etDescripcion;
    Button btnGuardar;
    DBHelper db;
    int taskId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task);

        etTitulo = findViewById(R.id.etTitulo);
        etDescripcion = findViewById(R.id.etDescripcion);
        btnGuardar = findViewById(R.id.btnGuardar);
        db = new DBHelper(this);

        if (getIntent().hasExtra("id")) {
            taskId = getIntent().getIntExtra("id", -1);
            etTitulo.setText(getIntent().getStringExtra("titulo"));
            etDescripcion.setText(getIntent().getStringExtra("descripcion"));
            setTitle("Editar Tarea");
        } else {
            setTitle("Nueva Tarea");
        }

        btnGuardar.setOnClickListener(v -> {
            String titulo = etTitulo.getText().toString().trim();
            String descripcion = etDescripcion.getText().toString().trim();

            if (titulo.isEmpty()) {
                etTitulo.setError("El título es obligatorio");
                Toast.makeText(this, "El título es obligatorio", Toast.LENGTH_SHORT).show();
                return;
            }

            // Obtener fecha y hora actual
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            String fechaActual = sdf.format(new Date());

            if (taskId == -1) {
                long resultado = db.insertarTask(titulo, descripcion, fechaActual);
                if (resultado != -1) {
                    Toast.makeText(this, " Tarea creada", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, " Error al crear", Toast.LENGTH_SHORT).show();
                }
            } else {
                int resultado = db.actualizarTask(taskId, titulo, descripcion);
                if (resultado > 0) {
                    Toast.makeText(this, " Tarea actualizada", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, " Error al actualizar", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}