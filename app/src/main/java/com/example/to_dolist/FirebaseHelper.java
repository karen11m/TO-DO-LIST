package com.example.to_dolist;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseHelper {
    private static final String TAG = "FirebaseHelper";
    private static final String COLLECTION_NAME = "tareas";
    private FirebaseFirestore firestore;
    private CollectionReference collectionRef;

    public interface OnTasksLoadedListener {
        void onTasksLoaded(List<Task> tasks);
        void onError(String error);
    }

    public interface OnTaskAddedListener {
        void onSuccess(String documentId);
        void onError(String error);
    }

    public interface OnTaskUpdatedListener {
        void onSuccess();
        void onError(String error);
    }

    public interface OnTaskDeletedListener {
        void onSuccess();
        void onError(String error);
    }

    public FirebaseHelper() {
        this.firestore = FirebaseFirestore.getInstance();
        this.collectionRef = firestore.collection(COLLECTION_NAME);
    }

    // CREATE: Agregar tarea
    public void agregarTarea(Task task, OnTaskAddedListener listener) {
        Map<String, Object> taskMap = new HashMap<>();
        taskMap.put("titulo", task.getTitulo());
        taskMap.put("descripcion", task.getDescripcion());
        taskMap.put("completado", task.isCompletado());
        taskMap.put("fecha", task.getFecha() != null ? task.getFecha() : new Date());
        taskMap.put("prioridad", task.getPrioridad());
        taskMap.put("categoria", task.getCategoria());

        collectionRef.add(taskMap)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Document added with ID: " + documentReference.getId());
                    task.setId(documentReference.getId());
                    listener.onSuccess(documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding document", e);
                    listener.onError(e.getMessage());
                });
    }

    // READ: Obtener todas las tareas en tiempo real (retorna ListenerRegistration)
    public ListenerRegistration obtenerTareasEnTiempoReal(OnTasksLoadedListener listener) {
        return collectionRef.orderBy("fecha").addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e(TAG, "Error listening for tasks", error);
                listener.onError(error.getMessage());
                return;
            }

            if (value == null || value.isEmpty()) {
                listener.onTasksLoaded(new ArrayList<>());
                return;
            }

            List<Task> tasks = new ArrayList<>();
            for (DocumentSnapshot document : value.getDocuments()) {
                Task task = document.toObject(Task.class);
                if (task != null) {
                    task.setId(document.getId());
                    tasks.add(task);
                }
            }
            listener.onTasksLoaded(tasks);
        });
    }

    // UPDATE: Actualizar tarea
    public void actualizarTarea(Task task, OnTaskUpdatedListener listener) {
        if (task.getId() == null || task.getId().isEmpty()) {
            listener.onError("El ID de la tarea es inválido");
            return;
        }

        Map<String, Object> taskMap = new HashMap<>();
        taskMap.put("titulo", task.getTitulo());
        taskMap.put("descripcion", task.getDescripcion());
        taskMap.put("completado", task.isCompletado());
        taskMap.put("prioridad", task.getPrioridad());
        taskMap.put("categoria", task.getCategoria());

        collectionRef.document(task.getId())
                .update(taskMap)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Document updated: " + task.getId());
                    listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating document", e);
                    listener.onError(e.getMessage());
                });
    }

    // DELETE: Eliminar tarea
    public void eliminarTarea(String documentId, OnTaskDeletedListener listener) {
        if (documentId == null || documentId.isEmpty()) {
            listener.onError("El ID de la tarea es inválido");
            return;
        }

        collectionRef.document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Document deleted: " + documentId);
                    listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting document", e);
                    listener.onError(e.getMessage());
                });
    }
}