package com.example.to_dolist;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;

public class AddEditTaskActivity extends AppCompatActivity {

    private EditText etTitulo, etDescripcion;
    private Button btnGuardar;
    private ProgressBar progressBar;
    private Spinner spPrioridad, spCategoria;
    private FirebaseHelper firebaseHelper;
    private String taskId = null;
    private boolean isEditing = false;
    private boolean completado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task);

        etTitulo = findViewById(R.id.etTitulo);
        etDescripcion = findViewById(R.id.etDescripcion);
        btnGuardar = findViewById(R.id.btnGuardar);
        progressBar = findViewById(R.id.progressBar);
        spPrioridad = findViewById(R.id.spPrioridad);
        spCategoria = findViewById(R.id.spCategoria);

        firebaseHelper = new FirebaseHelper();

        // Configurar Spinners
        ArrayAdapter<CharSequence> adapterPrioridad = ArrayAdapter.createFromResource(this,
                R.array.prioridades, android.R.layout.simple_spinner_item);
        adapterPrioridad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPrioridad.setAdapter(adapterPrioridad);

        ArrayAdapter<CharSequence> adapterCategoria = ArrayAdapter.createFromResource(this,
                R.array.categorias, android.R.layout.simple_spinner_item);
        adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategoria.setAdapter(adapterCategoria);

        // Validación título
        etTitulo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString().trim();
                if (text.length() < 3 && text.length() > 0) {
                    etTitulo.setError("El título debe tener al menos 3 caracteres");
                } else {
                    etTitulo.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Verificar si es edición
        if (getIntent().hasExtra("id")) {
            isEditing = true;
            taskId = getIntent().getStringExtra("id");
            String titulo = getIntent().getStringExtra("titulo");
            String descripcion = getIntent().getStringExtra("descripcion");
            completado = getIntent().getBooleanExtra("completado", false);
            String prioridad = getIntent().getStringExtra("prioridad");
            String categoria = getIntent().getStringExtra("categoria");

            etTitulo.setText(titulo);
            etDescripcion.setText(descripcion);

            // Seleccionar prioridad y categoría guardadas
            if (prioridad != null) {
                int posPrioridad = ((ArrayAdapter) spPrioridad.getAdapter()).getPosition(prioridad);
                if (posPrioridad >= 0) spPrioridad.setSelection(posPrioridad);
            }
            if (categoria != null) {
                int posCategoria = ((ArrayAdapter) spCategoria.getAdapter()).getPosition(categoria);
                if (posCategoria >= 0) spCategoria.setSelection(posCategoria);
            }

            setTitle("Editar Tarea");
        } else {
            setTitle("Nueva Tarea");
        }

        btnGuardar.setOnClickListener(v -> guardarTarea());
    }

    private void guardarTarea() {
        String titulo = etTitulo.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String prioridad = spPrioridad.getSelectedItem().toString();
        String categoria = spCategoria.getSelectedItem().toString();

        // Validaciones
        if (titulo.isEmpty()) {
            etTitulo.setError("El título es obligatorio");
            Toast.makeText(this, "El título es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        if (titulo.length() < 3) {
            etTitulo.setError("El título debe tener al menos 3 caracteres");
            Toast.makeText(this, "El título debe tener al menos 3 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        btnGuardar.setEnabled(false);
        btnGuardar.setText("Guardando...");
        progressBar.setVisibility(android.view.View.VISIBLE);

        if (isEditing) {
            Task task = new Task();
            task.setId(taskId);
            task.setTitulo(titulo);
            task.setDescripcion(descripcion);
            task.setCompletado(completado);
            task.setFecha(new Date());
            task.setPrioridad(prioridad);   // ← NUEVO
            task.setCategoria(categoria);   // ← NUEVO

            firebaseHelper.actualizarTarea(task, new FirebaseHelper.OnTaskUpdatedListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(AddEditTaskActivity.this, "✓ Tarea actualizada", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(AddEditTaskActivity.this, "❌ Error: " + error, Toast.LENGTH_SHORT).show();
                    btnGuardar.setEnabled(true);
                    btnGuardar.setText("Guardar Tarea");
                    progressBar.setVisibility(android.view.View.GONE);
                }
            });
        } else {
            Task task = new Task(titulo, descripcion, false, new Date(), prioridad, categoria);

            firebaseHelper.agregarTarea(task, new FirebaseHelper.OnTaskAddedListener() {
                @Override
                public void onSuccess(String documentId) {
                    Toast.makeText(AddEditTaskActivity.this, "✓ Tarea creada", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(AddEditTaskActivity.this, "❌ Error: " + error, Toast.LENGTH_SHORT).show();
                    btnGuardar.setEnabled(true);
                    btnGuardar.setText("Guardar Tarea");
                    progressBar.setVisibility(android.view.View.GONE);
                }
            });
        }
    }
}