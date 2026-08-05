package com.example.to_dolist;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Date;

public class AddEditTaskActivity extends AppCompatActivity {

    private TextInputLayout tilTitulo, tilDescripcion;
    private EditText etTitulo, etDescripcion;
    private MaterialButton btnGuardar;
    private ProgressBar progressBar;
    private Spinner spPrioridad, spCategoria;
    private FirebaseHelper firebaseHelper;

    private String taskId = null;
    private boolean isEditing = false;
    private boolean completado = false;
    private boolean guardando = false; // previene doble envío

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task);

        inicializarVistas();
        configurarSpinners();
        configurarValidacionEnTiempoReal();
        cargarDatosSiEsEdicion();

        btnGuardar.setOnClickListener(v -> intentarGuardar());
    }

    private void inicializarVistas() {
        tilTitulo = findViewById(R.id.tilTitulo);
        tilDescripcion = findViewById(R.id.tilDescripcion);
        etTitulo = findViewById(R.id.etTitulo);
        etDescripcion = findViewById(R.id.etDescripcion);
        btnGuardar = findViewById(R.id.btnGuardar);
        progressBar = findViewById(R.id.progressBar);
        spPrioridad = findViewById(R.id.spPrioridad);
        spCategoria = findViewById(R.id.spCategoria);
        firebaseHelper = new FirebaseHelper();

        ImageButton btnAtras = findViewById(R.id.btnAtras);
        btnAtras.setOnClickListener(v -> finish());
    }

    private void configurarSpinners() {
        ArrayAdapter<CharSequence> adapterPrioridad = ArrayAdapter.createFromResource(this,
                R.array.prioridades, android.R.layout.simple_spinner_item);
        adapterPrioridad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPrioridad.setAdapter(adapterPrioridad);

        ArrayAdapter<CharSequence> adapterCategoria = ArrayAdapter.createFromResource(this,
                R.array.categorias, android.R.layout.simple_spinner_item);
        adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategoria.setAdapter(adapterCategoria);
    }

    // Validaciones de negocio en tiempo real (TextWatcher)
    private void configurarValidacionEnTiempoReal() {
        etTitulo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String texto = s.toString().trim();
                if (texto.length() > 0 && texto.length() < 3) {
                    tilTitulo.setError("El título debe tener al menos 3 caracteres");
                } else {
                    tilTitulo.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        etDescripcion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String texto = s.toString().trim();
                if (texto.length() > 0 && texto.length() < 5) {
                    tilDescripcion.setError("La descripción es muy corta, agrega más detalle");
                } else {
                    tilDescripcion.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void cargarDatosSiEsEdicion() {
        if (!getIntent().hasExtra("id")) {
            setTitle("Nueva Tarea");
            return;
        }

        isEditing = true;
        taskId = getIntent().getStringExtra("id");
        String titulo = getIntent().getStringExtra("titulo");
        String descripcion = getIntent().getStringExtra("descripcion");
        completado = getIntent().getBooleanExtra("completado", false);
        String prioridad = getIntent().getStringExtra("prioridad");
        String categoria = getIntent().getStringExtra("categoria");

        etTitulo.setText(titulo);
        etDescripcion.setText(descripcion);

        if (prioridad != null) {
            int posPrioridad = ((ArrayAdapter) spPrioridad.getAdapter()).getPosition(prioridad);
            if (posPrioridad >= 0) spPrioridad.setSelection(posPrioridad);
        }
        if (categoria != null) {
            int posCategoria = ((ArrayAdapter) spCategoria.getAdapter()).getPosition(categoria);
            if (posCategoria >= 0) spCategoria.setSelection(posCategoria);
        }

        setTitle("Editar Tarea");
    }

    private void intentarGuardar() {
        if (guardando) return; // previene doble envío

        String titulo = etTitulo.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();

        if (!validarCampos(titulo, descripcion)) return;

        String prioridad = spPrioridad.getSelectedItem().toString();
        String categoria = spCategoria.getSelectedItem().toString();

        setGuardando(true);

        if (isEditing) {
            actualizarTareaExistente(titulo, descripcion, prioridad, categoria);
        } else {
            crearNuevaTarea(titulo, descripcion, prioridad, categoria);
        }
    }

    // Ningún campo crítico puede enviarse vacío o con datos ilógicos
    private boolean validarCampos(String titulo, String descripcion) {
        boolean valido = true;

        tilTitulo.setError(null);
        tilDescripcion.setError(null);

        if (TextUtils.isEmpty(titulo)) {
            tilTitulo.setError("El título es obligatorio");
            valido = false;
        } else if (titulo.length() < 3) {
            tilTitulo.setError("El título debe tener al menos 3 caracteres");
            valido = false;
        }

        if (TextUtils.isEmpty(descripcion)) {
            tilDescripcion.setError("La descripción es obligatoria");
            valido = false;
        } else if (descripcion.length() < 5) {
            tilDescripcion.setError("La descripción es muy corta");
            valido = false;
        }

        return valido;
    }

    private void crearNuevaTarea(String titulo, String descripcion, String prioridad, String categoria) {
        Task task = new Task(titulo, descripcion, false, new Date(), prioridad, categoria);

        firebaseHelper.agregarTarea(task, new FirebaseHelper.OnTaskAddedListener() {
            @Override
            public void onSuccess(String documentId) {
                Toast.makeText(AddEditTaskActivity.this, " Tarea creada", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String error) {
                manejarErrorGuardado(error);
            }
        });
    }

    private void actualizarTareaExistente(String titulo, String descripcion, String prioridad, String categoria) {
        Task task = new Task();
        task.setId(taskId);
        task.setTitulo(titulo);
        task.setDescripcion(descripcion);
        task.setCompletado(completado);
        task.setFecha(new Date());
        task.setPrioridad(prioridad);
        task.setCategoria(categoria);

        firebaseHelper.actualizarTarea(task, new FirebaseHelper.OnTaskUpdatedListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(AddEditTaskActivity.this, "✓ Tarea actualizada", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String error) {
                manejarErrorGuardado(error);
            }
        });
    }

    private void manejarErrorGuardado(String error) {
        Toast.makeText(AddEditTaskActivity.this, " Error: " + error, Toast.LENGTH_SHORT).show();
        setGuardando(false);
    }

    private void setGuardando(boolean guardando) {
        this.guardando = guardando;
        btnGuardar.setEnabled(!guardando);
        btnGuardar.setText(guardando ? "Guardando..." : "GUARDAR TAREA");
        progressBar.setVisibility(guardando ? View.VISIBLE : View.GONE);
    }
}